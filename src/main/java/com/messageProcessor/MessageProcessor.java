package main.java.com.messageProcessor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import main.java.com.clientManager.Client;

/*@description: Responsible for fetching messages from EventManager, Ordering them on the basis of the
 * sequence number and handling the messages.
 * This Module is midway between the EventManager and ClientManager
 * */
public class MessageProcessor {
	//Shared Data structure with EventManager
	private ConcurrentLinkedQueue<String> messageQueue;
	// Shared Data structure with MessageHandler(Thread) and MessageSequencer(Thread)
	private ConcurrentLinkedQueue<String> orderedMsgQueue;
	//Shared Data structure with ClientManager
	private ConcurrentHashMap<Long, Client> clientIdToClentObjMap;
	
	public MessageProcessor(ConcurrentLinkedQueue<String> messageQueue, ConcurrentHashMap<Long, Client> clientIdToClentObjMap) {
		this.messageQueue = messageQueue;
		this.orderedMsgQueue =  new ConcurrentLinkedQueue<String>();
		this.clientIdToClentObjMap = clientIdToClentObjMap;
	}
	
	//Start messageSequencer Thread and messageHandler Thread
	public void start() {
		System.out.println("[MessageProcessor][start]Starting the MessageProcessor ...");
		MessageSequencer messageSequencer = new MessageSequencer(this.messageQueue, this.orderedMsgQueue);
		Thread messageSequencerThread = new Thread(messageSequencer); 
		
		MessageHandler messageHandler = new MessageHandler(this.orderedMsgQueue, this.clientIdToClentObjMap);
		Thread messageHandlerThread = new Thread(messageHandler);
		
		messageSequencerThread.start();
		messageHandlerThread.start();
	}

}
