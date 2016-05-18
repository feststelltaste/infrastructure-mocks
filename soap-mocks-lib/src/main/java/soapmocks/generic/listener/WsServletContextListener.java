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

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import soapmocks.generic.ContextPath;
import soapmocks.generic.StaticFileConfig;
import soapmocks.generic.logging.Log;
import soapmocks.generic.logging.LogFactory;

import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.transport.http.servlet.ServletAdapter;
import com.sun.xml.ws.transport.http.servlet.ServletAdapterList;
import com.sun.xml.ws.transport.http.servlet.WSServlet;
import com.sun.xml.ws.transport.http.servlet.WSServletDelegate;

public class WsServletContextListener implements ServletContextListener,
	ServletContextAttributeListener {

    private static final Log LOG = LogFactory
	    .create(WsServletContextListener.class);

    private static final AtomicBoolean INVOKED = new AtomicBoolean(false);

    private WSServletDelegate wsServletDelegate;

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
	ServletContext context = sce.getServletContext();
	
	ContextPath.SOAP_MOCKS_CONTEXT = sce.getServletContext().getContextPath();
	LOG.outNoId("SoapMocks context " + ContextPath.SOAP_MOCKS_CONTEXT);
	
	try {
	    parseAdaptersAndCreateDelegate(context);
	} catch (MalformedURLException e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
	if (wsServletDelegate != null) {
	    wsServletDelegate.destroy();
	}
	LOG.outNoId("SoapMocks stopped");
    }

    void parseAdaptersAndCreateDelegate(ServletContext context)
	    throws MalformedURLException {
	if (INVOKED.getAndSet(true)) {
	    return;
	}

	ClassLoader classLoader = Thread.currentThread()
		.getContextClassLoader();
	if (classLoader == null) {
	    classLoader = getClass().getClassLoader();
	}

	try {
	    AdapterLookup<ServletAdapter> parser = new AdapterLookup<ServletAdapter>(
		    classLoader, new ServletResourceLoader(context),
		    createContainer(context), new ServletAdapterList());
	    List<ServletAdapter> servletAdapters = parser.parse(
		    "jndi:/localhost/soap-mocks/WEB-INF/sun-jaxws.xml", null);
	    wsServletDelegate = new WSServletDelegate(servletAdapters, context);
	    context.setAttribute(WSServlet.JAXWS_RI_RUNTIME_INFO,
		    wsServletDelegate);
	    LOG.outNoId("SoapMocks initialization\n");
	    logServices(servletAdapters);
	} catch (Throwable e) {
	    context.removeAttribute(WSServlet.JAXWS_RI_RUNTIME_INFO);
	    throw new RuntimeException(e);
	}
	StaticFileConfig.initWithRuntimeException();
	LOG.outNoId("SoapMocks started\n");
    }

    private void logServices(List<ServletAdapter> servletAdapters) {
	for (ServletAdapter servletAdapter : servletAdapters) {
	    LOG.outNoId("#### " + servletAdapter.getName()
		    + " JaxWs mock added for url pattern "
		    + servletAdapter.urlPattern + "\n");
	}
    }

    private Container createContainer(ServletContext servletContext) {
	return new ServletContainer(servletContext);
    }

}
