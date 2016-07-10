package main.java.com.messageProcessor;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import main.java.com.clientManager.Client;

public class MessageProcessor {

	private BlockingQueue<String> messageQueue;
	private BlockingQueue<String> orderedMsgQueue;
	private Map<Long, Client> clientIdToClentObjMap;
	
	public MessageProcessor(BlockingQueue<String> messageQueue, Map<Long, Client> clientIdToClentObjMap) {
		this.messageQueue = messageQueue;
		this.orderedMsgQueue = new ArrayBlockingQueue<String>(500);
		this.clientIdToClentObjMap = clientIdToClentObjMap;
	}
	
	public void start() {
		MessageSequencer messageSequencer = new MessageSequencer(this.messageQueue, this.orderedMsgQueue);
		Thread messageSequencerThread = new Thread(messageSequencer); 
		
		MessageHandler messageHandler = new MessageHandler(this.orderedMsgQueue, this.clientIdToClentObjMap);
		Thread messageHandlerThread = new Thread(messageHandler);
		
		messageSequencerThread.start();
		messageHandlerThread.start();
	}

}
