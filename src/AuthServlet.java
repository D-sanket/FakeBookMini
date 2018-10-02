
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class AuthServlet extends HttpServlet{

	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			RequestDispatcher rd = request.getRequestDispatcher("./auth.html");
			
			rd.forward(request, response);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			PrintWriter pw = response.getWriter();
			
			if(request.getParameter("submit").equals("login")) {
				pw.println("Logged in");
			}
			else {
				pw.println("Registered");
			}
			pw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
