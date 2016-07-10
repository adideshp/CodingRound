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
	
	@Override
	public void run() {
		System.out.println("Starting the MessageSequencer Thread...");
		String message;
		while(true) {
			while ((message = this.messageQueue.poll()) != null) {
				System.out.println("Fetch from queue by MessageSequencer");
				Message msg = new Message(message);
				if (isDefyingSequence(msg.getSequenceNum(), this.lastValidMsgPushed)) {
					System.out.println("Message:"+ message + " not in order. Adding in buffer");
					this.messageBuffer.addMsgInBuffer(msg);
				} else {
					this.orderedMsgQueue.add(message);
					this.lastValidMsgPushed = msg.getSequenceNum();
					System.out.println("Message:"+ message + " added to orderedQueue");
				}
				// Check if the messageBuffer has any messages that can go in orderedMsgQueue
				message = this.messageBuffer.getMsgFromBuffer(this.lastValidMsgPushed + 1);
				if (message != null) {
					//There is a valid message
					msg = new Message(message);
					this.orderedMsgQueue.add(message);
					this.lastValidMsgPushed = msg.getSequenceNum();
					System.out.println("Message:"+ message + " added to orderedQueue");
				}
			}
		}
	}

}
