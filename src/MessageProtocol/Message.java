package MessageProtocol;

import javax.websocket.Session;

public class Message {
	
	String sender;
	String reciever; 
	String tag; 
	String message;
	String timeStamp;
	String communicationGroup;
	String priority;
	Session session;
	
	
	public Message(String sender, String reciever, String tag, String message, String timeStamp, String communicationGroup, String priority,Session session){
		this.sender 			= sender;
		this.reciever 			= reciever;
		this.tag				= tag;
		this.message 			= message;
		this.timeStamp			= timeStamp;
		this.communicationGroup = communicationGroup;
		this.priority			= priority;
		this.session			= session;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReciever() {
		return reciever;
	}

	public void setReciever(String reciever) {
		this.reciever = reciever;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getCommunicationGroup() {
		return communicationGroup;
	}

	public void setCommunicationGroup(String communicationGroup) {
		this.communicationGroup = communicationGroup;
	}
	

}
