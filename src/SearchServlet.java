import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.*;
import javax.servlet.http.*;

public class SearchServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			
			response.setContentType("text/plain");
			PrintWriter pw = response.getWriter();
			
			if(request.getSession(false) == null) {
				pw.println("Log in first!");
				return;
			}
			int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			String url = "jdbc:mysql://localhost:3306/fakebook";
			String user = "root";
			String pass = "";
			Connection conn = DriverManager.getConnection(url, user, pass);
			
			String searchQuery = request.getParameter("q");
			
			String query = "SELECT * FROM users WHERE id != '"+user_id+"' AND name LIKE '%"+searchQuery+"%' OR email LIKE '%"+searchQuery+"%'";
			
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			boolean flag = false;
		
			int count = 0;
			
			String query2 = "SELECT * FROM friends WHERE first = ? AND second = ? AND is_deleted = 0";
			PreparedStatement ps2 = conn.prepareStatement(query2);
			ResultSet rs2 = null;
			ResultSet rs3 = null;
			
		  if(rs.next()) {
			  if(user_id != rs.getInt("id")) {
				  flag = true;
				  boolean is_requested = false;
				  boolean did_request = false;
				  boolean is_confirmed = false;
				  ps2.setInt(1, user_id);
				  ps2.setInt(2, rs.getInt("id"));
				  rs2 = ps2.executeQuery();
				  
				  if(rs2.next()) {
					  is_requested = true;
					  if(rs2.getBoolean("is_confirmed"))
						  is_confirmed = true;
					  if(rs2.getInt("first") == user_id)
						  did_request = true;
				  }
				  ps2.setInt(1, rs.getInt("id"));
				  ps2.setInt(2, user_id);
				  rs3 = ps2.executeQuery();
				  if(rs3.next()) {
					  is_requested = true;
					  if(rs3.getBoolean("is_confirmed"))
						  is_confirmed = true;
					  if(rs3.getInt("first") == user_id)
						  did_request = true;
				  }
				  
				  pw.println("<div class='row'>");
				  pw.println("<div class='search-result-item first col s12'>");
				  pw.println("<div class='detail col s8'>");
				  pw.println("<b class='blue-grey-text text-darken-3'>"+rs.getString("name")+"</b>");
				  pw.println("</div>");
				  pw.println("<div class='action col s4'>");
				  if(!is_requested)
					  pw.println("<a id='btn"+count+"' href=\"javascript:addFriend("+rs.getInt("id")+", "+count+")\" class='btn btn-flat btn-request'><i class='material-icons'>person_add</i></a>");
				  else if(did_request && !is_confirmed)
					  pw.println("<a id='btn"+count+"' href=\"javascript:cancelRequest("+rs.getInt("id")+", "+count+")\" class='btn btn-flat btn-cancel'><i class='material-icons'>close</i></a>");
				  else if(!is_confirmed)
					  pw.println("<a id='btn"+count+"' href=\"javascript:confirmRequest("+rs.getInt("id")+", "+count+")\" class='btn btn-flat btn-confirm'><i class='material-icons'>done</i></a>");
				  else
					  pw.println("<a id='btn"+count+"' href=\"javascript:unfriend("+rs.getInt("id")+", "+count+")\" class='btn btn-flat btn-unfriend'><i class='material-icons'>remove</i></a>");
				  pw.println("</div>");
				  pw.println("</div>");
				  count++;
			  }
			  
		  }
			
			while(rs.next()) {
				if(rs.getInt("id") != user_id) {
					flag = true;
					  boolean is_requested = false;
					  boolean did_request = false;
					  boolean is_confirmed = false;
					  ps2.setInt(1, user_id);
					  ps2.setInt(2, rs.getInt("id"));
					  rs2 = ps2.executeQuery();
					  
					  if(rs2.next()) {
						  is_requested = true;
						  if(rs2.getBoolean("is_confirmed"))
							  is_confirmed = true;
						  if(rs2.getInt("first") == user_id)
							  did_request = true;
					  }
					  
					  ps2.setInt(1, rs.getInt("id"));
					  ps2.setInt(2, user_id);
					  rs3 = ps2.executeQuery();
					  
					  if(rs3.next()) {
						  is_requested = true;
						  if(rs3.getBoolean("is_confirmed"))
							  is_confirmed = true;
						  if(rs3.getInt("first") == user_id)
							  did_request = true;
					  }
					  
					  pw.println("<div class='row'>");
					  pw.println("<div class='search-result-item first col s12'>");
					  pw.println("<div class='detail col s8'>");
					  pw.println("<b class='blue-grey-text text-darken-3'>"+rs.getString("name")+"</b>");
					  pw.println("</div>");
					  pw.println("<div class='action col s4'>");
					  if(!is_requested)
						  pw.println("<a id='btn"+count+"' href=\"javascript:addFriend("+rs.getInt("id")+", "+count+")\" class='btn btn-flat btn-request'><i class='material-icons'>person_add</i></a>");
					  else if(did_request && !is_confirmed)
						  pw.println("<a id='btn"+count+"' href=\"javascript:cancelRequest("+rs.getInt("id")+", "+count+")\" class='btn btn-flat btn-cancel'><i class='material-icons'>close</i></a>");
					  else if(!is_confirmed)
						  pw.println("<a id='btn"+count+"' href=\"javascript:confirmRequest("+rs.getInt("id")+", "+count+")\" class='btn btn-flat btn-confirm'><i class='material-icons'>done</i></a>");
					  else
						  pw.println("<a id='btn"+count+"' href=\"javascript:unfriend("+rs.getInt("id")+", "+count+")\" class='btn btn-flat btn-unfriend'><i class='material-icons'>remove</i></a>");
					  pw.println("</div>");
					  pw.println("</div>");
					  count++;
				}
			}
			
			if(flag == true)
				pw.println("</div>");
			
			rs.close();
			rs2.close();
			rs3.close();
			conn.close();
			pw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}