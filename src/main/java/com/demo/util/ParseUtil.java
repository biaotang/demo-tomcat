package com.demo.util;

import java.util.StringTokenizer;

import com.demo.tomcat.enums.RequestMethod;
import com.demo.tomcat.http.SavedRequest;

public class ParseUtil {

	/**  
	 *	parse request
	 */   
	public static void parser(String input, SavedRequest savedRequest) {
		// we parse the request with a string tokenizer
		StringTokenizer tokenizer = new StringTokenizer(input);
		String method = tokenizer.nextToken().toUpperCase(); // we get the HTTP method of the client
		
		if (RequestMethod.method(method) != null) {
			// we get request uri
			String uri = tokenizer.nextToken().toLowerCase();
			savedRequest.setMethod(method);
			savedRequest.setRequestURI(uri); 
		}
		
		// other params eg. Accept、 Cookie
	}
	
}
