package com.demo.tomcat.enums;

public enum RequestMethod {
	
	GET("GET"),
	POST("POST"),
	PUT("PUT"),
	PATCH("PATCH"),
	HEAD("HEAD"),
	DELETE("DELETE"),
	OPTION("OPTION");
	
	String method;

	private RequestMethod(String method) {
		this.method = method;
	}
	
	public static RequestMethod method(String method) {
		for (RequestMethod requestMethod : RequestMethod.values()) {
			if (method.equals(requestMethod.method)) {
				return requestMethod;
			}
		}
		return null;
	}
}
