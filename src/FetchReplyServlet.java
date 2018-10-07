import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.Calendar;

public class FetchReplyServlet extends HttpServlet {
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
				int post_id = Integer.parseInt(request.getParameter("post_id"));
				
				String query2 = "SELECT * FROM replies WHERE post_id = ?";
				String query3 = "SELECT * FROM users WHERE id = ?";
				
				PreparedStatement ps2 = conn.prepareStatement(query2);
				PreparedStatement ps3 = conn.prepareStatement(query3);
				
				ps2.setInt(1, post_id);
				ResultSet rs2 = ps2.executeQuery();
				
				boolean flag = false;
				
						while(rs2.next()) {
							ps3.setInt(1, rs2.getInt("user_id"));
							ResultSet rs33 = ps3.executeQuery();
							
							if(rs33.next()) {
								flag = true;
								pw.println("<div class='comment'>");
								pw.println("<div class='comment-author blue-text'>");
								pw.println(rs33.getString("name"));
								pw.println("</div>");
								pw.println("<div class='cotent'>");
								pw.println(rs2.getString("content"));
								pw.println("</div>");
								pw.println("</div>");
							}
							
						}
						
						if(!flag) {
							pw.println("<div class='no-comments'>No comments yet.</div>");
						}
						
						pw.println("</div>");
				
				pw.close();
				conn.close();
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
