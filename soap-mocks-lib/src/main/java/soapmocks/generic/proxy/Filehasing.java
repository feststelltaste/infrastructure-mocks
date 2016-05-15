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
package soapmocks.generic.proxy;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.MessageDigest;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Filehasing {

    private static final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

    public String hash(byte[] xml1) {
	return createHash(xml1);
    }

    private String createHash(byte[] xml1) {
	try {
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	    Document doc = docBuilder.parse(new ByteArrayInputStream(xml1));
	    
	    Element documentElement = doc.getDocumentElement();
	    documentElement.normalize();
	    
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    StringWriter writer = new StringWriter();
	    transformer.transform(new DOMSource(doc), new StreamResult(writer));
	    String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
	    
	    String hash = (new HexBinaryAdapter()).marshal(MessageDigest.getInstance("MD5").digest(output.getBytes())).toLowerCase();
	    return hash;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

}
