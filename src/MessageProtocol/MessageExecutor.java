package MessageProtocol;

import java.util.LinkedList;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

import MessageProtocol.MessageBrocker.Interrupt;

@ServerEndpoint("/coordinator")
public class MessageExecutor {
	
	public enum MessagePacket{
		Header,
		Sender,
		Reciever,
		TimeStamp,
		Tag,
		Priority,
		CoomunicatorGroup,
		Body,
		Message
	}
	
	
	
	void notifyBrockerForIncomingMessages(){
		while(MessageBrocker.interrupt != MessageBrocker.Interrupt.NO_EVENT);
		MessageBrocker.interrupt = Interrupt.MESSAGE_RECIEVED_FROM_AGENT;		
		MessageBrocker.Brocker.interrupt();
		
	}
	

	@OnOpen
	public void onOpen(){
		System.out.println("Open Connection ...");
	}
	
	@OnClose
	public void onClose(){
		System.out.println("Close Connection ...");
	}
	
	@OnMessage
	public void onMessage(Session session, String message){
		System.out.println("Message from the client: " + message);
		try {
		
			JSONObject msg = new JSONObject(message);
			int priority = Integer.parseInt(msg.getJSONObject(MessagePacket.Header.toString()).getString(MessagePacket.Priority.toString()));
			LinkedList<Message> arr = null;
			if(priority == MessageCommonData.Priority.LOW){				
				if(MessageCommonData.msgQueue.containsKey(MessageCommonData.Priority.LOW)){
					arr = MessageCommonData.msgQueue.get(MessageCommonData.Priority.LOW);
				}else{
					arr = new LinkedList<Message>();
					MessageCommonData.msgQueue.put(MessageCommonData.Priority.LOW, arr);
				}
				
			}else if(priority == MessageCommonData.Priority.NORMAL){
				
				if(MessageCommonData.msgQueue.containsKey(MessageCommonData.Priority.NORMAL)){
					arr = MessageCommonData.msgQueue.get(MessageCommonData.Priority.NORMAL);
				}else{
					arr = new LinkedList<>();
					MessageCommonData.msgQueue.put(MessageCommonData.Priority.NORMAL, arr);
				}
				
				
			}else{
				if(MessageCommonData.msgQueue.containsKey(MessageCommonData.Priority.HIGH)){
					arr = MessageCommonData.msgQueue.get(MessageCommonData.Priority.HIGH);
				}else{
					arr = new LinkedList<>();
					MessageCommonData.msgQueue.put(MessageCommonData.Priority.HIGH, arr);
				}				
			}
			MessageAcceptor ma = new MessageAcceptor();			
			arr.add(ma.extractMsg(msg, session));
			notifyBrockerForIncomingMessages();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@OnError
	public void onError(Throwable e){
		e.printStackTrace();
	}
}




