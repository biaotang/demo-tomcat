package com.demo.util;

public class WebXmlConfigUtil {
	
	public ProjectUtil.WebXML loadXml(String xmlPath) {
		
		return new ProjectUtil().new WebXML();
	}
	
}
