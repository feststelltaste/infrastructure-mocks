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
package soapmocks.services;

import javax.jws.WebService;

import soapmocks.api.ContextPath;
import soapmocks.api.ProxyDelegator;
import soapmocks.api.RequestIdentifier;
import soapmocks.api.Response;
import soapmocks.api.ResponseIdentifier;
import soapmocks.generated.helloservice.HelloWorld;

@WebService(endpointInterface = "soapmocks.generated.helloservice.HelloWorld")
@ContextPath("/WebService/services/HelloWorld")
public class HelloWorldServiceMock implements HelloWorld {

    Response responseCreator = new Response("/jaxws");

    @Override
    public String sayHello(String name) {
	final String result = anotherMethod(name);
	return result;
    }

    private String anotherMethod(String name) {
	final String result = responseCreator.using(String.class,
		ResponseIdentifier.with().elementHashExcludes("uga")
			.elementResponse("sayHelloReturn").build(),
		RequestIdentifier.by(name));
	return result;
    }

    @Override
    public String sayHello2(String name) {
	ProxyDelegator.toProxy();
	return null;
    }
}
