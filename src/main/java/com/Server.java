package main.java.com;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import main.java.com.clientManager.Client;
import main.java.com.clientManager.ConnectionManager;
import main.java.com.eventManager.EventManager;
import main.java.com.messageProcessor.MessageProcessor;

public class Server {

	private ConcurrentLinkedQueue<String> messageQueue;
	private Map<Long, Client> clientIdToClentObjMap;
	private int clientPort; 
	private int eventPort; 
	
	public Server(int clientPort, int eventPort) {
		this.clientPort = clientPort;
		this.eventPort = eventPort;
		this.messageQueue = new ConcurrentLinkedQueue<String>();
		this.clientIdToClentObjMap = new ConcurrentHashMap<Long, Client>(1000);
	}
	
	public void start() {
		try {	
			System.out.println("Server starting ...");
			EventManager eventReader = new EventManager(this.eventPort, this.messageQueue);
			ConnectionManager connManager = new ConnectionManager(this.clientPort);
			MessageProcessor messageProcessor = new MessageProcessor(this.messageQueue, this.clientIdToClentObjMap);
			eventReader.start();
			connManager.start();
			messageProcessor.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
