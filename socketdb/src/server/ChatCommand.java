package server;

import org.json.JSONObject;

public class ChatCommand implements CommandControl {

	public ChatCommand() {
	}

	public boolean chatList(SocketClient sc, JSONObject jsonObject) throws Exception {
		// 파일
		sc.clientUid = jsonObject.getString("Uid");
		sc.room = sc.chatServer.roomManager.loadRoom(sc.clientUid);
		// DB
		sc.chatServer.chattingRepositoryDB.selectChat(sc.chatServer.roomManager);

		// roomManager.updateRoom(); /*파일*/
		String roomStatus = "[room status]\n";
		if (sc.chatServer.roomManager.rooms.size() > 0) {
			for (Room room : sc.chatServer.roomManager.rooms) {
				roomStatus += String.format("{no : %s, title : %s}\n", room.no, room.title);
			}
			roomStatus = roomStatus.substring(0, roomStatus.length() - 1);
		}

		JSONObject jsonResult = new JSONObject();
		jsonResult.put("message", roomStatus);

		sc.send(jsonResult.toString());

		sc.close();
		return true;

	}
	public boolean startChat(SocketClient sc, JSONObject jsonObject) {

		sc.clientUid = jsonObject.getString("Uid");
		sc.loadMember(sc.clientUid);
		sc.room = sc.chatServer.roomManager.loadRoom(sc.clientUid);
		sc.sendWithOutMe("님이 들어오셨습니다.");
		sc.room.clients.add(sc);
		return false;
	}
	
	public boolean sendMessage(SocketClient sc, JSONObject jsonObject) throws Exception {
		JSONObject root = new JSONObject();
		root.put("chatName", sc.chatName);
		// 출력 파일 생성
		//String chatTitle = roomManager.loadRoom(sender.clientUid).title;
		//FileWriter filewriter = new FileWriter("C:/Temp/" + chatTitle + ".db", true);
		//filewriter.write(message);
		//filewriter.flush();
		//filewriter.write("\n");
		//filewriter.close();
		String message = jsonObject.getString("data");
		if (message.indexOf("@") == 0) {
			int pos = message.indexOf(" ");
			String key = message.substring(1, pos);
			for (SocketClient c :  sc.room.clients) {
				if (key.equals(c.clientUid)) {
					message = "(귀속말)  " + message.substring(pos + 1);
					root.put("message", message);
					String json = root.toString();
					c.send(json);
				}
			}

		} else {
			sc.sendWithOutMe(message);
		}
		return false;
	}
	
	public boolean endChat(SocketClient sc, JSONObject jsonObject) {
		sc.sendWithOutMe("님이 나갔습니다.");
		return true;
	}

	public boolean removeRoom(SocketClient sc, JSONObject jsonObject) throws Exception {
		sc.clientUid = jsonObject.getString("Uid");
		sc.room = sc.chatServer.roomManager.loadRoom(sc.clientUid);
		int chatNo = Integer.parseInt(jsonObject.getString("chatNo"));
		JSONObject jsonResult = new JSONObject();
		Room target = null;
		jsonResult.put("message", "해당번호 채팅방이 존재하지 않습니다.");

		for (Room room : sc.chatServer.roomManager.rooms) {
			if (room.no == chatNo) {
				target = room;
				jsonResult.put("message", room.title + " 방을 삭제했습니다.");

			}
		}
		// 파일
		if (target != null)
			sc.chatServer.roomManager.destroyRoom(target);
		// DB
		if (target != null)
			sc.chatServer.chattingRepositoryDB.DelectChat(chatNo);
		sc.send(jsonResult.toString());

		sc.close();
		return true;

	}

	public boolean chatCreate(SocketClient sc, JSONObject jsonObject) throws Exception {
		sc.clientUid = jsonObject.getString("Uid");
		sc.room = sc.chatServer.roomManager.loadRoom(sc.clientUid);

		String chatRoomName = jsonObject.getString("chatRoomName");
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "0");

		sc.chatServer.roomManager.createRoom(chatRoomName);
		System.out.println("[채팅서버] 채팅방 개설 ");
		System.out.println("[채팅서버] 현재 채팅방 갯수 " + sc.chatServer.roomManager.rooms.size());

		jsonResult.put("message", chatRoomName + " 채팅방이 생성되었습니다.");

		sc.send(jsonResult.toString());
		sc.chatServer.chattingRepositoryDB.insertChat(chatRoomName);
		sc.close();
		return true;
	}

	public boolean chatEnter(SocketClient sc, JSONObject jsonObject) throws Exception {
		sc.clientUid = jsonObject.getString("Uid");
		sc.room = sc.chatServer.roomManager.loadRoom(sc.clientUid);

		int chatNo = Integer.parseInt(jsonObject.getString("chatNo"));
		sc.chatName = jsonObject.getString("chatname");

		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "해당번호 채팅방이 존재하지 않습니다.");

		for (Room room : sc.chatServer.roomManager.rooms) {
			if (room.no == chatNo) {
				jsonResult.put("statusCode", "0");
				jsonResult.put("message", chatNo + "번 방에 입장했습니다.");
				room.entryRoom(sc);
				sc.chatServer.chattingRepositoryDB.enterChat(chatNo,sc.clientUid,sc.chatName);
				sc.room = room;
				sc.chatTitle = room.title;
				break;
			}
		}

		sc.send(jsonResult.toString());

		sc.close();
		return true;
	}
}
