package server;

import java.net.Socket;
import java.util.List;
import org.json.JSONObject;
import member.Member;

public class MemberCommand implements CommandControl{
	
	public MemberCommand(){
		
	}

	public boolean memberInfo(SocketClient sc, JSONObject jsonObject) {
		JSONObject jsonResult = new JSONObject();
		System.out.println("hi");
		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {

			String memberData = "[INFO]\n";

			List<Member> memberList = sc.chatServer.memberRepository.getList();
			for (Member member : memberList) {
				memberData += String.format("[id : %s, pwd : %s, name : %s, sex : %s, address : %s, phone : %s]\n",
						member.uid, member.pwd, member.name, member.sex, member.address, member.phone);
			}

			jsonResult.put("statusCode", "0");
			jsonResult.put("message", memberData);

		} catch (Exception e) {
			e.printStackTrace();
		}

		sc.send(jsonResult.toString());

		sc.close();
		return true;
	}

	public boolean memberDelete(SocketClient sc, JSONObject jsonObject) {
		Member member = new Member(jsonObject);

		JSONObject jsonResult = new JSONObject();
		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {
			sc.chatServer.memberRepository.memberDelete(member);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", member.getUid() + "  탈퇴했습니다.");

			sc.send(jsonResult.toString());

			sc.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	   public void registerMember(SocketClient sc,JSONObject jsonObject) {
		      Member member = new Member(jsonObject);

		      JSONObject jsonResult = new JSONObject();
		      jsonResult.put("statusCode", "-1");
		      jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		      try {
		            boolean result = sc.chatServer.memberRepository.memberCheck(member);
		            if(result==true) {
		            	sc.chatServer.registerMember(member);
		               jsonResult.put("statusCode", "0");
		               jsonResult.put("message", member.getUid() +"님 환영합니다.");   
		            }else {
		                
		            	sc.chatServer.registerMember(member);
		               jsonResult.put("statusCode", "-1");
		               jsonResult.put("message", "중복된 아이디입니다. ");
		            }      

		      } catch (Exception e) {
		         e.printStackTrace();
		      }

		      sc.send(jsonResult.toString());

		      sc.close();

		   }

	public boolean updateMember(SocketClient sc, JSONObject jsonObject) {
		Member member = new Member(jsonObject);
		System.out.println(jsonObject);
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {
			sc.chatServer.memberRepository.findByUid(member.getUid());
			sc.chatServer.memberRepository.updateMember(member);
			jsonResult.put("statusCode", "0");
			jsonResult.put("message", "회원정보수정이 정상으로 처리되었습니다");
		} catch (Exception e) {
			e.printStackTrace();
		}

		sc.send(jsonResult.toString());

		sc.close();
		return true;

	}

	public boolean login(SocketClient sc, JSONObject jsonObject) {
		
		String uid = jsonObject.getString("uid");
		String pwd = jsonObject.getString("pwd");
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {
			Member member = sc.chatServer.findByUid(uid);
			if (null != member && pwd.equals(member.getPwd())) {
				jsonResult.put("statusCode", "0");
				jsonResult.put("message", "로그인 성공");
				jsonResult.put("name", member.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		sc.send(jsonResult.toString());

		sc.close();
		return true;
	}

	public boolean passwdSearch(SocketClient sc, JSONObject jsonObject) {
		String uid = jsonObject.getString("uid");
		JSONObject jsonResult = new JSONObject();

		jsonResult.put("statusCode", "-1");
		jsonResult.put("message", "로그인 아이디가 존재하지 않습니다");

		try {
			Member member = sc.chatServer.findByUid(uid);
			if (null != member) {
				jsonResult.put("statusCode", "0");
				jsonResult.put("message", "비밀번호 찾기 성공");
				jsonResult.put("pwd", member.getPwd());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		sc.send(jsonResult.toString());

		sc.close();
		return true;
	}
}
