import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class InfoServlet extends HttpServlet {
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
				
				String query = "SELECT * FROM users WHERE id = ?";
				PreparedStatement ps = conn.prepareStatement(query);
				
				ps.setInt(1, user_id);
				
				ResultSet rs = ps.executeQuery();
				
				if(rs.next()) {	
					String name = rs.getString("name");
					String fname = name.substring(0, name.indexOf(" "));
					String lname = name.substring(name.indexOf(" "));
					pw.println("<div class='name center'><span class='center'>"+fname+"</span>&nbsp;<span class='center'>"+lname+"</span></div>");
					pw.println("<div class='email center grey-text'>"+rs.getString("email")+"</div>");
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
