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




/**
 * A Thread-Local that ensures correct handling of proxied requests.
 */
public final class ProxyDelegator {

    private static final ThreadLocal<Boolean> IS_DELEGATED = new ThreadLocal<>();
    private static final ThreadLocal<ProxyServiceIdentifier> SERVICE_IDENTIFIER = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> HAS_SERVICE_IDENTIFIER = new ThreadLocal<>();


    public static void toProxy() {
	IS_DELEGATED.set(true);
    }

    public static void toProxy(String method, String... parameters) {
	serviceIdentifier(method, parameters);
	toProxy();
    }
    
    public static void serviceIdentifier(String method, String... parameters) {
	SERVICE_IDENTIFIER.set(new ProxyServiceIdentifier(method, parameters));
	HAS_SERVICE_IDENTIFIER.set(true);
    }

    public static boolean isDelegateToProxy() {
	Boolean isDelegated = IS_DELEGATED.get();
	return isDelegated != null ? isDelegated : false;
    }
    
    public static boolean hasServiceIdentifier() {
	Boolean isIdentified = HAS_SERVICE_IDENTIFIER.get();
	return isIdentified != null ? isIdentified : false;
    }
    
    public static ProxyServiceIdentifier getServiceIdentifier() {
	return SERVICE_IDENTIFIER.get();
    }

    public static void reset() {
	IS_DELEGATED.remove();
    }
    
    public static void restart() {
	reset();
	SERVICE_IDENTIFIER.remove();
	HAS_SERVICE_IDENTIFIER.remove();
    }

}
