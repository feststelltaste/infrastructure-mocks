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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;

import soapmocks.generic.logging.Log;
import soapmocks.generic.logging.LogFactory;

final class StaticFileHandler {

    private static final Log LOG = LogFactory
	    .create(StaticFileHandler.class);
    

    boolean containsKey(String uri) {
	return StaticFileConfig.URL_TO_FILE_MAPPING.containsKey(uri);
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


    GenericSoapResponse findResponseByPropertiesAndRequest(
	    HttpServletRequest hsr, String uri) throws IOException {
	List<Properties> propertiesList = StaticFileConfig.URL_TO_FILE_MAPPING.get(uri);
	String request = null;
	for (Properties properties : propertiesList) {
	    if (hasAnyRequestCondition(properties)) {
		request = createRequestStringIfNeeded(hsr, request);
		boolean conditionMet = checkConditionMet(request, properties);
		if (conditionMet) {
		    String responseFile = StaticFileConfig.responseFile(properties);
		    LOG.out("Generic conditional ResponseFile: " + responseFile);
		    return new GenericSoapResponse(getClass()
			    .getResourceAsStream(responseFile), null);
		}
	    }
	}
	for (Properties properties : propertiesList) {
	    if (!hasRequestContainsCondition(properties)) {
		request = createRequestStringIfNeeded(hsr, request);
		String responseFile = StaticFileConfig.responseFile(properties);
		LOG.out("Generic default ResponseFile: " + responseFile);
		return new GenericSoapResponse(getClass().getResourceAsStream(
			responseFile), null);
	    }
	}
	LOG.out("No condition met and no default response found for url " + uri);
	LOG.out("Request was:\n" + createRequestStringIfNeeded(hsr, request));
	return null;
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
