package main.java.com.eventManager;

import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;


public class MessageBuffer extends PriorityQueue<Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BlockingQueue<String> priorityMsgBuffferQueue;
	private long smallestSeqNum;
	private long largestSeqNum;
	
	public MessageBuffer() {
		
	}

}
