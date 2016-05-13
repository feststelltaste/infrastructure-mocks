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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import soapmocks.api.ProxyDelegator;

public final class ProxyHandler {

    private ProxyUrl proxyUrl;

    public ProxyHandler() throws IOException {
	proxyUrl = new ProxyUrl();
    }

    /**
     * @return Time in millis that the proxy call took
     */
    public long doPost(String uri, HttpServletRequest req,
	    HttpServletResponse resp) throws IOException {
	ProxyDelegator.reset();
	return new ProxyPostHandler(proxyUrl).doPostInternal(uri, req, resp);
    }
    
    public boolean isProxy(String uri) {
	return proxyUrl.isProxy(uri);
    }

}
