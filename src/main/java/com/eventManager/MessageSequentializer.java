package main.java.com.eventManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class MessageSequentializer implements Runnable{

	private BlockingQueue<String> messageQueue;
	private BlockingQueue<String> priorityMsgBuffferQueue;
	private BlockingQueue<String> orderedMsgQueue;
	
	private long lastValidMsgPushed; 
	
	public MessageSequentializer(BlockingQueue<String> messageQueue, BlockingQueue<String> orderedMsgQueue) {
		this.messageQueue = messageQueue;
		this.orderedMsgQueue = orderedMsgQueue;
		this.priorityMsgBuffferQueue = new PriorityBlockingQueue<String>();
		this.lastValidMsgPushed =0;
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
		
	}

}
