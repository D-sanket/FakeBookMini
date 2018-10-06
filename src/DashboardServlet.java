import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class DashboardServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			if(request.getSession(false) == null) {
				response.sendRedirect("./auth");
				return;
			}
			response.setContentType("text/html");
			
			request.getRequestDispatcher("./dashboard.html").forward(request, response);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
}
