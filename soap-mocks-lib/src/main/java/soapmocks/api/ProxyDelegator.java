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
package soapmocks.api;

import soapmocks.generic.proxy.ProxyServiceIdentifier;


/**
 * A Thread-Local that ensures correct handling of proxy requests.
 */
public final class ProxyDelegator {

    private static final ThreadLocal<Boolean> IS_DELEGATED = new ThreadLocal<>();
    private static final ThreadLocal<ProxyServiceIdentifier> SERVICE_IDENTIFIER = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> HAS_SERVICE_IDENTIFIER = new ThreadLocal<>();


    /**
     * This enables the proxy delegation, if a proxy is available for the URI. This will skip
     * the current JaxWS service call and send to proxy.
     */
    public static void toProxy() {
	IS_DELEGATED.set(true);
    }

    /**
     * This enables the proxy delegation, if a proxy is available for the URI.
     * In addition it sets the service identifier used by Proxy Records.
     *
     * @param method The webservice method being called.
     * @param parameters parameters that identify a unique request.
     */
    public static void toProxy(String method, String... parameters) {
	serviceIdentifier(method, parameters);
	toProxy();
    }
    
    /**
     * Sets the service identifier used by proxy records.
     * 
     * @param method The webservice method being called.
     * @param parameters parameters that identify a unique request.
     */
    public static void serviceIdentifier(String method, String... parameters) {
	SERVICE_IDENTIFIER.set(new ProxyServiceIdentifier(method, parameters));
	HAS_SERVICE_IDENTIFIER.set(true);
    }

    /**
     * @return true, if proxy delegation was enabled during current REQ-RESP.
     */
    public static boolean isDelegateToProxy() {
	Boolean isDelegated = IS_DELEGATED.get();
	return isDelegated != null ? isDelegated : false;
    }
    
    /**
     * @return boolean, true if a service identifier has been set for proxy records.
     */
    public static boolean hasServiceIdentifier() {
	Boolean isIdentified = HAS_SERVICE_IDENTIFIER.get();
	return isIdentified != null ? isIdentified : false;
    }
    
    /** 
     * @return {@link ProxyServiceIdentifier}
     */
    public static ProxyServiceIdentifier getServiceIdentifier() {
	return SERVICE_IDENTIFIER.get();
    }

    /**
     * Reset the delegation.
     */
    public static void reset() {
	IS_DELEGATED.remove();
    }
    
    /**
     * Reset the delegation and remove the service identifier.
     */
    public static void restart() {
	reset();
	SERVICE_IDENTIFIER.remove();
	HAS_SERVICE_IDENTIFIER.remove();
    }

}
