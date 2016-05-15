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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class FilehashingTest {

    Filehasing filehashing = new Filehasing();
    String xml1 = "<xml><data>blah</data></xml>";
    String xml2 = "<xml><data>blah2</data></xml>";
    String xml3Like1ButFormatted = "<xml>\n\r<data>\n\rblah</data></xml>";
    
    byte[] firstXml;
    byte[] secondXml;
    
    String hashResult1;
    String hashResult2;
    
    @Test
    public void assureThatSimpleHashSameWorks() {
    	firstXml = xml1.getBytes();
	secondXml = xml1.getBytes();
	runHashing();
	assertEquals(hashResult1, hashResult2);
    }

    @Test
    public void assureThatSimpleHashLowerCaseWorks() {
	firstXml = xml1.getBytes();
	runHashing();
	assertEquals("a798352b6a8d9bf0df7124590ce2c95c", hashResult1);
    }
    
    @Test
    public void assureThatSimpleHashWorks() {
	firstXml = xml1.getBytes();
	secondXml = xml2.getBytes();
	runHashing();
	assertFalse(hashResult1.equals(hashResult2));
    }
    
    @Test
    public void assureThatFormattedHashWorks() {
	firstXml = xml1.getBytes();
	secondXml = xml3Like1ButFormatted.getBytes();
	runHashing();
	assertEquals(hashResult1, hashResult2);
    }

    private void runHashing() {
	hashResult1 = filehashing.hash(firstXml);
	System.out.println(hashResult1);
	if(secondXml!=null) {
	    hashResult2 = filehashing.hash(secondXml);
	    System.out.println(hashResult2);
	}
    }
}
