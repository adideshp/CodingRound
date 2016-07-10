package main.java.com;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import main.java.com.clientManager.Client;
import main.java.com.clientManager.ConnectionManager;
import main.java.com.eventManager.EventManager;
import main.java.com.messageProcessor.MessageProcessor;

/*@description : Acts as a parent to call all the Managers in the application
*/
public class Server {

	//Shared Data structure between the EventManager and MessageProcessor
	private ConcurrentLinkedQueue<String> messageQueue;
	//Shared Data structure between the ConnectionManager and MessageProcessor
	private ConcurrentHashMap<Long, Client> clientIdToClentObjMap;
	private int clientPort; 
	private int eventPort; 
	
	public Server(int clientPort, int eventPort) {
		this.clientPort = clientPort;
		this.eventPort = eventPort;
		this.messageQueue = new ConcurrentLinkedQueue<String>();
		this.clientIdToClentObjMap = new ConcurrentHashMap<Long, Client>(1000);
	}
	
	/*Used to initiate all the Managers*/
	public void start() {
		try {	
			System.out.println("[Server][start] Server starting ...");
			//Initializing the managers
			EventManager eventManager = new EventManager(this.eventPort, this.messageQueue);
			ConnectionManager connManager = new ConnectionManager(this.clientPort, this.clientIdToClentObjMap);
			MessageProcessor messageProcessor = new MessageProcessor(this.messageQueue, this.clientIdToClentObjMap);
			//Starting the Managers
			eventManager.start();
			connManager.start();
			messageProcessor.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
