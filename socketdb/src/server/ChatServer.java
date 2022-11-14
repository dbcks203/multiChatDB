package server;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chat.ChattingRepositoryDB;
import member.Member;
import member.MemberRepository;

public class ChatServer {
	// 필드
	ServerSocket serverSocket;
	ExecutorService threadPool = Executors.newFixedThreadPool(100);
	ChattingRepositoryDB chattingRepositoryDB = new ChattingRepositoryDB();
	RoomManager roomManager = new RoomManager();
	CommandManager commandManager = new CommandManager();
	MemberRepository memberRepository;

	// 메소드: 서버 시작
	public void start() throws Exception {
		Properties prop = new Properties();
		prop.load(new FileInputStream(new File("db.properties")));
		Class cls = Class.forName(prop.getProperty("MemberRepository"));
		memberRepository = (MemberRepository) cls.newInstance();
		memberRepository.loadMember();

		serverSocket = new ServerSocket(50001);
		System.out.println("[서버] 시작됨");


		Thread thread = new Thread(() -> {
			try {
				while (true) {
					Socket socket = serverSocket.accept();
					new SocketClient(this, socket, roomManager);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}



	// 메소드: 서버 종료
	public void stop() {
		try {
			serverSocket.close();
			threadPool.shutdownNow();
			System.out.println("[서버] 종료됨 ");
		} catch (IOException e1) {
		}
	}

	public synchronized void registerMember(Member member) throws Exception {
		try {
			memberRepository.insertMember(member);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void deleteMemberInfo(Member member) throws Exception {
		try {
			memberRepository.memberDelete(member);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized Member findByUid(String uid) throws Exception {
		return memberRepository.findByUid(uid);
	}

	public synchronized void selectMember(Member member) throws Exception {
		try {
			memberRepository.memberInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 메소드: 메인
	public static void main(String[] args) throws Exception {
		try {
			ChatServer chatServer = new ChatServer();
			chatServer.start();

			System.out.println("----------------------------------------------------");
			System.out.println("[종료커맨드 : 'q' or 'Q']");
			System.out.println("----------------------------------------------------");

			Scanner scanner = new Scanner(System.in);
			while (true) {
				String key = scanner.nextLine();
				if (key.equals("q"))
					break;
			}
			scanner.close();
			chatServer.stop();
		} catch (IOException e) {
			System.out.println("[서버] " + e.getMessage());
		}
	}
}