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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

public class XmlUtil {

    private String baseDir = "";

    public XmlUtil() {
    }

    public XmlUtil(String baseDir) {
	if (baseDir == null) {
	    throw new NullPointerException();
	}
	this.baseDir = baseDir;
    }

    /**
     * With searching for defaultXml true
     */
    public <T> T unmarshal(Class<T> classForT, String method,
	    String... parameters) {
	return unmarshal(classForT, true, method, parameters);
    }

    public <T> T unmarshal(Class<T> classForT, boolean defaultXml,
	    String method, String... parameters) {
	String filename = findFileFromMethodsAndParameter(defaultXml, method,
		parameters);
	if (filename == null) {
	    ProxyDelegator.toProxy();
	    throw new QuietException("file not found");
	}
	return unmarshal(filename, classForT);
    }

    public <T> T unmarshal(String xmlfile, Class<T> classForT) {
	String simpleName = classForT.getSimpleName();
	String fromElement = Character.toLowerCase(simpleName.charAt(0))
		+ (simpleName.length() > 1 ? simpleName.substring(1) : "");
	return unmarshal(xmlfile, fromElement, classForT);
    }

    public <T> T unmarshal(String xmlfile, String fromElement,
	    Class<T> classForT) {
	try {
	    xmlfile = baseDir + xmlfile;
	    XMLInputFactory xif = XMLInputFactory.newInstance();
	    InputStream fileInputStream = getClass().getResourceAsStream(
		    xmlfile);
	    failIfStreamNotFound(xmlfile, fileInputStream);
	    StreamSource xml = new StreamSource(fileInputStream);
	    XMLStreamReader xsr = xif.createXMLStreamReader(xml);
	    xsr.next();
	    while (xsr.getLocalName() == null
		    || !xsr.getLocalName().equals(fromElement)) {
		xsr.next();
	    }
	    JAXBContext jc;
	    jc = JAXBContext.newInstance(classForT);
	    Unmarshaller unmarshaller = jc.createUnmarshaller();
	    JAXBElement<T> jaxbElement = unmarshaller.unmarshal(xsr, classForT);
	    xsr.close();
	    fileInputStream.close();
	    System.out.println("JaxWS ResponseFile: " + xmlfile);
	    return jaxbElement.getValue();
	} catch (Exception e) {
	    ProxyDelegator.toProxy();
	    throw new QuietException(e);
	}
    }

    private void failIfStreamNotFound(String file, InputStream fileInputStream)
	    throws FileNotFoundException {
	if (fileInputStream == null) {
	    throw new QuietException(file + " not found.");
	}
    }

    private String findFileFromMethodsAndParameter(boolean defaultXml,
	    String method, String... parameters) {
	String filename = "/" + method;
	for (String parameter : parameters) {
	    filename += "-" + parameter;
	}
	filename += ".xml";
	InputStream fileInputStream = getClass().getResourceAsStream(
		baseDir + filename);

	if (fileInputStream == null) {
	    if (defaultXml) {
		filename = "/" + method + "-default.xml";
	    } else {
		ProxyDelegator.toProxy();
		throw new QuietException(filename + " not found");
	    }
	} else {
	    closeQuietly(fileInputStream);
	}

	fileInputStream = getClass().getResourceAsStream(baseDir + filename);
	if (fileInputStream == null) {
	    ProxyDelegator.toProxy();
	    throw new QuietException(filename + " not found");
	} else {
	    closeQuietly(fileInputStream);
	}

	return filename;
    }

    private void closeQuietly(InputStream fileInputStream) {
	try {
	    fileInputStream.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

}
