import java.sql.*;
import java.util.Calendar;

public class Notifier {
	public static int NEW_REPLY = 1, NEW_LIKE = 2;
	public static void notify(int user_id, int post_id, String name, int notification_type) throws SQLException {
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		String url = "jdbc:mysql://localhost:3306/fakebook";
		String user = "root";
		String pass = "";
		Connection conn = DriverManager.getConnection(url, user, pass);
		String query = "INSERT INTO notifications (user_id, post_id, title, notified_at, type) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setInt(1, user_id);
		ps.setInt(2, post_id);
		
		Calendar c = Calendar.getInstance();
		int h = c.get(Calendar.HOUR_OF_DAY);
		int m = c.get(Calendar.MINUTE);
		int s = c.get(Calendar.SECOND);
		int Y = c.get(Calendar.YEAR);
		int M = c.get(Calendar.MONTH);
		int D = c.get(Calendar.DAY_OF_MONTH);
		
		String date = Y+"-"+(M > 9 ? M : "0"+M)+"-"+(D > 9 ? D : "0"+D)+" "+(h > 9 ? h : "0"+h)+":"+(m > 9 ? m : "0"+m)+":"+(s > 9 ? s : "0"+s);
		
		ps.setString(4, date);
		switch(notification_type) {
		case 1:
			ps.setString(3, name+" replied to your status.");
			ps.setInt(5, 1);
			break;
		case 2:
			ps.setString(3, name+" liked your status.");
			ps.setInt(5, 2);
			break;
		}
		
		int count = ps.executeUpdate();
	}
}
