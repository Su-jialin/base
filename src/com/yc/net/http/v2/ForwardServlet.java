package com.yc.net.http.v2;

/**
 * 实现请求转发
 * @author ASUS
 *
 */
public class ForwardServlet extends HttpServlet{

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		
		//请求转发 其实跟HTTP 协议无关
		
		//request.setRequestURL("index.html")
		RequestDispatcher rd=request.getRequestDispatcher("/index.html");
		rd.forward(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
}
