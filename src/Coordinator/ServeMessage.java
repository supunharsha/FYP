package Coordinator;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import MessageProtocol.Message;
import MessageProtocol.MessageBrocker;
import MessageProtocol.MessageBrocker.Interrupt;
import MessageProtocol.MessageCommonData;
import Test.WebSocketServer;

public class ServeMessage implements Runnable {

	Message msg;

	public ServeMessage(Message ms) {
		this.msg = ms;
	}

	@Override
	public void run() {

		if (Short.parseShort(msg.getTag()) == MessageCommonData.Message.READY_TO_WORK) {
			MessageCommonData.agentList.put(msg.getSender(), msg.getSession());
			
			JSONObject clientMsg = new JSONObject();
			try {
				clientMsg.append("Message",MessageCommonData.Message.READY_TO_WORK);
				clientMsg.append("AgentId", msg.getSender());
			} catch (JSONException e) {
				e.printStackTrace();
			}			
			Test.WebSocketServer.sendToAll(clientMsg.toString());

			while (MessageBrocker.interrupt != MessageBrocker.Interrupt.NO_EVENT)
				;
			MessageBrocker.interrupt = Interrupt.NOTIFY_AGENT;
			Message ms = new Message(MessageCommonData.agentList.keySet().iterator().next(), this.msg.getSender(),
					String.valueOf(MessageCommonData.Message.LIST_OF_OTHER_AGENTS),
					MessageCommonData.agentList.keySet().toString(), new Date().toString(),
					String.valueOf(MessageCommonData.commGroup), String.valueOf(MessageCommonData.Priority.NORMAL),
					msg.getSession());

			MessageBrocker.ms = ms;
			MessageBrocker.Brocker.interrupt();
		} else if (Short.parseShort(msg.getTag()) == MessageCommonData.Message.LOCATION_MAP) {
			Message ms = new Message(MessageCommonData.agentList.keySet().iterator().next(), this.msg.getSender(),
					String.valueOf(MessageCommonData.Message.LOCATION_MAP), MessageCommonData.map,
					new Date().toString(), String.valueOf(MessageCommonData.commGroup),
					String.valueOf(MessageCommonData.Priority.NORMAL), msg.getSession());
			while (MessageBrocker.interrupt != MessageBrocker.Interrupt.NO_EVENT)
				;
			MessageBrocker.interrupt = Interrupt.NOTIFY_AGENT;
			MessageBrocker.ms = ms;
			MessageBrocker.Brocker.interrupt();
		} else if (Short.parseShort(msg.getTag()) == MessageCommonData.Message.ASSIGNED_AREA) {
			try {
				int numberOfClusters =  MessageCommonData.agentList.size() - 1;
				JSONObject points = new JSONObject(MessageCommonData.map);
				JSONArray arr1 = //new JSONArray("[[10,10],[20,20],[40,40]]");
						 new JSONArray(points.getString("data2").toString());
				JSONArray arr2;

				// ********************** initiallizing K-Means centroids *******************************************************
				if (numberOfClusters > 1) {
					double[][] centroid = new double[numberOfClusters][2];
					for (int i = 0; i < numberOfClusters; i++) {
						arr2 = arr1.getJSONArray(i);
						centroid[i][0] = arr2.getInt(0);
						centroid[i][1] = arr2.getInt(1);

					}

					// ************************ Calculate distance ******************************************************************				

					while (true) {
						ArrayList<ArrayList<Float>> distanceMatrix = new ArrayList<>();
						ArrayList<ArrayList<Integer>> clusterMatrix = new ArrayList<>();

						for (int i = 0; i < centroid.length; i++) {
							ArrayList<Integer> temp = new ArrayList<>();

							for (int j = 0; j < arr1.length(); j++) {
								temp.add(0);
							}
							clusterMatrix.add(temp);
						}


						for (int i = 0; i < centroid.length; i++) {
							ArrayList<Float> temp = new ArrayList<>();
							for (int j = 0; j < arr1.length(); j++) {
								double distance = Math.sqrt(Math.pow(centroid[i][0] - arr1.getJSONArray(j).getInt(0), 2)
										+ Math.pow(centroid[i][1] - arr1.getJSONArray(j).getInt(1), 2));
								temp.add((float) distance);
							}
							distanceMatrix.add(temp);
						}


						for (int j = 0; j < distanceMatrix.get(0).size(); j++) {
							float minValue = distanceMatrix.get(0).get(j);
							int minIndex = 0;
							for (int i = 0; i < centroid.length; i++) {
								if (distanceMatrix.get(i).get(j) < minValue) {
									minValue = distanceMatrix.get(i).get(j);
									minIndex = i;
								}
							}
							clusterMatrix.get(minIndex).set(j, 1);
						}
					
						
						double[][] centroid2 = new double[numberOfClusters][2];

						// ************************ Re - Calculate centroids ******************************************************************
						for (int i = 0; i < centroid.length; i++) {

							double averageX = 0, averageY = 0;
							int count = 0;
							for (int j = 0; j < arr1.length(); j++) {
								if (clusterMatrix.get(i).get(j) != 0) {
									averageX += arr1.getJSONArray(j).getDouble(0);
									averageY += arr1.getJSONArray(j).getDouble(1);
									count++;
								}
							}
							
							if(count != 0){
								centroid2[i][0] = averageX / count;
								centroid2[i][1] = averageY / count;
							}else{
								centroid2[i][0] = 0;
								centroid2[i][1] = 0;
							}
							

						}
						
						boolean equals = true;
						
						
						for (int i = 0; i < centroid.length; i++) {
							boolean check=false;
							for (int j = 0; j < centroid2.length; j++) {
								if(centroid[i][0] == centroid2[j][0] && centroid[i][1] == centroid2[j][1]){
									check = true;									
									break;
								}
							}
							if(check != true){
								equals = false;
								break;
							}
						}
						    
					  if(equals){
						  System.out.println(clusterMatrix);
						  System.out.println( centroid2[0][0]+" "+centroid2[0][1]+","+centroid2[1][0]+" "+centroid2[1][1]);
						  break;
						  
					  }else{
						  centroid = centroid2;
					  }

					}
					///////////////////////////////////////////////
					Message ms = new Message(MessageCommonData.agentList.keySet().iterator().next(),
							this.msg.getSender(), String.valueOf(MessageCommonData.Message.ASSIGNED_AREA),
							points.getString("data2").toString(), new Date().toString(),
							String.valueOf(MessageCommonData.commGroup),
							String.valueOf(MessageCommonData.Priority.NORMAL), msg.getSession());
					sendBroadcast(ms);
					
				} else {

					Message ms = new Message(MessageCommonData.agentList.keySet().iterator().next(),
							this.msg.getSender(), String.valueOf(MessageCommonData.Message.ASSIGNED_AREA),
							points.getString("data2").toString(), new Date().toString(),
							String.valueOf(MessageCommonData.commGroup),
							String.valueOf(MessageCommonData.Priority.NORMAL), msg.getSession());
					while (MessageBrocker.interrupt != MessageBrocker.Interrupt.NO_EVENT)
						;
					MessageBrocker.interrupt = Interrupt.NOTIFY_AGENT;
					MessageBrocker.ms = ms;
					MessageBrocker.Brocker.interrupt();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}else if(Short.parseShort(msg.getTag()) == MessageCommonData.Message.SUSPICIOUS_PERSON){
						
			JSONObject clientMsg = new JSONObject();
			try {
				System.out.println(msg.getMessage());
				clientMsg.append("Message",MessageCommonData.Message.SUSPICIOUS_PERSON);
				clientMsg.append("AgentId", msg.getSender());
				clientMsg.append("Person", msg.getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}	
			
			WebSocketServer.sendToAll(clientMsg.toString());
			
//			msg.setMessage("Found the person");
//			msg.setTag(Short.toString(MessageCommonData.Message.PERSON_DETECTED));
//			sendBroadcast(msg);
			//CoordinatorBackend.processPersonImage();
		}else if(Short.parseShort(msg.getTag()) == MessageCommonData.Message.PERSONS_DETAILS){
			if(MessageCommonData.agentList.isEmpty()){

			}else{
				sendBroadcast(msg);				
			}
		}else if(Short.parseShort(msg.getTag()) == MessageCommonData.Message.BATTERY_STATUS){

			JSONObject clientMsg = new JSONObject();
			try {
				clientMsg.append("Message",MessageCommonData.Message.BATTERY_STATUS);
				clientMsg.append("AgentId", msg.getSender());
				clientMsg.append("Battery", msg.getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			}			
			Test.WebSocketServer.sendToAll(clientMsg.toString());
		}

	}
	
	public void sendBroadcast(Message ms){	
		ms.setSender(MessageCommonData.agentList.keySet().iterator().next());
		
		System.out.println(MessageCommonData.agentList.size());
		
		if(ms.getMessage().equals("{}")){
			ms.setTag(Short.toString(MessageCommonData.Message.PERSON_DETECTED));
		}
		
		if(MessageCommonData.agentList.size() <= 2){
			for(int i = 0; i < MessageCommonData.agentList.size() ; i++){
				if(MessageCommonData.agentList.get(MessageCommonData.agentList.keySet().toArray()[i]) != null){
					ms.setReciever(MessageCommonData.agentList.keySet().toArray()[i].toString());
					ms.setSession(MessageCommonData.agentList.get(MessageCommonData.agentList.keySet().toArray()[i]));
					while (MessageBrocker.interrupt != MessageBrocker.Interrupt.NO_EVENT)
						;
					MessageBrocker.interrupt = Interrupt.NOTIFY_AGENT;
					MessageBrocker.ms = ms;
					MessageBrocker.Brocker.interrupt();					
				}
			}			
		}else{
			for(int i = 0; i < MessageCommonData.broadcasters.length ; i++){				
				ms.setReciever(MessageCommonData.agentList.keySet().toArray()[MessageCommonData.broadcasters[i]].toString());
				ms.setSession(MessageCommonData.agentList.get(MessageCommonData.agentList.keySet().toArray()[MessageCommonData.broadcasters[i]]));
				
				Message msg2 = new Message(ms.getSender(), ms.getReciever(), ms.getTag(), ms.getMessage(), ms.getTimeStamp(), ms.getCommunicationGroup(), ms.getPriority(), ms.getSession());
				MessageBrocker.ms = msg2;
				System.out.println(msg2);
				while (MessageBrocker.interrupt != MessageBrocker.Interrupt.NO_EVENT)
					;
				MessageBrocker.interrupt = Interrupt.NOTIFY_AGENT;
				MessageBrocker.Brocker.interrupt();
			}			
		}
	}

}
