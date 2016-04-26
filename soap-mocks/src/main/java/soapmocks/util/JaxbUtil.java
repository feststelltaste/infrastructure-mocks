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
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class JaxbUtil {

	
	public <T> JAXBElement<T> unmarshal(String file, Class<T> classForT) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(classForT);
			Unmarshaller jaxbUnmarshaller;
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			InputStream fileInputStream = getClass().getResourceAsStream(file);
	        failIfStreamNotFound(file, fileInputStream);
			Source source = new StreamSource(fileInputStream);
			JAXBElement<T> jaxbElement = jaxbUnmarshaller.unmarshal(source, classForT);
			fileInputStream.close();
			return jaxbElement;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public <T> JAXBElement<T> unmarshal(String file, String fromElement, Class<T> classForT)  {
		try {
			XMLInputFactory xif = XMLInputFactory.newInstance();
			InputStream fileInputStream = getClass().getResourceAsStream(file);
	        failIfStreamNotFound(file, fileInputStream);
			StreamSource xml = new StreamSource(fileInputStream);
	        XMLStreamReader xsr = xif.createXMLStreamReader(xml);
	        xsr.next();
	        while(xsr.getLocalName()==null || !xsr.getLocalName().equals(fromElement)) {
	            xsr.next();
	        }
	        JAXBContext jc;
			jc = JAXBContext.newInstance(classForT);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			JAXBElement<T> jaxbElement = unmarshaller.unmarshal(xsr, classForT);
			xsr.close();
			fileInputStream.close();
			System.out.println("JaxWS ResponseFile: " + file);
			return jaxbElement;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void failIfStreamNotFound(String file, InputStream fileInputStream)
			throws FileNotFoundException {
		if(fileInputStream==null) {
			throw new FileNotFoundException(file+" not found.");
		}
	}
}
