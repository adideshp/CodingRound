package main.java.com.eventManager;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class EventManager {

	private EventReader eventWorker = null;
	private BlockingQueue<String> messageQueue; 
	
	
	public EventManager(int port) throws IOException {
		this.messageQueue = new ArrayBlockingQueue<String>(100);
		this.eventWorker = new EventReader(port, this.messageQueue);
	}
		
	public void start() {
		System.out.println("Starting the Events reader...");
		Thread eventWorkerThread  = new Thread(this.eventWorker);
		eventWorkerThread.start();
	}
}
