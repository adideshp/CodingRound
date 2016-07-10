package main.java.com;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import main.java.com.clientManager.Client;
import main.java.com.clientManager.ConnectionManager;
import main.java.com.eventManager.EventManager;
import main.java.com.messageProcessor.MessageProcessor;

public class Server {

	private BlockingQueue<String> messageQueue;
	private Map<Long, Client> clientIdToClentObjMap;
	private int clientPort; 
	private int eventPort; 
	
	public Server(int clientPort, int eventPort) {
		this.clientPort = clientPort;
		this.eventPort = eventPort;
		this.messageQueue = new ArrayBlockingQueue<String>(1000);
		this.clientIdToClentObjMap = new ConcurrentHashMap<Long, Client>(1000);
	}
	
	public void start() {
		try {	
			EventManager eventReader = new EventManager(this.eventPort);
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
