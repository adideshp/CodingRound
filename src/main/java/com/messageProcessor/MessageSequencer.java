package main.java.com.messageProcessor;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageSequencer implements Runnable{

	private ConcurrentLinkedQueue<String> messageQueue;
	private ConcurrentLinkedQueue<String> orderedMsgQueue;
	
	private MessageBuffer messageBuffer; 
	private long lastValidMsgPushed; 
	
	public MessageSequencer(ConcurrentLinkedQueue<String> messageQueue, ConcurrentLinkedQueue<String> orderedMsgQueue) {
		this.messageQueue = messageQueue;
		this.orderedMsgQueue = orderedMsgQueue;
		this.messageBuffer = new MessageBuffer();
		this.lastValidMsgPushed = 0;
	}

	
	public boolean isDefyingSequence(long seqNum, long lastValidMsgPushed) {
		if (seqNum == lastValidMsgPushed + 1) {return false;}
		return true;
	}
	
	public String getMsgFromQueue() {
		String message;
		while ((message = this.messageQueue.poll()) != null) {
			System.out.println("Fetch from queue by MessageSequencer");
			return message;
		}
		return null;
	}
	
	@Override
	public void run() {
		System.out.println("Starting the MessageSequencer Thread...");
		String message;
		Message msgObj;
		while(true) {
			message = this.getMsgFromQueue();
			if (message != null) {
				msgObj = new Message(message);
				
				if (isDefyingSequence(msgObj.getSequenceNum(), this.lastValidMsgPushed)) {
					System.out.println("Message:"+ message + " not in order. Adding in buffer");
					this.messageBuffer.addMsgInBuffer(msgObj);
				} else {
					this.orderedMsgQueue.add(message);
					this.lastValidMsgPushed = msgObj.getSequenceNum();
					System.out.println("Message:"+ message + " added to orderedQueue");
				}
			}
			
			// Check if the messageBuffer has any messages that can go in orderedMsgQueue
			
			message = this.messageBuffer.getMsgFromBuffer(this.lastValidMsgPushed + 1);
			if (message != null) {
				//There is a valid message
				msgObj = new Message(message);
				this.orderedMsgQueue.add(message);
				this.lastValidMsgPushed = msgObj.getSequenceNum();
				System.out.println("Message:"+ message + " added to orderedQueue");
			}
		}
	}

}
