package main.java.com.eventManager;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventManager {

	private EventReader eventWorker = null;
	private ConcurrentLinkedQueue<String> messageQueue; 
	
	
	public EventManager(int port, ConcurrentLinkedQueue<String> messageQueue) throws IOException {
		this.messageQueue = messageQueue;
		this.eventWorker = new EventReader(port, this.messageQueue);
	}
		
	public void start() {
		System.out.println("Starting the EventManager ...");
		Thread eventWorkerThread  = new Thread(this.eventWorker);
		eventWorkerThread.start();
	}
}
