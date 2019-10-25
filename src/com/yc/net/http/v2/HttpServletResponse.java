package com.yc.net.http.v2;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

public class HttpServletResponse {
	
		/**
		 * 响应报文：
		 * 	1、头行（协议版本  状态码  状态码 的描述信息）
		 *  2、头域（键值对 :分割）
		 *  	Conten-Type:对应的文件类型
		 *  3、空行
		 *  4、实体（body）
		 *  	返回的结果（html、css、js、jpg）
		 */
	//web.xml 解析器
	private static WebXmlParser webXmlParser=new WebXmlParser("web.xml");
	
	private HttpServletRequest request;
	private OutputStream out;
	
	private int status=200;//状态码
	private String message="OK";//状态码描述
	//头域集合
	private HashMap<String, String> headerMap=new HashMap<>();
	
	public HttpServletResponse(HttpServletRequest request,OutputStream out){
		super();
		this.request=request;
		this.out=out;
	}
	
	//response.setContentType("");设置响应类型
	//response.setStatus(404,"");设置结果码
	//response.setHeader("键","值");
	
	//提交方法
	public void commit() throws IOException{
		String suffix = request.getRequestURL().substring(
				request.getRequestURL().lastIndexOf(".")+1);
	
		// 从 web.xml 文件中取 contentType， 替代之前的硬编码判断
		
		if(headerMap.containsKey("Content-Type")==false){
			String contentType = webXmlParser.getContentType(suffix);
			//设置响应类型
			setContentType(contentType);
		}
	
		String resp = "HTTP/1.1 "+status+" "+message+"\r\n";
		//resp += "Content-Type: "+contentType+"\r\n";
		//写头域信息
		for(Entry<String, String> entry:headerMap.entrySet()){
			resp+=entry.getKey()+":"+entry.getValue()+"\r\n";
		}
		resp +="\r\n";
		out.write(resp.getBytes());
		
		//响应重定向不需要写body
		if(status<300 || status>399){
			
			if(caw.toString().isEmpty()){
				String rootPath = "E:/tomcat/photo";
				String filePath = request.getRequestURL();
				// 判断访问文件是否存在			
				String diskPath = rootPath + filePath;
				if(new File(diskPath).exists() == false){
					diskPath = rootPath + "/404.html";
				}
				FileInputStream fis = new FileInputStream(diskPath);
				
				byte[] buf = new byte[1024];
				int count;
				//向浏览器发送报文
				while((count=fis.read(buf))>0){
					out.write(buf,0,count);
				}
				fis.close();
			}else{
				out.write(caw.toString().getBytes());
			}
			
		}
	}

	public void setStatus(int status, String message) {
		this.status=status;
		this.message=message;
	}
	/**
	 * 响应重定向
	 */
	public void sendRedirect(String webPath){
		/**
		 * 响应结果：
		 * 	1XX  接受到请求，继续处理
		 * 	2XX  正常响应200
		 * 	3XX  响应重定向 301 302
		 * 	4XX  客户端错误  404 405
		 * 	5XX  服务端错误  服务器无法完成明显有效的请求
		 */
		this.setStatus(301, "Redirect");
		this.addHeader("Location", webPath);
	}
	
	public void addHeader(String key,String value){
		this.headerMap.put(key, value);
	}
	
	public void setContentType(String contentType) {
		this.headerMap.put("Content-Type", contentType);
	}
	
	/**
	 * 如何定义 PrintWriter，在commit 要考虑 和文件输出配合问题
	 * @return
	 */
	CharArrayWriter caw=new CharArrayWriter();
	PrintWriter pw=new PrintWriter(caw);
	public PrintWriter getWriter(){
		return pw;
	}
}














