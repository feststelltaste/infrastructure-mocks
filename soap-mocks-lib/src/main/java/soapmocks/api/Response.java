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

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import soapmocks.generic.logging.Log;
import soapmocks.generic.logging.LogFactory;

/**
 * An API class to create JaxWS response objects from XML files.
 */
public final class Response {

    private static final Log LOG = LogFactory.create(Response.class);

    private String baseDir = "";

    /**
     * Uses soapmocks.files.basedir to find files.
     */
    public Response() {
    }

    /**
     * @param baseDir
     *            for finding the files. Relative to soapmocks.files.basedir or
     *            classpath
     */
    public Response(String baseDir) {
	if (baseDir == null) {
	    throw new NullPointerException();
	}
	this.baseDir = baseDir;
    }

    /**
     * Create the response object using all information given. If nothing was
     * found for method and parameter, it will try to find a default file. If
     * this fails, proxy delegation jumps in if configured. As element for
     * response the classname of classForT starting with lower case will be
     * taken.
     * <p>
     * <p>
     * Supports Service Identifier, so when proxy delegation jumps in, it will
     * be able to create a record.
     * <p>
     * 
     * @param classForResponseType
     *            The type of the response object
     * @param Identifier
     *            Identifier to find matching response
     * @return RESPONSE_TYPE Object to return in WebService
     */
    public <RESPONSE_TYPE> RESPONSE_TYPE using(
	    Class<RESPONSE_TYPE> classForResponseType, Identifier identifier) {
	return using(classForResponseType, null, true, identifier);
    }

    /**
     * Create the response object using all information given. If nothing was
     * found for method and parameter, it will try to find a default file. If
     * this fails, proxy delegation jumps in if configured.
     * <p>
     * <p>
     * Supports Service Identifier, so when proxy delegation jumps in, it will
     * be able to create a record.
     * <p>
     * 
     * @param classForResponseType
     *            The type of the response object
     * @param elementResponse
     *            The element in the response file representing the response
     *            object
     * @param Identifier
     *            Identifier to find matching response
     * @return Object to return in WebService
     */
    public <RESPONSE_TYPE> RESPONSE_TYPE using(
	    Class<RESPONSE_TYPE> classForResponseType, String elementResponse,
	    Identifier requestIdentifier) {
	return using(classForResponseType, elementResponse, true,
		requestIdentifier);
    }

    /**
     * Create the response object using all information given. If nothing was
     * found for method and parameter, it will try to find a default file, when
     * defaultXml is true. If this fails, proxy delegation jumps in if
     * configured.
     * <p>
     * <p>
     * Supports Service Identifier, so when proxy delegation jumps in, it will
     * be able to create a record.
     * <p>
     * 
     * @param classForResponseType
     *            The type of the response object
     * @param elementResponse
     *            The element in the response file representing the response
     *            object
     * @param defaultXml
     *            true when a default xml shall be searched for
     * @param Identifier
     *            Identifier to find matching response
     * @return Object to return in WebService
     */
    public <RESPONSE_TYPE> RESPONSE_TYPE using(
	    Class<RESPONSE_TYPE> classForResponseType, String elementResponse,
	    boolean defaultXml, Identifier requestIdentifier) {
	ProxyDelegator.serviceIdentifier(requestIdentifier.getMethod(),
		requestIdentifier.getParameters());
	String filename = new ResponseCreatorFileFinder()
		.findFileFromMethodsAndParameter(baseDir, defaultXml,
			requestIdentifier.getMethod(),
			requestIdentifier.getParameters());
	if (filename == null) {
	    throw new ProxyDelegateQuietException("file not found");
	}
	return using(filename, elementResponse, classForResponseType);
    }

    public <RESPONSE_TYPE> RESPONSE_TYPE using(String xmlfile,
	    Class<RESPONSE_TYPE> classForT) {
	return using(xmlfile, null, classForT);
    }

    public <RESPONSE_TYPE> RESPONSE_TYPE using(String xmlfile,
	    String fromElement, Class<RESPONSE_TYPE> classForT) {
	if (fromElement == null) {
	    String simpleName = classForT.getSimpleName();
	    fromElement = Character.toLowerCase(simpleName.charAt(0))
		    + (simpleName.length() > 1 ? simpleName.substring(1) : "");
	}
	try {
	    xmlfile = baseDir + xmlfile;
	    XMLInputFactory xif = XMLInputFactory.newInstance();
	    InputStream fileInputStream = new ResponseCreatorFileFinder()
		    .getFile(xmlfile);
	    failIfStreamNotFound(xmlfile, fileInputStream);
	    StreamSource xml = new StreamSource(fileInputStream);
	    XMLStreamReader xsr = xif.createXMLStreamReader(xml);
	    boolean found = false;
	    while (xsr.hasNext()) {
		xsr.next();
		if (xsr.isStartElement()
			&& xsr.getLocalName().equals(fromElement)) {
		    found = true;
		    break;
		}
	    }
	    throwExceptionIfNotFound(xmlfile, fromElement, found);
	    JAXBContext jc;
	    jc = JAXBContext.newInstance(classForT);
	    Unmarshaller unmarshaller = jc.createUnmarshaller();
	    JAXBElement<RESPONSE_TYPE> jaxbElement = unmarshaller.unmarshal(
		    xsr, classForT);
	    xsr.close();
	    fileInputStream.close();
	    LOG.out("JaxWS ResponseFile: " + xmlfile);
	    return jaxbElement.getValue();
	} catch (Exception e) {
	    e.printStackTrace();
	    ProxyDelegator.toProxy();
	    throw new ProxyDelegateQuietException(e);
	}
    }

    private void throwExceptionIfNotFound(String xmlfile, String fromElement,
	    boolean found) {
	if (!found) {
	    throw new ProxyDelegateQuietException(fromElement
		    + " element not found in " + xmlfile);
	}
    }

    private void failIfStreamNotFound(String file, InputStream fileInputStream)
	    throws FileNotFoundException {
	if (fileInputStream == null) {
	    throw new ProxyDelegateQuietException(file + " not found.");
	}
    }
}
