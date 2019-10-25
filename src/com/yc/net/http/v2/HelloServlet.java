package com.yc.net.http.v2;

import java.io.PrintWriter;

/**
 * 实现响应输出流html 代码
 * @author ASUS
 *
 */
public class HelloServlet extends HttpServlet{

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		/**
		 * 作业
		 */
		response.setContentType("text/html");
		PrintWriter pw=response.getWriter();
		
		pw.print("<h1>Hello world!</h1>");
		
		
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
}
