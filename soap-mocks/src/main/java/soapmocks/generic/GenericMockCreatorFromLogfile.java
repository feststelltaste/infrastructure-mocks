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
package soapmocks.generic;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class GenericMockCreatorFromLogfile {

	
	public void generate(int start, int end, String conditionStart, String conditionEnd,
			String url, String service, String file) throws IOException,
			URISyntaxException {
		
		List<String> lines = FileUtils.readLines(new File(getClass().getResource(file).toURI()), "UTF-8");
		for (int i = start; i < end; i++) {
			GenericMockCreatorSingleResult result = parse("ID: "+i, lines);
			String request = result.request;
			String condition = request.substring(request.indexOf(conditionStart)+conditionStart.length(), request.indexOf(conditionEnd));
			FileUtils.writeByteArrayToFile(new File("./" + service + "-"+condition+".config"), 
					("url=" + url + "\n"+
					  "responseFile=" + service + "Response-"+condition+".xml\n"+
					  "requestContains="+condition).getBytes());
			FileUtils.writeByteArrayToFile(new File("./"+service+"Response-"+condition+".xml"), 
					result.response.getBytes());
		}
	}
	
	
	public GenericMockCreatorSingleResult parse(String id, List<String> lines) throws IOException {
		GenericMockCreatorSingleResult result = new GenericMockCreatorSingleResult();
		StringBuffer request = new StringBuffer();
		StringBuffer response = new StringBuffer();
		
		String foundId = null;
		boolean inRequest = false;
		boolean requestFinished = false;
		
		boolean inResponse = false;
		boolean responseFinished = false;
		
		for (String line : lines) {
			if(inRequest) {
				if(line.startsWith("--------------------------------------")) {
					requestFinished = true;
					inRequest = false;
				} else {
					if(lineNotStartWithForbidden(line)) {
						request.append(line);
					}
				}
			}
			
			if(inResponse) {
				if(line.startsWith("--------------------------------------")) {
					responseFinished = true;
					inResponse = false;
				} else {
					if(lineNotStartWithForbidden(line)) {
						response.append(line);
					}
				}
			}
			
			if(line.startsWith(id) && !requestFinished) {
				inRequest = true;
				foundId = line; 
			}
			if(line.startsWith(id) && requestFinished) {
				inResponse = true;
			}
			if(responseFinished) {
				break;
			}
		}
		result.id=foundId;
		result.request=request.toString().replaceFirst("Payload: ", "").trim();
		result.response=response.toString().replaceFirst("Payload: ", "").trim();
		return result;
	}

	private boolean lineNotStartWithForbidden(String line) {
		return !line.startsWith("Address: ")
				&& !line.startsWith("Encoding: ")
				&& !line.startsWith("Content-Type: ")
				&& !line.startsWith("Headers: ")
				&& !line.startsWith("Response-Code: ");
	}

}

