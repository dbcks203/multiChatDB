package client;

import java.util.Scanner;

import org.json.JSONObject;

import member.Member;

public class ClientControlMember extends ChatClient {

	private Scanner scanner = null;
	Member member;

	static interface LoginListener {
		void afterLogin();
	}

	static interface ExitListener {
		void afterExit();
	}

	LoginListener loginListener = null;
	ExitListener exitListener = null;

	public ClientControlMember(Scanner scanner, Member member, LoginListener loginListener, ExitListener exitListener) {
		this.scanner = scanner;
		this.member = member;
		this.loginListener = loginListener;
		this.exitListener = exitListener;
	}

	public void login() {
		try {
			String uid;
			String pwd;
			System.out.println("\n1. 로그인 작업");
			System.out.print("아이디 : ");
			uid = scanner.nextLine();
			System.out.print("비밀번호 : ");
			pwd = scanner.nextLine();

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("memberCommand", "login");
			jsonObject.put("uid", uid);
			jsonObject.put("pwd", pwd);

			send(jsonObject.toString());

			loginResponse(uid, pwd);



			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loginResponse(String uid, String pwd) throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println("로그인 성공");
			System.out.println(uid + "님이 로그인 하셨습니다.");
			member = member.settingMember(uid, pwd, root.getString("name"));

			if (loginListener != null) {
				loginListener.afterLogin();
			}

		} else {
			System.out.println(message);
		}
	}

	// 회원가입
	public void registerMember() {
		String uid;
		String pwd;
		String name;
		String sex;
		String address;
		String phone;
		try {
			System.out.println("[2]회원가입");
			System.out.print("아이디 : ");
			uid = scanner.nextLine();
			System.out.print("비번 : ");
			pwd = scanner.nextLine();
			System.out.print("이름 : ");
			name = scanner.nextLine();
			System.out.print("성별[M/F] : ");
			sex = scanner.nextLine();
			System.out.print("주소 : ");
			address = scanner.nextLine();
			System.out.print("핸드폰 : ");
			phone = scanner.nextLine();
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("memberCommand", "registerMember");
			jsonObject.put("uid", uid);
			jsonObject.put("pwd", pwd);
			jsonObject.put("name", name);
			jsonObject.put("sex", sex);
			jsonObject.put("address", address);
			jsonObject.put("phone", phone);
			String json = jsonObject.toString();

			send(json);

			registerMemberResonse();

			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void registerMemberResonse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println(message);
		} else {
			System.out.println(message);
		}
	}

	public void passwdSearch() {
		try {
			String uid;

			System.out.println("\n3. 비밀번호 찾기");
			System.out.print("아이디 : ");
			uid = scanner.nextLine();

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("memberCommand", "passwdSearch");
			jsonObject.put("uid", uid);
			String json = jsonObject.toString();
			send(json);

			passwdSearchResponse();

			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void passwdSearchResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println("비밀번호 : " + root.getString("pwd"));
			System.out.println("정상적으로 실행되었습니다");
		} else {
			System.out.println(message);
		}
	}

	public void updateMember() {
		String uid;
		String pwd;
		String name;
		String sex;
		String address;
		String phone;

		try {
			System.out.println("[4]회원정보수정");
			System.out.println("변경할 회원정보를 입력하세요.");
			System.out.print("아이디 : ");
			uid = scanner.nextLine();
			System.out.print("비번 : ");
			pwd = scanner.nextLine();
			System.out.print("이름 : ");
			name = scanner.nextLine();
			System.out.print("성별[M/F] : ");
			sex = scanner.nextLine();
			System.out.print("주소 : ");
			address = scanner.nextLine();
			System.out.print("핸드폰 : ");
			phone = scanner.nextLine();
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("memberCommand", "updateMember");
			jsonObject.put("uid", uid);
			jsonObject.put("pwd", pwd);
			jsonObject.put("name", name);
			jsonObject.put("sex", sex);
			jsonObject.put("address", address);
			jsonObject.put("phone", phone);
			String json = jsonObject.toString();
			send(json);

			updateMemberResponse();

			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateMemberResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println(message);
		} else {
			System.out.println(message);
		}
	}

	public void memberDelete() {
		String uid;
		String pwd;

		try {
			System.out.println("[5]회원탈퇴");
			System.out.print("아이디 : ");
			uid = scanner.nextLine();
			System.out.print("비번 : ");
			pwd = scanner.nextLine();

			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("memberCommand", "memberDelete");
			jsonObject.put("uid", uid);
			jsonObject.put("pwd", pwd);
			jsonObject.put("name", "");
			jsonObject.put("sex", "");
			jsonObject.put("address", "");
			jsonObject.put("phone", "");
			String json = jsonObject.toString();
			send(json);

			updateMemberResponse();

			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void memberDeleteResponse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println(message);
		} else {
			System.out.println(message);
		}
	}

	public void memberInfo() {
		try {
			System.out.println("[회원목록]");
			connect();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("memberCommand", "memberInfo");
			String json = jsonObject.toString();

			send(json);

			memberInfoResonse();
			disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void memberInfoResonse() throws Exception {
		String json = dis.readUTF();
		JSONObject root = new JSONObject(json);
		String statusCode = root.getString("statusCode");
		String message = root.getString("message");

		if (statusCode.equals("0")) {
			System.out.println(message);
		} else {
			System.out.println(message);
		}
	}

	public boolean memberExit() {
		if (exitListener != null) {
			exitListener.afterExit();
		}
		return true;
	}
}
