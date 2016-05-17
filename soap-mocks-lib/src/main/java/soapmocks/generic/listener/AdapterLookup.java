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
package soapmocks.generic.listener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import soapmocks.api.SoapMockUrl;

import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.transport.http.DeploymentDescriptorParser;
import com.sun.xml.ws.transport.http.ResourceLoader;

final class AdapterLookup<A> extends DeploymentDescriptorParser<A> {

    private String SUN_JAXWS_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
	    + "<endpoints xmlns=\"http://java.sun.com/xml/ns/jax-ws/ri/runtime\" "
	    + "version=\"2.0\">\n" + "--ENDPOINTS--" + "</endpoints>\n";

    private String SUN_JAXWS_XML_ENDPOINT_TEMPLATE = "   <endpoint name=\"--NAME--\" implementation=\"--IMPL--\" "
	    + "url-pattern=\"--URLPATTERN--\" />";

    public AdapterLookup(ClassLoader cl, ResourceLoader loader,
	    Container container, AdapterFactory<A> adapterFactory)
	    throws MalformedURLException {
	super(cl, loader, container, adapterFactory);
    }

    @Override
    public List<A> parse(String systemId, InputStream is) {
	Reflections reflections = new Reflections("soapmocks.services");
	Set<Class<?>> annotated = reflections
		.getTypesAnnotatedWith(SoapMockUrl.class);
	StringBuilder endpointString = new StringBuilder();
	String sunJaxWs = findServicesAndCreateSytheticSunJaxWsXml(annotated, endpointString);
	return super.parse(systemId, new ByteArrayInputStream(
		sunJaxWs.getBytes()));
    }

    private String findServicesAndCreateSytheticSunJaxWsXml(Set<Class<?>> annotated,
	    StringBuilder endpointString) {
	for (Class<?> serviceClass : annotated) {
	    String urlPattern = serviceClass.getAnnotation(SoapMockUrl.class)
		    .value();
	    Service service = new Service();
	    service.implementation = serviceClass.getName();
	    service.urlPattern = urlPattern;
	    service.name = serviceClass.getSimpleName();
	    endpointString.append(
		    SUN_JAXWS_XML_ENDPOINT_TEMPLATE
			    .replaceAll("--NAME--", service.name)
			    .replaceAll("--IMPL--", service.implementation)
			    .replaceAll("--URLPATTERN--", service.urlPattern))
		    .append("\n");
	}
	String sunJaxws = SUN_JAXWS_XML.replaceAll("--ENDPOINTS--",
		endpointString.toString());
	return sunJaxws;
    }

    private static final class Service {
	String urlPattern;
	String implementation;
	String name;
    }
}
