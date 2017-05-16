package MessageProtocol;

import java.util.ArrayList;
import java.util.Hashtable;
import org.json.JSONArray;

public class MessageCommonData {
	
	private MessageCommonData(){
		
	}
	
	static volatile ArrayList<String> agentList = new ArrayList<>();
	
	static volatile Hashtable<Integer, JSONArray> msgQueue = new Hashtable<>();   
	
	public enum TagList{
		INFORM_COORDINATOR,
		INFORM_AGENT
	}
	
	 

}

