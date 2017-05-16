package ClientHandler;
import Coordinator.Coordinator;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyAppServletContextListener
               implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

        //Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("Coordinator started");
		
		if(MessageProtocol.MessageBrocker.startMessageBrockerService() && Coordinator.startCoordinatorBroadcastService()){
			Coordinator.startCoordniatorMainProcess();
		};
	}
}