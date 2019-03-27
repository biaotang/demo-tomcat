package com.demo.util;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

public class ProjectUtil {
	
	/** 加载所有项目信息 **/
	public static Map<String, WebXML> load() throws Exception {
		Map<String, WebXML> configInfo = new HashMap<>();
		
		String webapps = "D:\\demo-tomcat\\webapps";
		
		//0、 war，自动解压
		//1、 webapps子文件夹算是一个项目
		File[] projects = new File(webapps).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});		
		for (File projectFile : projects) {
			//2、 读取每个项目XML文件 （如果没有xml文件，则扫描注解）
			WebXML webXML = new WebXmlConfigUtil().loadXml(projectFile.getPath() + "\\WEB-INF\\web.xml");
			webXML.projectPath = projectFile.getPath();
			//3、 调用servlet之前需要先进行类加载
			webXML.loadServlet();
			configInfo.put(projectFile.getName(), webXML);
		}
		return configInfo;
	}
	
	public class WebXML {
		/** 项目文件夹地址 **/
		public String projectPath = null;
		/** Servlet定义 **/
		public Map<String, Object> servlets = new HashMap<>();
		/** ServeltMapping定义 **/
		public Map<String, String> servletMapping = new HashMap<>();
		/** Servelt集合定义 **/
		public Map<String, Servlet> servletInstances = new HashMap<String, Servlet>();
		
		public void loadServlet() throws Exception {
			//每个项目，类加载器，去加载置顶位置的class信息
			URL classUrl = new URL("file:" + projectPath + "\\WEB-INF\\classes\\");
			URLClassLoader servletClassLoader = new URLClassLoader(new URL[] {classUrl});
			
			//加载该项目中的servlet class
			for (Map.Entry<?, ?> entry : this.servlets.entrySet()) {
				String servletClassName = entry.getValue().toString();
				String servletName = entry.getKey().toString();
				
				//1、 加载到JVM
				Class<?> servletClass = servletClassLoader.loadClass(servletClassName);
				//2、 实例化
				Servlet servlet = (Servlet) servletClass.newInstance();
				//3、 保存起来，以后使用
				servletInstances.put(servletName, servlet);
			}
			servletClassLoader.close();
			
		}
		
	}
	
}
