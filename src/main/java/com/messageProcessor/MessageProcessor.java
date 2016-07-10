package main.java.com.messageProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import main.java.com.clientManager.Client;

public class MessageProcessor {

	private ConcurrentLinkedQueue<String> messageQueue;
	private ConcurrentLinkedQueue<String> orderedMsgQueue;
	private Map<Long, Client> clientIdToClentObjMap;
	
	public MessageProcessor(ConcurrentLinkedQueue<String> messageQueue, Map<Long, Client> clientIdToClentObjMap) {
		this.messageQueue = messageQueue;
		this.orderedMsgQueue =  new ConcurrentLinkedQueue<String>();
		this.clientIdToClentObjMap = clientIdToClentObjMap;
	}
	
	public void start() {
		System.out.println("Starting the MessageProcessor ...");
		MessageSequencer messageSequencer = new MessageSequencer(this.messageQueue, this.orderedMsgQueue);
		Thread messageSequencerThread = new Thread(messageSequencer); 
		
		MessageHandler messageHandler = new MessageHandler(this.orderedMsgQueue, this.clientIdToClentObjMap);
		Thread messageHandlerThread = new Thread(messageHandler);
		
		messageSequencerThread.start();
		messageHandlerThread.start();
	}

}
