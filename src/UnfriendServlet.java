import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class UnfriendServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			
			PrintWriter pw = response.getWriter();
			
			if(request.getSession(false) == null) {
				pw.println("Please login first!");
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
				
				int auth_user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
				int user_id = Integer.parseInt(request.getParameter("user_id"));
				
				String query = "UPDATE friends SET is_deleted = 1, is_confirmed = 0 WHERE first = ? AND second = ?";
				PreparedStatement ps = conn.prepareStatement(query);
				
				ps.setInt(1, auth_user_id);
				ps.setInt(2, user_id);
				
				ps.executeUpdate();
				
				ps.setInt(1, user_id);
				ps.setInt(2, auth_user_id);
				
				ps.executeUpdate();
				
				pw.close();
				conn.close();
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
