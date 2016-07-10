package main.java.com.messageProcessor;

import java.util.concurrent.BlockingQueue;

public class MessageSequencer implements Runnable{

	private BlockingQueue<String> messageQueue;
	private BlockingQueue<String> orderedMsgQueue;
	
	private MessageBuffer messageBuffer; 
	private long lastValidMsgPushed; 
	
	public MessageSequencer(BlockingQueue<String> messageQueue, BlockingQueue<String> orderedMsgQueue) {
		this.messageQueue = messageQueue;
		this.orderedMsgQueue = orderedMsgQueue;
		this.messageBuffer = new MessageBuffer();
		this.lastValidMsgPushed = 0;
	}

	public long getSeqNumFromMsg(String msg) {
		long seqNum = -1;
		String[] msgParts = msg.split("|");
		seqNum =  Long.parseLong(msgParts[0]);
		return seqNum;
	}
	
	public boolean isDefyingSequence(long seqNum, long lastValidMsgPushed) {
		if (seqNum == lastValidMsgPushed+1) {return false;}
		return true;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				String message = this.messageQueue.take();
				Message msg = new Message(message);
				if (isDefyingSequence(msg.getSequenceNum(), this.lastValidMsgPushed)) {
					this.messageBuffer.addMsgInBuffer(msg);
				} else {
					this.orderedMsgQueue.put(message);
					this.lastValidMsgPushed = msg.getSequenceNum();
				}
				
				// Check if the messageBuffer has any messages that can go in orderedMsgQueue
				message = this.messageBuffer.getMsgFromBuffer(this.lastValidMsgPushed + 1);
				if (message != null) {
					//There is a valid message
					msg = new Message(message);
					this.orderedMsgQueue.put(message);
					this.lastValidMsgPushed = msg.getSequenceNum();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
