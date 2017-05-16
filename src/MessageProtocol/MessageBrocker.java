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
	
	public static boolean startMessageBrockerService(){
		
		executor = new MessageExecutor();
		
		t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					if(!MessageCommonData.msgQueue.isEmpty()){
						
					}else{
						try {
							System.out.println("Brocker Service start sleeping");
							Thread.sleep(Long.MAX_VALUE);
						} catch (InterruptedException e) {
							System.out.println("Brocker Service waked up from sleep");
						}
					}
				}
				
				
			}
			
			
		});
		t.start();
		return true;		
		
	}
	
	private boolean processMessageQueue(){
		
		return true;
	}
	
	
}
