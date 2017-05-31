package Coordinator;

import java.util.Date;

import org.json.JSONObject;

import MessageProtocol.Message;
import MessageProtocol.MessageBrocker;
import MessageProtocol.MessageBrocker.Interrupt;
import MessageProtocol.MessageCommonData;

public class ServeMessage implements Runnable {

	Message msg;

	public ServeMessage(Message ms) {
		this.msg = ms;
	}

	@Override
	public void run() {
			
		if (Short.parseShort(msg.getTag()) == MessageCommonData.Message.READY_TO_WORK) {
			MessageCommonData.agentList.put(msg.getSender(), msg.getSession());
			
			while (MessageBrocker.interrupt != MessageBrocker.Interrupt.NO_EVENT)
				;
			MessageBrocker.interrupt = Interrupt.NOTIFY_AGENT;
			Message ms = new Message(MessageCommonData.agentList.keySet().iterator().next(), this.msg.getSender(),String.valueOf(MessageCommonData.Message.LIST_OF_OTHER_AGENTS), MessageCommonData.agentList.keySet().toString(), new Date().toString(), String.valueOf(MessageCommonData.commGroup), String.valueOf(MessageCommonData.Priority.NORMAL),msg.getSession());
			
			MessageBrocker.ms = ms;
			MessageBrocker.Brocker.interrupt();
		}else if(Short.parseShort(msg.getTag()) == MessageCommonData.Message.LOCATION_MAP){	
			Message ms = new Message(MessageCommonData.agentList.keySet().iterator().next(), this.msg.getSender(),String.valueOf(MessageCommonData.Message.LOCATION_MAP),MessageCommonData.map, new Date().toString(), String.valueOf(MessageCommonData.commGroup), String.valueOf(MessageCommonData.Priority.NORMAL),msg.getSession());
			while (MessageBrocker.interrupt != MessageBrocker.Interrupt.NO_EVENT)
				;
			MessageBrocker.interrupt = Interrupt.NOTIFY_AGENT;
			MessageBrocker.ms = ms;
			MessageBrocker.Brocker.interrupt();
		}else if(Short.parseShort(msg.getTag()) == MessageCommonData.Message.ASSIGNED_AREA){
	
			int numberOfCluster = MessageCommonData.agentList.size()-1;
			
			if(numberOfCluster > 1){
				
			}else{
				try{
					JSONObject points = new JSONObject(MessageCommonData.map);
					Message ms = new Message(MessageCommonData.agentList.keySet().iterator().next(), this.msg.getSender(),String.valueOf(MessageCommonData.Message.ASSIGNED_AREA),points.getString("data2").toString(), new Date().toString(), String.valueOf(MessageCommonData.commGroup), String.valueOf(MessageCommonData.Priority.NORMAL),msg.getSession());
					while (MessageBrocker.interrupt != MessageBrocker.Interrupt.NO_EVENT)
						;
					MessageBrocker.interrupt = Interrupt.NOTIFY_AGENT;
					MessageBrocker.ms = ms;
					MessageBrocker.Brocker.interrupt();							
				}catch(Exception e){
					e.printStackTrace();
				}
						
			}	
			
						
		}

	}
	
	

}
