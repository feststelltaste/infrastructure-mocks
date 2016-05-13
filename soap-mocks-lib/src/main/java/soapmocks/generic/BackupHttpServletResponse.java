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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import soapmocks.api.ProxyDelegator;
import soapmocks.io.IOUtils;

public final class BackupHttpServletResponse implements HttpServletResponse {

    private HttpServletResponse delegate;
    private ByteArrayOutputStream byteArrayOutputStream;
    private int statuscode;

    BackupHttpServletResponse(HttpServletResponse delegate) {
	this.delegate = delegate;
    }

    @Override
    public void addCookie(Cookie cookie) {
	if (ProxyDelegator.isDelegateToProxy()) {
	    return;
	}
	delegate.addCookie(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
	return delegate.containsHeader(name);
    }

    @Override
    public String encodeURL(String url) {
	return delegate.encodeURL(url);
    }

    @Override
    public String getCharacterEncoding() {
	return delegate.getCharacterEncoding();
    }

    @Override
    public String encodeRedirectURL(String url) {
	return delegate.encodeRedirectURL(url);
    }

    @Override
    public String getContentType() {
	return delegate.getContentType();
    }

    @Override
    @SuppressWarnings("deprecation")
    public String encodeUrl(String url) {
	return delegate.encodeUrl(url);
    }

    @Override
    @SuppressWarnings("deprecation")
    public String encodeRedirectUrl(String url) {
	return delegate.encodeRedirectUrl(url);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
	if (ProxyDelegator.isDelegateToProxy()) {
	    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    ServletOutputStream outputStream = new ServletOutputStream() {
		@Override
		public void write(int b) throws IOException {
		    byteArrayOutputStream.write(b);
		}
	    };
	    return outputStream;
	}
	byteArrayOutputStream = new ByteArrayOutputStream();
	ServletOutputStream outputStream = new ServletOutputStream() {
	    @Override
	    public void write(int b) throws IOException {
		byteArrayOutputStream.write(b);
	    }
	};
	return outputStream;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
	if (ProxyDelegator.isDelegateToProxy()) {
	    return;
	}
	delegate.sendError(sc, msg);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
	if (ProxyDelegator.isDelegateToProxy()) {
	    return new PrintWriter(new ByteArrayOutputStream());
	}
	byteArrayOutputStream = new ByteArrayOutputStream();
	PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
	return printWriter;
    }

    @Override
    public void sendError(int sc) throws IOException {
	delegate.sendError(sc);
    }

    @Override
    public void setCharacterEncoding(String charset) {
	delegate.setCharacterEncoding(charset);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
	delegate.sendRedirect(location);
    }

    @Override
    public void setDateHeader(String name, long date) {
	delegate.setDateHeader(name, date);
    }

    @Override
    public void setContentLength(int len) {
	delegate.setContentLength(len);
    }

    @Override
    public void addDateHeader(String name, long date) {
	delegate.addDateHeader(name, date);
    }

    @Override
    public void setContentType(String type) {
	delegate.setContentType(type);
    }

    @Override
    public void setHeader(String name, String value) {
	if (ProxyDelegator.isDelegateToProxy()) {
	    return;
	}
	delegate.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
	if (ProxyDelegator.isDelegateToProxy()) {
	    return;
	}
	delegate.addHeader(name, value);
    }

    @Override
    public void setBufferSize(int size) {
	delegate.setBufferSize(size);
    }

    @Override
    public void setIntHeader(String name, int value) {
	if (ProxyDelegator.isDelegateToProxy()) {
	    return;
	}
	delegate.setIntHeader(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
	if (ProxyDelegator.isDelegateToProxy()) {
	    return;
	}
	delegate.addIntHeader(name, value);
    }

    @Override
    public void setStatus(int sc) {
	statuscode = sc;
    }

    @Override
    public int getBufferSize() {
	return delegate.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
	if (ProxyDelegator.isDelegateToProxy()) {
	    return;
	}
	delegate.flushBuffer();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setStatus(int sc, String sm) {
	delegate.setStatus(sc, sm);
    }

    @Override
    public void resetBuffer() {
	delegate.resetBuffer();
    }

    @Override
    public int getStatus() {
	return delegate.getStatus();
    }

    @Override
    public boolean isCommitted() {
	return delegate.isCommitted();
    }

    @Override
    public String getHeader(String name) {
	return delegate.getHeader(name);
    }

    @Override
    public void reset() {
	delegate.reset();
    }

    @Override
    public Collection<String> getHeaders(String name) {
	return delegate.getHeaders(name);
    }

    @Override
    public void setLocale(Locale loc) {
	delegate.setLocale(loc);
    }

    @Override
    public Collection<String> getHeaderNames() {
	return delegate.getHeaderNames();
    }

    @Override
    public Locale getLocale() {
	return delegate.getLocale();
    }

    public void commit() throws IOException {
	IOUtils.copy(
		new ByteArrayInputStream(byteArrayOutputStream.toByteArray()),
		delegate.getOutputStream());
	delegate.setStatus(statuscode);
    }

    public String getResponse() {
	return byteArrayOutputStream != null ? new String(
		byteArrayOutputStream.toByteArray()) : "";
    }
}
