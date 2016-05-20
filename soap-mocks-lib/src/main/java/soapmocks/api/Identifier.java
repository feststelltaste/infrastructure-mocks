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

/**
 * An object to identify a service request by method and some parameters. This
 * identifier will generate a filename by given parameters. Example:
 * Identifier.with("doSomething", "blah", "suelz"); will try to find a file
 * named doSomething-blah-suelz.xml
 */
public final class Identifier {

    private final String method;
    private final String[] parameters;

    private Identifier(String method, String... parameters) {
	this.method = method;
	this.parameters = parameters;
    }

    /**
     * @param method
     *            The method of the webservice
     * @param parameters
     *            Parameter strings from request to identify matching response
     * @return Identifier object
     */
    public static Identifier with(String method, String parameters) {
	return new Identifier(method, parameters);
    }

    public String getMethod() {
	return method;
    }

    public String[] getParameters() {
	return parameters;
    }

}
