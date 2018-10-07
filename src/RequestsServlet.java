import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.Calendar;

public class RequestsServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			
			PrintWriter pw = response.getWriter();
			
			if(request.getSession(false) == null) {
				pw.println("");
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
				
				String query = "SELECT * FROM friends WHERE second = ? AND is_deleted = 0 AND is_confirmed = 0 ORDER BY id DESC";
				String query2 = "SELECT name, id FROM users WHERE id = ?";
				PreparedStatement ps = conn.prepareStatement(query);
				PreparedStatement ps2 = conn.prepareStatement(query2);
				
				ps.setInt(1, user_id);
				
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) {
					ps2.setInt(1, rs.getInt("first"));
					ResultSet rs2 = ps2.executeQuery();
					if(rs2.next()) {
						pw.println("<div class='request' id='request_"+rs.getInt("id")+"'>");
						pw.println("<div class='name'>"+rs2.getString("name")+"</div>");
						pw.println("<div class='actions'>");
						pw.println("<a class='accept' href='javascript:acceptRequest("+rs2.getInt("id")+", "+rs.getInt("id")+")'>Accept</a>");
						pw.println("<a class='decline' href='javascript:deleteRequest("+rs2.getInt("id")+", "+rs.getInt("id")+")'>Decline</a>");
						pw.println("</div>");
						pw.println("</div>");
					}
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
