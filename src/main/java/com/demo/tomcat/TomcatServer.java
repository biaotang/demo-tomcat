package com.demo.tomcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.demo.tomcat.http.HttpServletRequestWrapper;
import com.demo.tomcat.http.HttpServletResponseWrapper;
import com.demo.tomcat.http.SavedRequest;
import com.demo.util.ParseUtil;
import com.demo.util.ProjectUtil;

public class TomcatServer {
	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	
	public final static int port = 8080;
	
	public static void main(String[] args) throws Exception {
		//加载项目信息
		Map<String, ProjectUtil.WebXML> configInfo = ProjectUtil.load();
		
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("tomcat 服务器启动成功");
		while (!serverSocket.isClosed()) {
			//获取Socket连接
			Socket socket = serverSocket.accept();
			threadPool.execute(() -> {
				try (InputStream is = socket.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(is))
						){
					System.out.println("收到请求：");
					String msg = null;
					SavedRequest savedRequest = new SavedRequest();
					while ((msg = reader.readLine()) != null) {
						if (msg.length() == 0) {
							break;
						}
						System.out.println(msg);
						ParseUtil.parser(msg, savedRequest);
					}
					System.out.println("---------收到请求");
					//根据用户请求的URL，找到相应的项目，并且调用相应的servlet
					//GET /servlet-demo-1.0.0/index HTTP/1.1
					//解析项目名称： /servlet-demo-1.0.0
					//请求servlet路径： /index
					
					StringTokenizer tokenizer = new StringTokenizer(savedRequest.getRequestURI(), "/");
					//根据URL请求调用servlet方法
					ProjectUtil.WebXML webXML = configInfo.get("/" + (tokenizer.hasMoreTokens() ? tokenizer.nextToken() : ""));
					String servletName = webXML.servletMapping.get("/" + (tokenizer.hasMoreTokens() ? tokenizer.nextToken() : ""));
					Servlet servlet = webXML.servletInstances.get(servletName);
					
					//Servlet生命周期
					//init  service   destroy
					HttpServletRequest servletRequest = new HttpServletRequestWrapper(savedRequest);
					HttpServletResponse servletResponse = new HttpServletResponseWrapper();
					servlet.service(servletRequest, servletResponse);
					
					//响应返回结果  200
					OutputStream os = socket.getOutputStream();
					os.write("HTTP/1.1 200 OK\r\n".getBytes());
					os.write("Content-Lenght: 11\r\n\r\n".getBytes());
					os.write("Hello World".getBytes());
					os.flush();
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (socket != null) {
							socket.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
		if (serverSocket != null) {
			serverSocket.close();
		}
	}
	
}
