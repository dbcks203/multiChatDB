package chat;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import server.Room;
import server.RoomManager;

public class ChattingRepositoryDB {
	Map<Integer, Room> roomMap = Collections.synchronizedMap(new HashMap<>());

	Properties prop = new Properties();
	Connection conn = null;
	PreparedStatement pstmt = null;
	static int roomNumber;

	private Connection connectDB() {
		try {
			conn = DriverManager.getConnection(prop.getProperty("dbServerConn"), prop.getProperty("dbUser"),
					prop.getProperty("dbPasswd"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	//채팅방 입장
		public void enterChat(int chatNo, String userid, String chatName) throws Exception{

			prop.load(new FileInputStream("db.properties"));
			Class.forName(prop.getProperty("driverClass"));
			System.out.println("JDBC 드라이버 로딩 성공");
			conn = connectDB();
			pstmt = conn.prepareStatement(prop.getProperty("ENTER_CHAT"));
			
			pstmt.setInt(1, chatNo);
			pstmt.setString(2, userid);
			pstmt.setString(3, chatName);
			pstmt.executeUpdate();
			
	}
	// 채팅방 생성
	public void insertChat(String chatname) throws Exception {
		prop.load(new FileInputStream("db.properties"));
		Class.forName(prop.getProperty("driverClass"));
		System.out.println("JDBC 드라이버 로딩 성공");
		conn = connectDB();
		pstmt = conn.prepareStatement(prop.getProperty("SELECT_CHATNO"));
		ResultSet rs = pstmt.executeQuery();

		if (rs.next())
			roomNumber = rs.getInt("no");

		String formatedNow = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		roomNumber++;
		pstmt = conn.prepareStatement(prop.getProperty("CREATE_CHATROOM"));
		pstmt.setInt(1, roomNumber);
		pstmt.setString(2, chatname);
		pstmt.setString(3, formatedNow);
		pstmt.executeUpdate();
		

	}
	// 방 삭제
	public void DelectChat(int chatNo) throws Exception {
		prop.load(new FileInputStream("db.properties"));
		Class.forName(prop.getProperty("driverClass"));
		System.out.println("JDBC 드라이버 로딩 성공");
		conn = connectDB();

		pstmt = conn.prepareStatement(prop.getProperty("DELETE_CHATROOM"));
		pstmt.setInt(1, chatNo);
		pstmt.executeUpdate();
		// 삭제시 방번호 1씩 줄임

		pstmt = conn.prepareStatement(prop.getProperty("UPDATE_CHATNO"));
		pstmt.setInt(1, chatNo);
		pstmt.executeUpdate();
		roomNumber--;
		pstmt.close();
	}
	// 방 목록
	public void selectChat(RoomManager roomManager) throws Exception {
		prop.load(new FileInputStream("db.properties"));

		Class.forName(prop.getProperty("driverClass"));
		conn = connectDB();

		pstmt = conn.prepareStatement(prop.getProperty("SELECT_CHATROOM"));
		ResultSet rs = pstmt.executeQuery();
		roomManager.rooms.clear();
		while (rs.next()) {
			int roomNumber = rs.getInt("NO");
			String roomTitle = rs.getString("TITLE");
			Date roomDate = rs.getDate("CREATEDATE");
			Room room = new Room(roomManager, roomNumber, roomTitle, roomDate);

			roomMap.put(roomNumber, room);
			roomManager.rooms.add(room);
		}

		rs.close();
	}
}