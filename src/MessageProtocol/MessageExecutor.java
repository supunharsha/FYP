package MessageProtocol;

import java.util.Iterator;
import java.util.LinkedList;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;

import MessageProtocol.MessageBrocker.Interrupt;
import Test.WebSocketServer;

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
	public void onClose(Session session){
		removeAgent(session);		   
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
	public void onError(Throwable e, Session session){
		
	}
	
	public void removeAgent(Session session){		
		Iterator<Session> it = MessageCommonData.agentList.values().iterator();			
		it.next();
		int i = 1;
		while (it.hasNext())
		{			
			Session sess = it.next();
			if(sess.equals(session)){
				
				JSONObject obj = new JSONObject();
				try {
					obj.append("Message", MessageCommonData.Message.AGENT_COMMUNICATION_STOPPED);
					obj.append("AgentId",MessageCommonData.agentList.keySet().toArray()[i]);
					MessageCommonData.agentList.remove(MessageCommonData.agentList.keySet().toArray()[i]);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}				
				WebSocketServer.sendToAll(obj.toString());
				break;
			}
			i++;
		}
	}
}




