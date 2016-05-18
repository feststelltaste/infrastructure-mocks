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

    public ResponseCreator(String baseDir) {
	if (baseDir == null) {
	    throw new NullPointerException();
	}
	this.baseDir = baseDir;
    }

    /**
     * With searching for defaultXml true. Supports Service Identifier.
     */
    public <T> T unmarshal(Class<T> classForT, String method,
	    String... parameters) {
	return unmarshal(null, classForT, true, method, parameters);
    }

    /**
     * With searching for defaultXml true, with element of response. Supports
     * Service Identifier.
     */
    public <T> T unmarshal(String elementResponse, Class<T> classForT,
	    String method, String... parameters) {
	return unmarshal(elementResponse, classForT, true, method, parameters);
    }

    /**
     * Supports Service Identifier.
     */
    public <T> T unmarshal(String elementResponse, Class<T> classForT,
	    boolean defaultXml, String method, String... parameters) {
	ProxyDelegator.serviceIdentifier(method, parameters);
	String filename = new ResponseCreatorFileFinder()
		.findFileFromMethodsAndParameter(baseDir, defaultXml, method,
			parameters);
	if (filename == null) {
	    throw new ProxyDelegateQuietException("file not found");
	}
	return unmarshal(filename, elementResponse, classForT);
    }

    public <T> T unmarshal(String xmlfile, Class<T> classForT) {

	return unmarshal(xmlfile, null, classForT);
    }

    public <T> T unmarshal(String xmlfile, String fromElement,
	    Class<T> classForT) {
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
	    if(!found) {
		throw new ProxyDelegateQuietException(fromElement + " element not found in " + xmlfile);
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
