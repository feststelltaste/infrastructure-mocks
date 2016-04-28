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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import soapmocks.generic.Constants;
import soapmocks.util.ProxyDelegator;

public class ProxyHandler {

    private static final GenericProxyCounter COUNTER = new GenericProxyCounter();
    private static final String PROXY_FILE = "/proxy.properties";
    private final Properties proxies = new Properties();

    public ProxyHandler() throws IOException {
	proxies.load(getClass().getResourceAsStream(PROXY_FILE));
    }

    public void doPost(String uri, HttpServletRequest req,
	    HttpServletResponse resp) throws IOException {
	ProxyDelegator.reset();
	long count = COUNTER.incrementAndGet();

	byte[] requestString = IOUtils.toByteArray(req.getInputStream());

	System.out.println("REQ-" + count + ": " + new String(requestString));

	ProxyResult proxyResult = sendPost(proxyUrl(uri),
		mapHeaderFromRequest(req), requestString);

	System.out.println("RESP-" + count + ": "
		+ new String(proxyResult.body));

	copyHeaderToResponse(resp, proxyResult);
	resp.setStatus(proxyResult.responseCode);
	IOUtils.write(proxyResult.body, resp.getOutputStream());
    }

    public boolean isProxy(String uri) {
	Set<Object> keySet = proxies.keySet();
	for (Object key : keySet) {
	    if (uri.contains((String) key)) {
		return true;
	    }
	}
	return false;
    }

    private void copyHeaderToResponse(HttpServletResponse resp,
	    ProxyResult proxyResult) {
	for (Entry<String, List<String>> header : proxyResult.header.entrySet()) {
	    List<String> headerEntryList = header.getValue();
	    for (String headerEntry : headerEntryList) {
		if (header.getKey() != null
			&& !header.getKey().equalsIgnoreCase("null")) {
		    resp.setHeader(header.getKey(), headerEntry);
		}
	    }
	}
    }

    private Map<String, String> mapHeaderFromRequest(HttpServletRequest req) {
	Map<String, String> headers = new HashMap<>();
	Enumeration<String> headerNames = req.getHeaderNames();
	while (headerNames.hasMoreElements()) {
	    String headerName = headerNames.nextElement();
	    String header = req.getHeader(headerName);
	    headers.put(headerName, header);
	}
	return headers;
    }

    private String proxyUrl(String uri) {
	Set<Entry<Object, Object>> entrySet = proxies.entrySet();
	for (Entry<Object, Object> entry : entrySet) {
	    if (uri.contains((String) entry.getKey())) {
		String value = (String) entry.getValue();
		value.indexOf(uri);
		String uriPart = uri.substring(uri
			.indexOf(Constants.SOAP_MOCKS_CONTEXT)
			+ Constants.SOAP_MOCKS_CONTEXT.length());
		String proxyUrl = value + uriPart;
		System.out.println("ProxyUrl: " + proxyUrl);
		return proxyUrl;
	    }
	}
	throw new RuntimeException("Proxy URL not found");
    }

    private ProxyResult sendPost(String url, Map<String, String> reqHeader,
	    byte[] body) {
	try {
	    URL obj = new URL(url);
	    HttpURLConnection connection = (HttpURLConnection) obj
		    .openConnection();
	    connection.setDoOutput(true);
	    connection.setRequestMethod("POST");
	    for (Entry<String, String> header : reqHeader.entrySet()) {
		connection.setRequestProperty(header.getKey(),
			header.getValue());
	    }
	    OutputStream outputStream = connection.getOutputStream();
	    IOUtils.write(body, outputStream);
	    outputStream.flush();
	    outputStream.close();
	    int responseCode = connection.getResponseCode();

	    byte[] response = IOUtils.toByteArray(connection.getInputStream());

	    ProxyResult proxyResult = new ProxyResult();
	    proxyResult.responseCode = responseCode;
	    proxyResult.body = response;
	    proxyResult.header = connection.getHeaderFields();
	    return proxyResult;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private static class ProxyResult {
	int responseCode;
	Map<String, List<String>> header;
	byte[] body;
    }
}
