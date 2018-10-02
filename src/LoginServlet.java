
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class LoginServlet extends HttpServlet{

	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			RequestDispatcher rd = request.getRequestDispatcher("./login.html");
			
			rd.forward(request, response);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
