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
public final class ResponseCreator {

    private static final Log LOG = LogFactory.create(ResponseCreator.class);

    private String baseDir = "";

    public ResponseCreator() {
    }

    /**
     * @param baseDir
     *            for finding the files to be found. Relative to
     *            soapmocks.files.basedir or classpath
     */
    public ResponseCreator(String baseDir) {
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
     * @param classForT
     *            The type of the response object
     * @param method
     *            the method of the webservice
     * @param parameters
     *            any parameter string from request to indentify it
     * @return Object to return in WebService
     */
    public <T> T using(Class<T> classForT, String method, String... parameters) {
	return using(null, classForT, true, method, parameters);
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
     * @param elementResponse
     *            The element in the response file representing the response
     *            object
     * @param classForT
     *            The type of the response object
     * @param method
     *            the method of the webservice
     * @param parameters
     *            any parameter string from request to indentify it
     * @return Object to return in WebService
     */
    public <T> T using(String elementResponse, Class<T> classForT,
	    String method, String... parameters) {
	return using(elementResponse, classForT, true, method, parameters);
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
     * @param elementResponse
     *            The element in the response file representing the response
     *            object
     * @param classForT
     *            The type of the response object
     * @param defaultXml
     *            true when a default xml shall be searched for
     * @param method
     *            the method of the webservice
     * @param parameters
     *            any parameter string from request to indentify it
     * @return Object to return in WebService
     */
    public <T> T using(String elementResponse, Class<T> classForT,
	    boolean defaultXml, String method, String... parameters) {
	ProxyDelegator.serviceIdentifier(method, parameters);
	String filename = new ResponseCreatorFileFinder()
		.findFileFromMethodsAndParameter(baseDir, defaultXml, method,
			parameters);
	if (filename == null) {
	    throw new ProxyDelegateQuietException("file not found");
	}
	return using(filename, elementResponse, classForT);
    }

    public <T> T using(String xmlfile, Class<T> classForT) {
	return using(xmlfile, null, classForT);
    }

    public <T> T using(String xmlfile, String fromElement, Class<T> classForT) {
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
	    if (!found) {
		throw new ProxyDelegateQuietException(fromElement
			+ " element not found in " + xmlfile);
	    }
	    JAXBContext jc;
	    jc = JAXBContext.newInstance(classForT);
	    Unmarshaller unmarshaller = jc.createUnmarshaller();
	    JAXBElement<T> jaxbElement = unmarshaller.unmarshal(xsr, classForT);
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

    private void failIfStreamNotFound(String file, InputStream fileInputStream)
	    throws FileNotFoundException {
	if (fileInputStream == null) {
	    throw new ProxyDelegateQuietException(file + " not found.");
	}
    }
}
