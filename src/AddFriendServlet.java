import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class AddFriendServlet extends HttpServlet {
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
				boolean flag = false;
				String query = "SELECT * FROM friends WHERE first = ? AND second = ?";
				String query2 = "UPDATE friends SET is_deleted = 0, is_confirmed = 0 WHERE first = ? AND second = ?";
				String query3 = "INSERT INTO friends (first, second) VALUES(?, ?)";
				String query4 = "UPDATE friends SET first = ?, second = ?, is_deleted = 0, is_confirmed = 0 WHERE first = ? AND second = ?";
				PreparedStatement ps = conn.prepareStatement(query);
				PreparedStatement ps2 = conn.prepareStatement(query2);
				PreparedStatement ps3 = conn.prepareStatement(query3);
				PreparedStatement ps4 = conn.prepareStatement(query4);
				ps.setInt(1, auth_user_id);
				ps.setInt(2, user_id);
				ResultSet rs = ps.executeQuery();
				
				if(rs.next()) {
					flag = true;
					if(rs.getBoolean("is_deleted")) {
						ps2.setInt(1, auth_user_id);
						ps2.setInt(2, user_id);
						int count = ps2.executeUpdate();
						if(count == 0) {
							pw.print("Failed to request 1!");
						}
						pw.close();
						conn.close();
						return;
					}
					pw.println("Request already sent!");
					pw.close();
					conn.close();
					return;
				}
				
				ps.setInt(1, user_id);
				ps.setInt(2, auth_user_id);
				ResultSet rs2 = ps.executeQuery();
				
				if(rs2.next()) {
					if(rs2.getBoolean("is_deleted")) {
						ps4.setInt(1, auth_user_id);
						ps4.setInt(2, user_id);
						ps4.setInt(3, user_id);
						ps4.setInt(4, auth_user_id);
						
						int count = ps4.executeUpdate();
						if(count == 0) {
							pw.print("Failed to request 2!");
						}
						pw.close();
						conn.close();
						return;
					}
					pw.println("This user has already requested you!");
					pw.close();
					conn.close(); 
					return;
				}
				
				ps3.setInt(1, auth_user_id);
				ps3.setInt(2, user_id);
				
				int count = ps3.executeUpdate();
				
				if(count == 0) {
					pw.println("Failed to request!");
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
