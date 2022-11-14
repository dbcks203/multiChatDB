package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.List;
import org.json.JSONObject;
import chat.ChatLogRepositoryDB;
import chat.chatLog;

public class FileCommand implements CommandControl{
	
	public boolean fileTran(SocketClient sc, JSONObject jsonObject) throws Exception {
		JSONObject json = new JSONObject();
		String fileName = jsonObject.getString("filename");
		byte[] data = Base64.getDecoder().decode(jsonObject.getString("filetrans").getBytes());

		File filePath = new File("C:\\temp\\server");
		if (!filePath.exists()) {
			filePath.mkdir();
		}
		BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream("C:\\temp\\server\\" + fileName));
		fos.write(data);
		fos.close();
		sc.send(json.toString());
		sc.close();
		
		return true;
	}

	// 파일 받기
	public boolean fileReceive(SocketClient sc, JSONObject jsonObject) throws Exception {
		JSONObject json = new JSONObject();
		String fileName = jsonObject.getString("filename");
	

		File filePath = new File("C:\\temp\\server\\" + fileName);
		if (!filePath.exists()) {
			System.out.println("파일 없음");
		} else {
			BufferedInputStream ios = new BufferedInputStream(new FileInputStream(filePath));
			byte[] data = new byte[(int) filePath.length()];
			ios.read(data);
			ios.close();
			json.put("decodeFile", new String(Base64.getEncoder().encode(data)));
			sc.send(json.toString());
		}
		sc.close();
		return true;
	}

	// 채팅 로그 출력-파일
		public boolean printChatLog(SocketClient sc, JSONObject jsonObject) throws Exception {
			JSONObject json = new JSONObject();
			String chatRoom = sc.chatTitle;
			json.put("chatTitle", chatRoom);
			System.out.println(chatRoom + " 채팅 기록");
			/*
			FileInputStream file = new FileInputStream("C:/Temp/" + chatRoom + ".db");
			Scanner scan = new Scanner(file);

			String chatms = "";
			while (scan.hasNextLine()) {
				chatms += scan.nextLine() + "\n";
			}
			json.put("chatLogReceive", chatms);
			sc.send(json.toString());
			scan.close();
			*/
			return true;
			
		}
		// 채팅 로그 출력-DB
		public boolean printChatLogDB(SocketClient sc, JSONObject jsonObject) throws Exception {
			ChatLogRepositoryDB chatLogRepositoryDB = new ChatLogRepositoryDB();
			JSONObject json = new JSONObject();
			String clientUid = jsonObject.getString("Uid");
			
			String chatRoom = sc.chatTitle;
			int chatNo = sc.roomManager.loadRoom(clientUid).no;
			//sc.chatName = jsonObject.getString("chatname");

			System.out.println(chatRoom + " 채팅 기록");
			System.out.println(chatNo);
			chatLogRepositoryDB.chatOutput(chatNo);
			
			String roomStatus = "[chatLog]\n";
			
			List<chatLog> chatList=chatLogRepositoryDB.getList();
			for (chatLog chat : chatList) {
				roomStatus += String.format("[WriteTime : %s, ChatName : %s, Content : %s]\n", chat.writetime, chat.chatname,chat.content);
			}
			json.put("chatLogReceive", roomStatus);
			sc.send(json.toString());
			
			return true;
		}

	// 파일 리스트 출력
	public boolean fileListOutput(SocketClient sc, JSONObject jsonObject) throws Exception {
		JSONObject json = new JSONObject();
		File file = new File("C:/Temp");
		String[] fileNames = file.list();
		String fileLi = "";
		for (String filename : fileNames) {
			System.out.println("filename : " + filename);
			fileLi += filename + "\n";
		}

		json.put("fileListOutputReceive", fileLi);
		sc.send(json.toString());
		
		return true;
	}

}
