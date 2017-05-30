package MessageProtocol;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.websocket.Session;

public class MessageCommonData {
	
	private MessageCommonData(){
		
	}
	
	static public volatile LinkedHashMap<String,Session> agentList = new LinkedHashMap();
	
	public static short commGroup = 1;
	
	static public volatile HashMap<Integer, LinkedList<MessageProtocol.Message>> msgQueue = new HashMap<>();   
	
	public enum TagList{
		INFORM_COORDINATOR,
		INFORM_AGENT
	}
	
	public class Priority{	    
		public static final int LOW                             = 0;
		public static final int NORMAL                          = 1;
		public static final int HIGH                            = 2;

	}
	
	public class Message{
		public static final short	READY_TO_WORK                   = 0;
		public static final short	ASK_FOR_OTHER_AGENTS            = 1;    
		public static final short	LIST_OF_OTHER_AGENTS            = 2;
		public static final short	LOCATION_MAP                    = 3;
		public static final short	ASSIGNED_AREA                   = 4;
		public static final short	PERSONS_DETAILS                 = 5;
		public static final short   SUSPICIOUS_PERSON               = 6;
		public static final short	CURRENT_BATTERY_VOLTAGE         = 7;
		public static final short	CRITICAL_BATTERY_LEVEL          = 8;
		public static final short	PERSON_DETECTED                 = 9;
		public static final short   CURRENT_LOCATION                = 10;
	}
	 

}

