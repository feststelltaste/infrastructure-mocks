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
package soapmocks.generic.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class SoapMocksLogger {

    private final Logger logger;

    SoapMocksLogger(String clazz) {
	this.logger = Logger.getLogger(clazz);
    }
    
    public void outNoId(String message) {
	System.out.println(message);
    }
    
    public void out(String message) {
	System.out.println(id() + message);
    }
    
    public void info(String message) {
	logger.info(id() + message);
    }
    
    public void debug(String message) {
	logger.finer(id() + message);
    }
    
    public void error(Throwable t, String message) {
	logger.log(Level.SEVERE, id() + message, t);
    }
    
    public void error(Throwable t) {
	logger.log(Level.SEVERE, id() + t.getMessage(), t);
    }
    
    String id() {
	return "[" + Thread.currentThread().getId() + "] ";
    }
}
