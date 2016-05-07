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
package soapmocks.util;

/**
 * A Thread-Local that ensures correct handling of proxied requests.
 */
public final class ProxyDelegator {

    private static final ThreadLocal<Boolean> IS_DELEGATED = new ThreadLocal<>();
    private static final ThreadLocal<ServiceIdentifier> SERVICE_IDENTIFIER = new ThreadLocal<>();


    public static void toProxy() {
	IS_DELEGATED.set(true);
    }

    public static void serviceIdentifier(String method, String... parameters) {
	SERVICE_IDENTIFIER.set(new ServiceIdentifier(method, parameters));
    }

    public static boolean isDelegateToProxy() {
	Boolean isDelegated = IS_DELEGATED.get();
	return isDelegated != null ? isDelegated : false;
    }
    
    public static boolean hasServiceIdentifier() {
	ServiceIdentifier isIdentified = SERVICE_IDENTIFIER.get();
	return isIdentified != null;
    }
    
    public static ServiceIdentifier getServiceIdentifier() {
	return SERVICE_IDENTIFIER.get();
    }

    public static void reset() {
	IS_DELEGATED.remove();
	SERVICE_IDENTIFIER.remove();
    }

}
