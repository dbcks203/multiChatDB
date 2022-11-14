package server;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.json.JSONObject;


interface CommandControl{
}

public class CommandManager {
	
	private Map<String, Method> actionUserManagement;
	private CommandControl commandControl;
	private static MemberCommand memberCommand;
	private static ChatCommand chatCommand;
	private static FileCommand fileCommand;
	
	enum Command{
		
		MEMBER_COMMAND{
			CommandControl changeCommand() {return memberCommand;};
			String setMethodManager() {return "memberCommandManagement.";};
		},
		CHAT_COMMAND{
			CommandControl changeCommand() {return chatCommand;};
			String setMethodManager() {return "chatCommandManagement.";};
		},
		FILE_COMMAND{
			CommandControl changeCommand() {return fileCommand;};
			String setMethodManager() {return "fileCommandManagement.";};
		};
		
		abstract CommandControl changeCommand();
		abstract String setMethodManager();
	}

	public CommandManager(){
		
		memberCommand= new MemberCommand();
		chatCommand = new ChatCommand();
		fileCommand = new FileCommand();
		actionUserManagement = new HashMap<>(); 
		
		for(Command command: Command.values()) {
			commandControl= command.changeCommand();
			Class cls = commandControl.getClass();
			try {
				setCommand(cls,command);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void setCommand(Class cls, Command command) throws Exception{
		
		Properties prop = new Properties();
		prop.load(new FileInputStream(new File("db.properties")));
		final int count = Integer.parseInt(prop.getProperty(command.setMethodManager()+"count"));
		
		for (int i=1;i<=count;i++) {
			String methodKey = command.setMethodManager()+i;
			String methodName = prop.getProperty(methodKey);
			Method method = cls.getMethod(methodName,new Class[] {SocketClient.class,JSONObject.class});
			actionUserManagement.put(methodName, method);
		}
	}

	public boolean setActiveCommand(SocketClient sc, JSONObject jsonObject) throws Exception {
		Object ret=null;
		
		if(jsonObject.has("memberCommand")) {
			String command = jsonObject.getString("memberCommand");
			ret = actionUserManagement.get(command).invoke(memberCommand,sc,jsonObject);
		}

		else if(jsonObject.has("chatCommand")){
			String command = jsonObject.getString("chatCommand");
			ret = actionUserManagement.get(command).invoke(chatCommand,sc,jsonObject);
		}

		else if(jsonObject.has("fileCommand")) {
			String command = jsonObject.getString("fileCommand");
			ret = actionUserManagement.get(command).invoke(fileCommand,sc,jsonObject);
		}

		if (ret instanceof Boolean) {
			if (((Boolean)ret).booleanValue()) {
				return true;
			}
		}
		return false;
	}
}
