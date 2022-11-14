package chat;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;

import lombok.Data;

@Data
//@Builder
public class chatLog implements Serializable {
	Map<String, chatLog> ChatRecord = Collections.synchronizedMap(new HashMap<>());
	private static final long serialVersionUID = 1449132512754742285L;
	public String content;
	public String chatname;
	public String writetime;

	public static class ExistContent extends Exception {
		public ExistContent(String content) {
			super(content);
		}
	}

	public static class NotExistChatName extends Exception {
		public NotExistChatName(String chatname) {
			super(chatname);
		}
	}

	public static class NotExistWriteTime extends Exception {
		public NotExistWriteTime(String writetime) {
			super(writetime);
		}
	}
	
	public chatLog(String content, String chatname, String writetime) {
		super();
		this.content = content;
		this.chatname = chatname;
		this.writetime = writetime;
	}

	public chatLog(JSONObject jsonObject) {
		content = jsonObject.getString("content");
		chatname = jsonObject.getString("chatname");
		writetime = jsonObject.getString("writetime");
	}

	public chatLog() {
	}

	public chatLog settingChatLog(String content, String chatname, String writetime) {
		this.content = content;
		this.chatname = chatname;
		this.writetime = writetime;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		chatLog other = (chatLog) obj;
		return Objects.equals(chatname, other.chatname);
	}

	@Override
	public int hashCode() {
		return Objects.hash(chatname);
	}
}