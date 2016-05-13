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

import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

final class ProxyResponseHeaderCopier {

    void copyHeaderToResponse(HttpServletResponse resp,
	    ProxyResult proxyResult) {
	for (Entry<String, List<String>> header : proxyResult.header.entrySet()) {
	    copyOneHeaderList(resp, header);
	}
    }

    private void copyOneHeaderList(HttpServletResponse resp,
	    Entry<String, List<String>> header) {
	List<String> headerEntryList = header.getValue();
	for (String headerEntry : headerEntryList) {
	    copyOneHeaderEntryIfNotNull(resp, header, headerEntry);
	}
    }

    private void copyOneHeaderEntryIfNotNull(HttpServletResponse resp,
	    Entry<String, List<String>> header, String headerEntry) {
	if (header.getKey() != null
	    && !header.getKey().equalsIgnoreCase("null")) {
	resp.setHeader(header.getKey(), headerEntry);
	}
    }
    
}

