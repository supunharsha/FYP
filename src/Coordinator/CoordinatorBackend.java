package Coordinator;
import com.sun.jna.Library;
import com.sun.jna.Native;
 

public class CoordinatorBackend {
	
	public interface Backend extends Library{
		public int verifyPerson();
	
	}
	
	public static boolean processPersonImage(){
		
		Backend backend = (Backend) Native.loadLibrary("kernel32", Backend.class);
		
		if(backend.verifyPerson() == 1){
			return true;
		}else{
			return false;
		}		
	}

}
