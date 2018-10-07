import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.Calendar;

public class GetPostServlet extends HttpServlet {
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
				
				String query = "SELECT * FROM posts WHERE id = ?";
				String query2 = "SELECT * FROM replies WHERE post_id = ?";
				String query3 = "SELECT * FROM users WHERE id = ?";
				String query4 = "SELECT * FROM likes WHERE post_id = ?";
				String query5 = "SELECT * FROM likes WHERE post_id = ? AND user_id = ?";
				String query6 = "SELECT * FROM replies WHERE post_id = ?";
				
				PreparedStatement ps = conn.prepareStatement(query);
				PreparedStatement ps2 = conn.prepareStatement(query2);
				PreparedStatement ps3 = conn.prepareStatement(query3);
				PreparedStatement ps4 = conn.prepareStatement(query4);
				PreparedStatement ps5 = conn.prepareStatement(query5);
				PreparedStatement ps6 = conn.prepareStatement(query6);
				
				
				
				ps.setInt(1, post_id);
				
				ResultSet rs = ps.executeQuery();
				
				if(rs.next()) {
					ps3.setInt(1, rs.getInt("user_id"));
					ResultSet rs3 = ps3.executeQuery();
					int likes = 0, comments = 0;
					boolean liked = false;
					ps4.setInt(1, post_id);
					ResultSet rs4 = ps4.executeQuery();
					
					ps6.setInt(1, post_id);
					ResultSet rs6 = ps6.executeQuery();
					
					while(rs4.next()) {
						likes++;
						if(rs4.getInt("user_id") == user_id)
							liked = true;
					}
					
					while(rs6.next())
						comments++;
					
					if(rs3.next()) {
						pw.println("<div class='active-post' id='active_post_"+rs.getInt("id")+"'>");
						pw.println("<div class=''><span class='name blue-text'>"+rs3.getString("name")+"</span> &nbsp; &nbsp; &nbsp; <span class'grey-text right time' style='color: grey; float: right;'>"+rs.getString("posted_at")+"</span></div>");
						pw.println("<div class='content'>");
						pw.println(rs.getString("content"));
						pw.println("</div>");
						pw.println("<div class=''>");
						pw.println("<a href="+(liked ? "'javascript:unlikePost("+rs.getInt("id")+")'" : "'javascript:likePost("+rs.getInt("id")+")'")+" id='likeBtn_"+rs.getInt("id")+"' class='likes "+(liked ? "liked" : "")+"'>Likes ("+likes+") </a>");
						pw.println("<span class='text-lighten-2 grey-text'> | </span>");
						pw.println("<a href='javascript:void(0)' class='comments'>Comments ("+comments+") </a>");
						pw.println("</div>");
						pw.println("</div>");
						
						pw.println("<div class='divider'></div>");
						
						pw.println("<div class='replies-container'>");
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
