import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class LogoutServlet extends HttpServlet{
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			HttpSession s = request.getSession();
			
			if(s != null) {
				s.invalidate();
			}
			
			response.sendRedirect("./auth");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
