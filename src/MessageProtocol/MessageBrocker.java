package MessageProtocol;
import java.util.concurrent.Executor;

import org.json.JSONObject;

public class MessageBrocker {
	
	static Thread t;
	private static MessageExecutor executor;
	
	private MessageBrocker(){
		
	}
	
	public static boolean queueMessage(JSONObject msg){		
		return false;
	}
	
	public static void startMessageBrockerService(){
		
		executor = new MessageExecutor();
		
		t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					if(!MessageCommonData.msgQueue.isEmpty()){
						
					}else{
						try {
							System.out.println("Brocker Service start sleeping");
							Thread.sleep(60000);
						} catch (InterruptedException e) {
							System.out.println("Brocker Service waked up from sleep");
						}
					}
				}
				
				
			}
			
			
		});
		t.start();		
		
	}
	
	private boolean processMessageQueue(){
		
		return true;
	}
	
	
}
