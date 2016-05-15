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
package soapmocks;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.sun.xml.ws.transport.http.servlet.WSServletContextListener;

@WebListener
public class ContextListener implements ServletContextListener, ServletContextAttributeListener {

    private WSServletContextListener wsServletContextListener = new WSServletContextListener();

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
	System.out.println("added: "+event.getName() + ":" + event.getValue());
	wsServletContextListener.attributeAdded(event);
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
	wsServletContextListener.attributeRemoved(event);
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
	System.out.println("replaced: "+event.getName() + ":" + event.getValue());
	wsServletContextListener.attributeReplaced(event);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
	wsServletContextListener.contextInitialized(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
	wsServletContextListener.contextDestroyed(sce);
    }
    
}

