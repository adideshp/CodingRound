package main.java.com.sequentializer;

import java.io.IOException;

public class EventReader {

	private EventReaderWorker eventWorker = null;
	
	public EventReader(int port) throws IOException {
		this.eventWorker = new EventReaderWorker(port);
	}
		
	public void start() {
		System.out.println("Starting the Events reader...");
		Thread eventWorkerThread  = new Thread(this.eventWorker);
		eventWorkerThread.start();
	}
}
