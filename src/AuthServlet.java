
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class AuthServlet extends HttpServlet{

	
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			if(request.getSession(false) != null) {
				response.sendRedirect("./dashboard");
				return;
			}
			response.setContentType("text/html");
			RequestDispatcher rd = request.getRequestDispatcher("./auth.html");
			
			rd.forward(request, response);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("text/html");
			
			PrintWriter pw = response.getWriter();
			
			if(request.getSession(false) != null) {
				response.sendRedirect("./dashboard");
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
				
				if(request.getParameter("submit").equals("login")) {
					String email = request.getParameter("email");
					String password = request.getParameter("password");
					int e = 0;

					if((email.equals("") || email == null) && (password.equals("") || password == null)) {
						e = 1;
					}
					else if(password.equals("") || password == null) {
						e = 2;
					}
					else if(email.equals("") || email == null) {
						e = 3;
					}
					if(e != 0) {
						response.sendRedirect(referer+"?e="+e+"#login");
						return;
					}
					
					String query = "SELECT * FROM users WHERE email = ? AND password = ?";
					PreparedStatement ps = conn.prepareStatement(query);
					ps.setString(1, email);
					ps.setString(2, password);
					
					ResultSet rs = ps.executeQuery();
					
					if(rs.next()) {
						HttpSession s = request.getSession();
						s.setAttribute("user_name", rs.getString("name"));
						s.setAttribute("user_id", rs.getString("id"));
						s.setAttribute("user_email", rs.getString("email"));
						
						response.sendRedirect("./dashboard");
						return;
					}
					else {
						response.sendRedirect(referer+"?e=7");
					}
				}
				else {
					String name = request.getParameter("name");
					String email = request.getParameter("email");
					String password = request.getParameter("password");
					int e = 0;

					if((name.equals("") || name == null) || (email.equals("") || email == null) && (password.equals("") || password == null)) {
						e = 4;
					}
					
					if(e != 0) {
						response.sendRedirect(referer+"?e="+e+"#register");
					}
					else {
						String query = "SELECT * FROM users WHERE email = ?";
						PreparedStatement ps = conn.prepareStatement(query);
						ps.setString(1, email);
						ResultSet rs = ps.executeQuery();
						
						if(rs.next()) {
							response.sendRedirect(referer+"?e="+5+"#register");
						}
						else {
							query = "INSERT INTO users (name, email, password) VALUES(?, ?, ?)";
							ps = conn.prepareStatement(query);
							ps.setString(1, name);
							ps.setString(2, email);
							ps.setString(3, password);
							
							int c = ps.executeUpdate();
							
							if(c == 1) {
								response.sendRedirect(referer+"?e="+6);
							}
							else {
								response.sendRedirect(referer+"?e="+(-1)+"#register");
							}
						}
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
