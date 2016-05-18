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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import soapmocks.api.Constants;
import soapmocks.generic.logging.Log;
import soapmocks.generic.logging.LogFactory;

public final class StaticFileConfig {

    private static final Log LOG = LogFactory.create(StaticFileConfig.class);

    static final Map<String, List<Properties>> URL_TO_FILE_MAPPING = new HashMap<String, List<Properties>>();

    private static final String GENERIC_SOAP_DIR = File.separatorChar + "static-files";

    public static void initWithRuntimeException() {
	try {
	    Collection<File> configFiles = findConfigFilesInGenericSoapMocks();
	    for (File configFile : configFiles) {
		Properties config = new Properties();
		config.load(new FileInputStream(configFile));
		configure(configFile.getName(), config);
	    }
	} catch (IOException | URISyntaxException e) {
	    throw new RuntimeException(e);
	}
    }

    private static Collection<File> findConfigFilesInGenericSoapMocks()
	    throws URISyntaxException {
	String staticFilesDir = filesBaseDir();
	if (staticFilesDir == null) {
	    URL genericSoapDirResource = StaticFileConfig.class
		    .getResource(GENERIC_SOAP_DIR);
	    if (genericSoapDirResource == null) {
		LOG.out("No generic soap files found.");
		return Collections.emptyList();
	    }
	}
	File genericSoapDirFile = new File(staticFilesDir);
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
	if (urlFiles == null || urlFiles.isEmpty()) {
	    LOG.out("No generic soap files found.");
	    return Collections.emptyList();
	}
	return urlFiles;
    }

    private static String filesBaseDir() {
	String filesBaseDir = System
		.getProperty(Constants.SOAPMOCKS_FILES_BASEDIR_SYSTEM_PROP);
	return filesBaseDir;
    }

    private static void configure(String config, Properties file) {
	String url = url(file);
	String responseFile = responseFile(file);
	LOG.outNoId("#### Static file mock " + config + " with url " + url
		+ " and resp-file " + new File(responseFile).getName() + "\n");
	String completeUrl = ContextPath.SOAP_MOCKS_CONTEXT + url;
	if (!URL_TO_FILE_MAPPING.containsKey(completeUrl)) {
	    List<Properties> properties = new ArrayList<Properties>();
	    URL_TO_FILE_MAPPING.put(completeUrl, properties);
	}
	URL_TO_FILE_MAPPING.get(completeUrl).add(file);
    }

    static String responseFile(Properties file) {
	return filesBaseDir() + file.getProperty("responseFile");
    }

    private static String url(Properties file) {
	return file.getProperty("url");
    }
}
