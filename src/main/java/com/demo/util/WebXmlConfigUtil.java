package com.demo.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import com.demo.util.ProjectUtil.WebXML;

public class WebXmlConfigUtil {
	
	protected SAXParserFactory factory = null;
	
	protected SAXParser parser = null;
	
	protected WebXML webXML;
	
	public WebXML loadXml(String xmlPath, WebXML webXML) throws IOException, SAXException {
		System.out.println("WebXmlConfigUtil load XML from : " + xmlPath);
		File webxml = new File(xmlPath);
		this.webXML = webXML;
		getParser().parse(webxml, new WebXmlHandler());
		return this.webXML;
	}
	
	public SAXParserFactory getFactory() {
		if (factory == null) {
			factory = SAXParserFactory.newInstance();
		}
		return (factory);
	}
	
	public SAXParser getParser() {
		if (parser != null) {
			return (parser);
		}
		
		// Create a new parse
		try {
			parser = getFactory().newSAXParser();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (parser);
	}
	
	public class WebXmlHandler extends DefaultHandler2 {
		
		protected String content = null;
		
		protected String servletName = null;
		
		protected String servletClass = null;
		
		protected String urlPattern = null;
		
		protected boolean parseServlet = false;
		
		protected boolean parseServletMapping = false;
		
		//开始解析文档
		@Override
		public void startDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.startDocument();
		}
		
		//文档解析结束
		@Override
		public void endDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.endDocument();
		}
		
		//开始解析某一个元素
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			System.out.println("WebXmlConfigUtil startElement localName : " + localName);
			System.out.println("WebXmlConfigUtil startElement qName : " + qName);
			if ("servlet".equals(qName)) {
				parseServlet = true;
			}
			if ("servlet-mapping".equals(qName)) {
				parseServletMapping = true;
			}
		}
		
		//解析某一个元素结束
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if ("servlet-name".equals(qName)) {
				servletName = content;
			} else if ("servlet-class".equals(qName)) {
				servletClass = content;
			} else if ("url-pattern".equals(qName)) {
				urlPattern = content;
			}
			
			if (parseServlet && "servlet".equals(qName)) {
				webXML.servlets.put(servletName, servletClass);
				reset();
			} else if (parseServletMapping && "servlet-mapping".equals(qName)) {
				webXML.servletMapping.put(urlPattern, servletName);
				reset();
			}
			
			System.out.println("endElement WebXML servlets : " + webXML.servlets.toString());
			System.out.println("endElement WebXML servletMapping : " + webXML.servletMapping.toString());
			
		}
		
		//正在解析的元素内容
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			content = new String(ch, start, length);
		}
		
		public void reset() {
			content = null;
			servletName = null;
			servletClass = null;
			urlPattern = null;
			parseServlet = false;
			parseServletMapping = false;
		}
		
	}
}
