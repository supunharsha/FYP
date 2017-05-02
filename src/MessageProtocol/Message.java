package MessageProtocol;

public class Message {
	
	String sender;
	String reciever; 
	String tag; 
	String message;
	String communicationGroup;
	
	public Message(String sender, String reciever, String tag, String message, String communicationGroup){
		this.sender 			= sender;
		this.reciever 			= reciever;
		this.tag				= tag;
		this.message 			= message;
		this.communicationGroup = communicationGroup;
	}

}
