package main.java.com.messageProcessor;

import java.util.concurrent.ConcurrentLinkedQueue;

/*@description: Responsible for reading out of order messages from the EventManager and 
 * converting them to sequential message queue for the MessageHandler thread.
 * */
public class MessageSequencer implements Runnable{

	private ConcurrentLinkedQueue<String> messageQueue;
	private ConcurrentLinkedQueue<String> orderedMsgQueue;
	
	// Data structure for storing out of order messages till their need arises
	private MessageBuffer messageBuffer; 
	// Last message pushed in the OrderedMsgQueue which was in order. This helps to keep track of the next needed msg 
	private long lastValidMsgPushed; 
	
	
	public MessageSequencer(ConcurrentLinkedQueue<String> messageQueue, ConcurrentLinkedQueue<String> orderedMsgQueue) {
		this.messageQueue = messageQueue;
		this.orderedMsgQueue = orderedMsgQueue;
		this.messageBuffer = new MessageBuffer();
		this.lastValidMsgPushed = 0;
	}

	// Verifies if the sequence number (Message having that seq.no) is valid to go in the OrderedMsgQueue
	public boolean isDefyingSequence(long seqNum, long lastValidMsgPushed) {
		if (seqNum == lastValidMsgPushed + 1) {return false;}
		return true;
	}
	
	public String getMsgFromQueue() {
		String message;
		// Fetch message from queue shared with EventManager 
		while ((message = this.messageQueue.poll()) != null) {
			return message;
		}
		// No message in the queue
		return null;
	}
	
	@Override
	public void run() {
		System.out.println("[MessageSequencer-Thread][run]Starting the MessageSequencer Thread...");
		String message;
		Message msgObj;
		while(true) {
			message = this.getMsgFromQueue();
			//valid Message fetched from the queue 
			if (message != null) {
				msgObj = new Message(message);
				//If it defies sequence then add the message to the MessageBuffer
				if (isDefyingSequence(msgObj.getSequenceNum(), this.lastValidMsgPushed)) {
					System.out.println("[MessageSequencer-Thread][run] Message: "+ message + " not in order. Adding in buffer");
					this.messageBuffer.addMsgInBuffer(msgObj);
				} else {
					//Add message directly to orderedMsgQueue
					this.orderedMsgQueue.add(message);
					this.lastValidMsgPushed = msgObj.getSequenceNum();
					System.out.println("[MessageSequencer-Thread][run] Message: "+ message + " added to orderedMsgQueue");
				}
			}
			
			// Check if the messageBuffer has any messages that can go in orderedMsgQueue
			message = this.messageBuffer.getMsgFromBuffer(this.lastValidMsgPushed + 1);
			if (message != null) {
				//There is a valid message
				msgObj = new Message(message);
				this.orderedMsgQueue.add(message);
				this.lastValidMsgPushed = msgObj.getSequenceNum();
				System.out.println("[MessageSequencer-Thread][run] Message: "+ message + "from messageBuffer added to orderedMsgQueue");
			}
		}
	}

}
