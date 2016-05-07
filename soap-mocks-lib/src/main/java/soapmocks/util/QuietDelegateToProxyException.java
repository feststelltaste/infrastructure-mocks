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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Used in SoapMocks internally to handle XmlUtil parts where files are not found.
 * Sets Proxy delegation on creation.
 */
public final class QuietDelegateToProxyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public QuietDelegateToProxyException(Exception e) {
	super(e.getMessage());
	ProxyDelegator.toProxy();
    }

    public QuietDelegateToProxyException(String message) {
	super(message);
	ProxyDelegator.toProxy();
    }

    @Override
    public void printStackTrace() {
    }

    @Override
    public void printStackTrace(PrintStream s) {
    }

    @Override
    public void printStackTrace(PrintWriter s) {
    }

}
