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

public class ProxyDelegator {

    private static final ThreadLocal<Boolean> local = new ThreadLocal<>();

    public static void toProxy() {
	local.set(true);
    }

    public static boolean isDelegateToProxy() {
	Boolean isDelegated = local.get();
	return isDelegated != null ? isDelegated : false;
    }

    public static void reset() {
	local.remove();
    }

}
