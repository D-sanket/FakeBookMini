import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.Calendar;

public class LikeServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			
			PrintWriter pw = response.getWriter();
			
			if(request.getSession(false) == null) {
				pw.println("Something went wrong");
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
				
				String query = "SELECT * FROM likes WHERE post_id = ? AND user_id = ?";
				String query2 = "INSERT INTO likes (user_id, post_id, liked_at) VALUES (?, ?, ?)";
				String query3 = "SELECT name FROM users WHERE id = ?";
				String query4 = "SELECT user_id FROM posts WHERE id = ?";
				
				PreparedStatement ps = conn.prepareStatement(query);
				PreparedStatement ps2 = conn.prepareStatement(query2);
				PreparedStatement ps3 = conn.prepareStatement(query3);
				PreparedStatement ps4 = conn.prepareStatement(query4);
				
				ps.setInt(1, post_id);
				ps.setInt(2, user_id);
				
				if(ps.executeQuery().next()) {
					pw.println("You have already liked this post!");
				}
				else {
					Calendar c = Calendar.getInstance();
					int h = c.get(Calendar.HOUR_OF_DAY);
					int m = c.get(Calendar.MINUTE);
					int s = c.get(Calendar.SECOND);
					int Y = c.get(Calendar.YEAR);
					int M = c.get(Calendar.MONTH);
					int D = c.get(Calendar.DAY_OF_MONTH);
					
					String date = Y+"-"+(M > 9 ? M : "0"+M)+"-"+(D > 9 ? D : "0"+D)+" "+(h > 9 ? h : "0"+h)+":"+(m > 9 ? m : "0"+m)+":"+(s > 9 ? s : "0"+s);
					
					ps2.setInt(1, user_id);
					ps2.setInt(2, post_id);
					ps2.setString(3, date);
					int t = ps2.executeUpdate();
					
					if(t != 0) {
						ps3.setInt(1, user_id);
						ResultSet rs3 = ps3.executeQuery();
						if(rs3.next()) {
							ps4.setInt(1, post_id);
							ResultSet rs4 = ps4.executeQuery();
							if(rs4.next()) {
								if(rs4.getInt("user_id") != user_id)
									Notifier.notify(rs4.getInt("user_id"), post_id, rs3.getString("name"), Notifier.NEW_LIKE);
							}
						}
					}
					else {
						pw.println("Something went wrong!");
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
