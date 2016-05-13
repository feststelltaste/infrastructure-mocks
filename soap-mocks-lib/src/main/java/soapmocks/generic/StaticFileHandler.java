/*
Copyright 2016 Peter Bilstein

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package soapmocks.generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import soapmocks.Constants;
import soapmocks.generic.logging.LogFactory;
import soapmocks.generic.logging.Log;
import soapmocks.io.FileUtils;
import soapmocks.io.IOUtils;
import soapmocks.io.filefilter.IOFileFilter;

final class StaticFileHandler {

    private static final Log LOG = LogFactory
	    .create(StaticFileHandler.class);
    private static final String GENERIC_SOAP_DIR = "/generic_soap_mocks/";
    private Map<String, List<Properties>> URL_TO_FILE_MAPPING = new HashMap<String, List<Properties>>();

    StaticFileHandler() throws IOException, URISyntaxException {
	Collection<File> configFiles = findConfigFilesInGenericSoapMocks();
	for (File configFile : configFiles) {
	    Properties config = new Properties();
	    config.load(new FileInputStream(configFile));
	    configure(configFile.getName(), config);
	}
    }

    boolean containsKey(String uri) {
	return URL_TO_FILE_MAPPING.containsKey(uri);
    }

    private Collection<File> findConfigFilesInGenericSoapMocks()
	    throws URISyntaxException {
	URL genericSoapDirResource = getClass().getResource(GENERIC_SOAP_DIR);
	if (genericSoapDirResource == null) {
	    LOG.out("No generic soap files found.");
	    return Collections.emptyList();
	}
	File genericSoapDirFile = new File(genericSoapDirResource.toURI());
	Collection<File> urlFiles = FileUtils.listFiles(genericSoapDirFile,
		new IOFileFilter() {
		    @Override
		    public boolean accept(File arg0, String arg1) {
			return arg0.getName().endsWith(".config");
		    }

		    @Override
		    public boolean accept(File arg0) {
			return arg0.getName().endsWith(".config");
		    }
		}, new IOFileFilter() {
		    @Override
		    public boolean accept(File arg0, String arg1) {
			return true;
		    }

		    @Override
		    public boolean accept(File arg0) {
			return true;
		    }
		});
	return urlFiles;
    }

    GenericSoapResponse findResponseByPropertiesAndRequest(
	    HttpServletRequest hsr, String uri) throws IOException {
	List<Properties> propertiesList = URL_TO_FILE_MAPPING.get(uri);
	String request = null;
	for (Properties properties : propertiesList) {
	    if (hasAnyRequestCondition(properties)) {
		request = createRequestStringIfNeeded(hsr, request);
		boolean conditionMet = checkConditionMet(request, properties);
		if (conditionMet) {
		    String responseFile = responseFile(properties);
		    LOG.out("Generic conditional ResponseFile: " + responseFile);
		    return new GenericSoapResponse(getClass()
			    .getResourceAsStream(responseFile), null);
		}
	    }
	}
	for (Properties properties : propertiesList) {
	    if (!hasRequestContainsCondition(properties)) {
		request = createRequestStringIfNeeded(hsr, request);
		String responseFile = responseFile(properties);
		LOG.out("Generic default ResponseFile: " + responseFile);
		return new GenericSoapResponse(getClass().getResourceAsStream(
			responseFile), null);
	    }
	}
	LOG.out("No condition met and no default response found for url " + uri);
	LOG.out("Request was:\n" + createRequestStringIfNeeded(hsr, request));
	return null;
    }

    private String createRequestStringIfNeeded(HttpServletRequest hsr,
	    String request) throws IOException {
	if (request == null) {
	    request = IOUtils.toString(hsr.getInputStream(),
		    Charset.forName("UTF-8"));
	}
	return request;
    }

    private boolean checkConditionMet(String request, Properties properties) {
	boolean conditionMet = true;
	if (hasRequestContainsCondition(properties)) {
	    String[] requestContains = requestContainsCondition(properties);
	    for (String condition : requestContains) {
		if (!request.contains(condition)) {
		    conditionMet = false;
		}
	    }
	}
	if (hasRequestContainsNotCondition(properties)) {
	    String[] requestContainsNot = requestContainsNotCondition(properties);
	    for (String condition : requestContainsNot) {
		if (request.contains(condition)) {
		    conditionMet = false;
		}
	    }
	}
	return conditionMet;
    }

    private void configure(String config, Properties file) {
	String url = url(file);
	String responseFile = responseFile(file);
	LOG.outNoId("#### GenericSoap Mock " + config + "\n#### " + url
		+ "\n#### " + responseFile + "\n");
	String completeUrl = Constants.SOAP_MOCKS_CONTEXT + url;
	if (!URL_TO_FILE_MAPPING.containsKey(completeUrl)) {
	    List<Properties> properties = new ArrayList<Properties>();
	    URL_TO_FILE_MAPPING.put(completeUrl, properties);
	}
	URL_TO_FILE_MAPPING.get(completeUrl).add(file);
    }

    private String responseFile(Properties file) {
	return GENERIC_SOAP_DIR + file.getProperty("responseFile");
    }

    private String url(Properties file) {
	return file.getProperty("url");
    }

    private String[] requestContainsCondition(Properties file) {
	return file.getProperty("requestContains").split(" ");
    }

    private String[] requestContainsNotCondition(Properties file) {
	return file.getProperty("requestContainsNot").split(" ");
    }

    private boolean hasRequestContainsCondition(Properties file) {
	return file.getProperty("requestContains") != null;
    }

    private boolean hasRequestContainsNotCondition(Properties file) {
	return file.getProperty("requestContainsNot") != null;
    }

    private boolean hasAnyRequestCondition(Properties properties) {
	return hasRequestContainsCondition(properties)
		|| hasRequestContainsNotCondition(properties);
    }

}
