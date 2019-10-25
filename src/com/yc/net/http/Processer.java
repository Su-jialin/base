package com.yc.net.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Processer {
	// web.xml 解析器
	private static WebXmlParser webXmlParser = new WebXmlParser("web.xml");
	
	public void process(Socket socket) {
		
		InputStream in;
		OutputStream out;
		try {
			in=socket.getInputStream();
			out=socket.getOutputStream();
			byte[] buf=new byte[1024];
			int count;
			count=in.read(buf);
			String content=new String(buf,0,count);
			System.out.println(content);
			//解析请求报文（暂未实现）
			HttpServletRequest request=parseRequest(content);
			
			/**
			 * /index.html
			 * E:/photo/index.html
			 *  /imgges
			 * E:/photo/images
			 */
			
			
			String suffix=request.getRequestURL().substring(request.getRequestURL().lastIndexOf(".")+1);
			String contentType=webXmlParser.getContentType(suffix);
//			switch (suffix) {
//			case "js":
//				contentType="application/x-javascript";
//				break;
//			case "jpg":
//				contentType="image/jpg";
//				break;
//			case "bmp":
//				contentType="image/bmp";
//				break;
//			case "css":
//				contentType="text/css";
//				break;
//			case "gif":
//				contentType="image/gif";
//				break;
//			case "png":
//				contentType="image/png";
//				break;
//			default:
//				contentType="text/html";
//			}
			
			String responseStr="HTTP/1.1 200 OK\r\n";
			responseStr +="Content-Type:"+contentType+"\r\n";
			responseStr +="\r\n";//CRLF 空行
//			responseStr +="<h1>hello world<h1>";
			out.write(responseStr.getBytes());
			
			String rootPath="E:/photo";
			String filePath=request.getRequestURL();
			//判断文件是否存在
			File file=new File(rootPath+filePath);
			FileInputStream fis;
			if(!file.exists()){
				fis=new FileInputStream(rootPath+"/404.html");
			}else{
				fis=new FileInputStream(rootPath+filePath);
			}
			
			
			//向浏览器发送报文
			while((count=fis.read(buf))>0){
				out.write(buf,0,count);
			}
			fis.close();
			//根据请求路径返回对应的文件 html
			//如果访问的文件不存在，则放回404.html
			//解析tomcat/conf/web 文件，读取文件类型，然后输出对应的contentType
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//解析请求报文
		
		//给于对应的响应
	}
	public HttpServletRequest parseRequest(String content){
		HttpServletRequest request=new HttpServletRequest(content);
		return request;
		
	}
}
