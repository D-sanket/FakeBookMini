import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.Calendar;

public class ReplyServlet extends HttpServlet {
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
				String content = request.getParameter("content");
				
				String query = "INSERT INTO replies (user_id, post_id, content, replied_at) VALUES (?, ?, ?, ?)";
				
				
				PreparedStatement ps = conn.prepareStatement(query);
				
				ps.setInt(1, user_id);
				ps.setInt(2, post_id);
				ps.setString(3, content);
				
				Calendar c = Calendar.getInstance();
				int h = c.get(Calendar.HOUR_OF_DAY);
				int m = c.get(Calendar.MINUTE);
				int s = c.get(Calendar.SECOND);
				int Y = c.get(Calendar.YEAR);
				int M = c.get(Calendar.MONTH);
				int D = c.get(Calendar.DAY_OF_MONTH);
				
				String date = Y+"-"+(M > 9 ? M : "0"+M)+"-"+(D > 9 ? D : "0"+D)+" "+(h > 9 ? h : "0"+h)+":"+(m > 9 ? m : "0"+m)+":"+(s > 9 ? s : "0"+s);
				
				ps.setString(4, date);
				
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
