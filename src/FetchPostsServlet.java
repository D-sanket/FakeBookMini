import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.Calendar;

public class FetchPostsServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/plain");
			
			PrintWriter pw = response.getWriter();
			
			if(request.getSession(false) == null) {
				pw.println("Login first!");
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
				int lastPostId = Integer.parseInt(request.getParameter("lastPostId"));
				
				
				String query = "SELECT first, second FROM friends WHERE is_deleted = 0 AND is_confirmed = 1 AND (first = ? OR second = ?)";
				String query2 = "SELECT posts.id, posts.user_id, posts.content, posts.posted_at, users.name FROM posts, users WHERE users.id = ? AND posts.user_id = ? AND posts.id > ? ORDER BY posted_at DESC";
				String query3 = "SELECT * FROM likes WHERE post_id = ?";
				String query4 = "SELECT * FROM likes WHERE post_id = ? AND user_id = ?";
				String query5 = "SELECT * FROM replies WHERE post_id = ?";
				PreparedStatement ps = conn.prepareStatement(query);
				PreparedStatement ps2 = conn.prepareStatement(query2);
				PreparedStatement ps3 = conn.prepareStatement(query3);
				PreparedStatement ps4 = conn.prepareStatement(query4);
				PreparedStatement ps5 = conn.prepareStatement(query5);
				
				ps.setInt(1, user_id);
				ps.setInt(2, user_id);
				
				ResultSet rs = ps.executeQuery();
				ResultSet rs2 = null;
				
				ps2.setInt(3, lastPostId);
				
				String result = "";
				while(rs.next()) {
					
					if(rs.getInt("first") == user_id) {
						ps2.setInt(1, rs.getInt("second"));
						ps2.setInt(2, rs.getInt("second"));
						rs2 = ps2.executeQuery();
					}
					else {
						ps2.setInt(1, rs.getInt("first"));
						ps2.setInt(2, rs.getInt("first"));
						rs2 = ps2.executeQuery();
					}
					
					while(rs2.next()) {
						ps3.setInt(1, rs2.getInt("id"));
						ResultSet rs3 = ps3.executeQuery();
						
						ps5.setInt(1, rs2.getInt("id"));
						ResultSet rs5 = ps5.executeQuery();
						
						int likes = 0;
						int comments = 0;
						
						while(rs3.next())
							likes++;
						
						while(rs5.next())
							comments++;
						
						ps4.setInt(1, rs2.getInt("id"));
						ps4.setInt(2, user_id);
						ResultSet rs4 = ps4.executeQuery();
						
						
						boolean liked = rs4.next();
						
						result += "<div class='post' data-post-id='"+rs2.getInt("id")+"' data-user-id='"+rs2.getInt("user_id")+"'>";
						result += "<div ><a class='post-author-name' href='javascript:viewAuthorProfile("+rs2.getInt("user_id")+")'>"+rs2.getString("name")+"</a><span class='right time grey-text'>"+rs2.getString("posted_at")+"</span></div>";
						result += "<div class=''>"+rs2.getString("content")+"</div><br>";
						result += "<div class=''>";
						result += "<a href="+(liked ? "'javascript:unlikePost("+rs2.getInt("id")+")'" : "'javascript:likePost("+rs2.getInt("id")+")'")+" id='likeBtn_"+rs2.getInt("id")+"' class='likes "+(liked ? "liked" : "")+"'>Likes ("+likes+") </a>";
						result += "<span class='text-lighten-2 grey-text'> | </span>";
						result += "<a href='javascript:void(0)' class='comments'>Comments ("+comments+") </a>";
						result += "</div>";
						result += "</div>";
					}
				}
				
				ps2.setInt(1, user_id);
				ps2.setInt(2, user_id);
				
				rs2 = ps2.executeQuery();
				
				while(rs2.next()) {
					ps3.setInt(1, rs2.getInt("id"));
					ResultSet rs3 = ps3.executeQuery();
					
					ps5.setInt(1, rs2.getInt("id"));
					ResultSet rs5 = ps5.executeQuery();
					
					int likes = 0, comments = 0;
					
					while(rs3.next())
						likes++;
					while(rs5.next())
						comments++;
					
					ps4.setInt(1, rs2.getInt("id"));
					ps4.setInt(2, user_id);
					ResultSet rs4 = ps4.executeQuery();
					
					boolean liked = rs4.next();
					
					
					
					result += "<div class='post' data-post-id='"+rs2.getInt("id")+"' data-user-id='"+rs2.getInt("user_id")+"'>";
					result += "<div ><a class='post-author-name' href='javascript:viewAuthorProfile("+rs2.getInt("user_id")+")'>"+rs2.getString("name")+"</a><span class='right time grey-text'>"+rs2.getString("posted_at")+"</span></div>";
					result += "<div class=''>"+rs2.getString("content")+"</div><br>";
					result += "<div class=''>";
					result += "<a href="+(liked ? "'javascript:unlikePost("+rs2.getInt("id")+")'" : "'javascript:likePost("+rs2.getInt("id")+")'")+" id='likeBtn_"+rs2.getInt("id")+"' class='likes "+(liked ? "liked" : "")+"'>Likes ("+likes+") </a>";
					result += "<span class='text-lighten-2 grey-text'> | </span>";
					result += "<a href='javascript:void(0)' class='comments'>Comments ("+comments+") </a>";
					result += "</div>";
					result += "</div>";
				}
				
				
				pw.println(result);
				
				pw.close();
				conn.close();
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
