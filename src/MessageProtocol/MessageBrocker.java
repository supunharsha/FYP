package MessageProtocol;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import Coordinator.Coordinator;
import Coordinator.Coordinator.CoordinatorInterrupts;

public class MessageBrocker {

	public static Thread Brocker;
	public static Interrupt interrupt;
	private static ExecutorService executor;
	public static Message ms;

	private static final short THREAD_POOL_SIZE = 10;

	private static final short HIGH_PRIORITY_MSG_SERVE_LENGTH = 3;
	private static final short NORMAL_PRIORITY_MSG_SERVE_LENGTH = 2;
	private static final short LOW_PRIORITY_MSG_SERVE_LENGTH = 1;

	public enum Interrupt {
		NO_EVENT, MESSAGE_RECIEVED_FROM_AGENT, REQUEST_RECIEVED_FROM_CLIENT, NOTIFY_AGENT
	}

	private MessageBrocker() {

	}

	public static boolean queueMessage(JSONObject msg) {
		return false;
	}
	
	private static void serveMsg(LinkedList<Message> msgList){
		while (Coordinator.interrupt != CoordinatorInterrupts.NO_EVENT);
		try {
			
			Message ms = msgList.getFirst();
			Coordinator.interrupt = CoordinatorInterrupts.BROCKER_SEND_A_MESSAGE;
			Coordinator.msg = ms;
			Coordinator.Coordinator.interrupt();
			msgList.removeFirst();
		} catch (Exception e) {										
			e.printStackTrace();
		}		
	}

	public static boolean startMessageBrockerService() {

		Brocker = new Thread(new Runnable() {

			@Override
			public void run() {
				interrupt = Interrupt.NO_EVENT;
				executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
				while (true) {
					try {
						if (interrupt == Interrupt.NO_EVENT) {
							System.out.println("Brocker Service start sleeping");
							Thread.sleep(Long.MAX_VALUE);
						} else if (interrupt == Interrupt.MESSAGE_RECIEVED_FROM_AGENT) {
							
							LinkedList<Message> msgList = MessageCommonData.msgQueue.get(MessageCommonData.Priority.HIGH);

							for (short i = 0; msgList != null  && i < msgList.size() && i < MessageBrocker.HIGH_PRIORITY_MSG_SERVE_LENGTH; i++) {
								serveMsg(msgList);
							}
							
							msgList = MessageCommonData.msgQueue.get(MessageCommonData.Priority.NORMAL);
							
							for (short i = 0; msgList!= null && i < msgList.size() && i < MessageBrocker.NORMAL_PRIORITY_MSG_SERVE_LENGTH; i++) {
								serveMsg(msgList);	
							}
							
							msgList = MessageCommonData.msgQueue.get(MessageCommonData.Priority.LOW);
							
							for (short i = 0; msgList != null && i < msgList.size() && i < MessageBrocker.LOW_PRIORITY_MSG_SERVE_LENGTH; i++) {
								serveMsg(msgList);	
							}
							
							interrupt = Interrupt.NO_EVENT;
						}else if(interrupt == Interrupt.NOTIFY_AGENT){
							executor.execute(new Runnable() {
								
								@Override
								public void run() {
									MessageAcceptor acceptor = new MessageAcceptor();
									acceptor.sendMessage(ms);
									
								}
							});
							interrupt = Interrupt.NO_EVENT;
							
						}

					} catch (InterruptedException e) {
						System.out.println("Brocker Service waked up from sleep");
					}

				}

			}

		});
		Brocker.start();
		return true;

	}

}
