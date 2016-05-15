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
package soapmocks.generic.proxy;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import soapmocks.Constants;
import soapmocks.generic.logging.LogFactory;
import soapmocks.generic.logging.Log;

final class ProxyUrl {
    
    private static final Log LOG = LogFactory.create(ProxyUrl.class);

    private static final String PROXY_FILE = "/proxy.properties";
    private final Properties proxies = new Properties();
    
    ProxyUrl() {
	initwithRuntimeException();
    }

    private void initwithRuntimeException() {
	try {
	    proxies.load(getClass().getResourceAsStream(PROXY_FILE));
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    String proxyUrl(String uri) {
	Set<Entry<Object, Object>> entrySet = proxies.entrySet();
	for (Entry<Object, Object> entry : entrySet) {
	    String key = (String) entry.getKey();
	    if (uri.contains(key)) {
		return extractProxyUri(uri, key);
	    }
	}
	throw new RuntimeException("Proxy URL not found for " + uri);
    }

    boolean isProxy(String uri) {
	Set<Object> keySet = proxies.keySet();
	for (Object key : keySet) {
	    if (uri.contains((String) key)) {
		return true;
	    }
	}
	return false;
    }

    private String extractProxyUri(String uri, String key) {
	key.indexOf(uri);
	String uriPart = uri.substring(uri
		.indexOf(Constants.SOAP_MOCKS_CONTEXT)
		+ Constants.SOAP_MOCKS_CONTEXT.length());
	String proxyUrl = proxies.getProperty(key) + uriPart;
	LOG.out("ProxyUrl: " + proxyUrl);
	return proxyUrl;
    }
}
