package Test;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocketserver")
public class WebSocketServer {
	
	
	static String msg = "";

	@OnOpen
	public void onOpen(){
		System.out.println("Open Connection ...");
	}
	
	@OnClose
	public void onClose(){
		System.out.println("Close Connection ...");
	}
	
	@OnMessage
	public String onMessage(String message){
		System.out.println("Message from the client: " + message);
		String echoMsg = "Echo from the server : " + message;
		this.msg = message;
		return echoMsg;
	}
	
	@OnError
	public void onError(Throwable e){
		e.printStackTrace();
	}
	
	
}
