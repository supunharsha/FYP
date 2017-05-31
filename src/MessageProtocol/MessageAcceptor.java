package MessageProtocol;

import javax.websocket.Session;

import org.json.*;

public class MessageAcceptor {
	
	enum MessagePacket{
		;
		enum Header{
			Sender,
			Reciever,
			TimeStamp,
			Tag,
			Priority,
			CoomunicatorGroup			
		}
		enum Body{
			Message
		}	
	}
	
	String header = "";
	String body = "";
	
	public boolean sendMessage(Message msg){
		JSONObject messagePacket = new JSONObject();
		JSONObject header = new JSONObject();
		JSONObject body = new JSONObject();
		
		try {
			
			header.append(MessagePacket.Header.Sender.name(), msg.getSender());
			header.append(MessagePacket.Header.Reciever.name(), msg.getReciever());
			header.append(MessagePacket.Header.Tag.name(), msg.getTag());
			header.append(MessagePacket.Header.TimeStamp.name(), msg.getTimeStamp());
			header.append(MessagePacket.Header.Priority.name(), msg.getPriority());
			header.append(MessagePacket.Header.CoomunicatorGroup.name(), msg.getCommunicationGroup());
			
			body.append(MessagePacket.Body.Message.name(), msg.getMessage());
			messagePacket.append("Header", header);
			messagePacket.append("Body", body);			
			System.out.println(messagePacket.toString());
			msg.getSession().getBasicRemote().sendText(messagePacket.toString());
			
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;		
	}
	
	
	
	public Message extractMsg(JSONObject obj,Session session){
		try {

			Message ms = new Message(obj.getJSONObject("Header").getString(MessagePacket.Header.Sender.name()),obj.getJSONObject("Header").getString(MessagePacket.Header.Reciever.name()), obj.getJSONObject("Header").getString(MessagePacket.Header.Tag.name()), obj.getJSONObject("Body").getString(MessagePacket.Body.Message.name()), obj.getJSONObject("Header").getString(MessagePacket.Header.TimeStamp.name()), obj.getJSONObject("Header").getString(MessagePacket.Header.CoomunicatorGroup.name()),obj.getJSONObject("Header").getString(MessagePacket.Header.Priority.name()),session);
			return ms;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
