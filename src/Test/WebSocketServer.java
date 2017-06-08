package Test;
import java.util.ArrayList;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocketserver")
public class WebSocketServer {
	
	public static ArrayList<Session> clients = new ArrayList<>();

	@OnOpen
	public void onOpen(Session session){
		clients.add(session);
	}
	
	@OnClose
	public void onClose(Session session){
		clients.remove(session);
	}
	
	@OnMessage
	public void onMessage(String message){
		
	}
	
	@OnError
	public void onError(Throwable e){
		e.printStackTrace();
	}
	
	
	public static void sendToAll(String msg){
		for(int i = 0; i < clients.size() ;i++){
			try {
				clients.get(i).getBasicRemote().sendText(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	
}
