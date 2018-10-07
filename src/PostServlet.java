import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.Calendar;

public class PostServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			
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
				String content = "";
				if(request.getParameter("content") != null) {
					content += request.getParameter("content");
				}
				
				if(content.length() < 5){
					pw.println("Content cannot be less than 5 characters!");
					pw.close();
					conn.close();
					return;
				}
				
				String query = "INSERT INTO posts (user_id, content, posted_at) VALUES(?, ?, ?)";
				
				PreparedStatement ps = conn.prepareStatement(query);
				
				Calendar c = Calendar.getInstance();
				int h = c.get(Calendar.HOUR_OF_DAY);
				int m = c.get(Calendar.MINUTE);
				int s = c.get(Calendar.SECOND);
				int Y = c.get(Calendar.YEAR);
				int M = c.get(Calendar.MONTH);
				int D = c.get(Calendar.DAY_OF_MONTH);
				
				String date = Y+"-"+(M > 9 ? M : "0"+M)+"-"+(D > 9 ? D : "0"+D)+" "+(h > 9 ? h : "0"+h)+":"+(m > 9 ? m : "0"+m)+":"+(s > 9 ? s : "0"+s);
				
				ps.setInt(1, user_id);
				ps.setString(2, content);
				
				ps.setString(3, date);
				
				int count = ps.executeUpdate();
				
				if(count == 0) {
					pw.println("Failed to post!");
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
