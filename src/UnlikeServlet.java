import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.Calendar;

public class UnlikeServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			
			PrintWriter pw = response.getWriter();
			
			if(request.getSession(false) == null) {
				pw.println("Something went wrong!");
				return;
			}
			else {
				String referer = request.getHeader("Referer").substring(0, request.getHeader("Referer").indexOf('?') > 0 ? request.getHeader("Referer").indexOf('?') : 0);
				RequestDispatcher rd = request.getRequestDispatcher(referer);
				
				DriverManager.registerDriver(new com.mysql.jdbc.Driver());
				String url = "jdbc:mysql://localhost:3306/fakebook";
				String user = "root";
				String pass = "";
				Connection conn = DriverManager.getConnection(url, user, pass);
				
				int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
				int post_id = Integer.parseInt(request.getParameter("post_id"));
				
				String query = "DELETE FROM likes WHERE post_id = ? AND user_id = ?";
				
				PreparedStatement ps = conn.prepareStatement(query);
				
				ps.setInt(1, post_id);
				ps.setInt(2, user_id);
				
				if(ps.executeUpdate() == 0) {
					pw.println("Something went wrong!");
				}
				
				pw.close();
				conn.close();
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
