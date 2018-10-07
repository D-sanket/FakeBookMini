import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.Calendar;

public class UnreadNotificationsServlet extends HttpServlet {
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
				
				String query = "SELECT * FROM notifications WHERE user_id = ? AND is_deleted = 0 AND is_read = 0 ORDER BY id DESC";
				
				PreparedStatement ps = conn.prepareStatement(query);
				
				ps.setInt(1, user_id);
				
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) {
					pw.println("<a class='notification' href='"+"javascript:readNotification("+rs.getInt("post_id")+")"+"'>");
					if(rs.getInt("type") == Notifier.NEW_LIKE)
						pw.println("<i class='tiny material-icons'>thumb_up</i>");
					else if(rs.getInt("type") == Notifier.NEW_REPLY)
						pw.println("<i class='tiny material-icons'>chat</i>");
					pw.println(rs.getString("title"));
					pw.println("</a>");
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
