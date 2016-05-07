package soapmocks.util;

public final class ServiceIdentifier {

    private final String method;
    
    private final String[] parameters;

    ServiceIdentifier(String method, String... parameters) {
	this.method = method;
	this.parameters = parameters;
    }
    
    public String[] getParameters() {
	return parameters;
    }

    public String getMethod() {
	return method;
    }
    
    public String generateFilename() {
	StringBuilder filename = new StringBuilder();
	filename.append(method);
	for (String parameter : parameters) {
	    filename.append("-").append(parameter);
	}
	return filename.append(".xml").toString();
    }
}
