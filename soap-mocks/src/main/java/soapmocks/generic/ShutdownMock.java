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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShutdownMock extends com.sun.xml.ws.transport.http.servlet.WSServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException {
		if("SECRET".equals(req.getParameter("shutdown"))) {
			sendResponse("Shutdown done.", resp);
			System.exit(0);
		} else {
			sendResponse("No shutdown parameter given", resp);
		}
	}

	private void sendResponse(String message, HttpServletResponse resp) {
		try {
			System.out.println(message);
			resp.getOutputStream().write(message.getBytes());
			resp.getOutputStream().flush();
			resp.getOutputStream().close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}