package chat;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ChatLogRepositoryDB {
	List<chatLog> chatlogList = new ArrayList<>();
	Properties prop = new Properties();
	Connection conn = null;
	PreparedStatement pstmt = null;

//	public ChatLogRepositoryDB(){
//		List<chatLog> chatlogList = new ArrayList<>();
//	}
	private Connection connectDB() {
		try {
			conn = DriverManager.getConnection(prop.getProperty("dbServerConn"), prop.getProperty("dbUser"),
					prop.getProperty("dbPasswd"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	// 채팅 로그 입력
	public void chatInput(String content, String Uid) throws Exception {

		int roomNumber = 0;
		String chatName = null;

		prop.load(new FileInputStream("db.properties"));
		Class.forName(prop.getProperty("driverClass"));
		conn = connectDB();

		pstmt = conn.prepareStatement(prop.getProperty("SELECT_CHATNO_AND_CHATNAME"));
		pstmt.setString(1, Uid);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			roomNumber = Integer.parseInt(rs.getString("no"));
			chatName = rs.getString("chatName");
		}

		pstmt = conn.prepareStatement(prop.getProperty("INSERT_CHAT"));

		SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = now.format(System.currentTimeMillis());

		pstmt.setInt(1, roomNumber);
		pstmt.setString(2, content);
		pstmt.setString(3, chatName);
		pstmt.setString(4, nowTime);
		pstmt.executeQuery();
	}

	// 채팅 로그 출력
	public void chatOutput(int chatNo) throws Exception {
		prop.load(new FileInputStream("db.properties"));
		Class.forName(prop.getProperty("driverClass"));
		conn = connectDB();
		pstmt = conn.prepareStatement(prop.getProperty("SELECT_CHATLOG"));
		pstmt.setInt(1, chatNo);
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			chatLog chat = new chatLog();
			chat.content = rs.getString("content");
			chat.chatname = rs.getString("chatname");
			chat.writetime = rs.getString("writetime");
			System.out.println(chat.content);

			chatlogList.add(chat);
		}
		rs.close();
	}

	public List<chatLog> getList() {
		return chatlogList;
	}
}