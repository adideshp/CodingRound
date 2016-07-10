package main.java.com.messageProcessor;

import java.util.LinkedList;


public class MessageBuffer {
	
	private LinkedList<Message> earlyMsgBuffer;
	private long floorSeqNum;
	private long ceilSeqNum;
	
	public MessageBuffer() {
		this.floorSeqNum = 999999999;
		this.ceilSeqNum = 0;
		this.earlyMsgBuffer = new LinkedList<Message>();
	}
	

	public boolean updateFloorCeilSeqNum(long seqNum) {
		if (seqNum < this.floorSeqNum) {this.floorSeqNum = seqNum;}
		if (seqNum > this.ceilSeqNum) {this.ceilSeqNum = seqNum;}
		return true;
	}
	
	public void addMsgInBuffer(Message message) {
		long seqNum = message.getSequenceNum();
		if (seqNum < this.floorSeqNum) {
			this.floorSeqNum = seqNum;
			this.earlyMsgBuffer.addFirst(message);
		}
		else if (seqNum > this.ceilSeqNum) {
			this.ceilSeqNum = seqNum;
			this.earlyMsgBuffer.addLast(message);
		}
		else {
			int index=0;
			for(Message msg: this.earlyMsgBuffer) {
				long tempSeqNum = msg.getSequenceNum();
				if((seqNum-tempSeqNum) < 0) {this.earlyMsgBuffer.add(index, message);}
				index++;
			}
		}
	}
	
	public String getMsgFromBuffer(long seqNum) {
		if(this.earlyMsgBuffer.isEmpty()) {
			if (seqNum < this.floorSeqNum || seqNum > this.ceilSeqNum) {
				return null;
			} else if(seqNum == this.floorSeqNum) {
				String msg = this.earlyMsgBuffer.removeFirst().getStringMsg();
				this.floorSeqNum = this.earlyMsgBuffer.peekFirst().getSequenceNum();
				return msg;
			} 
		} 
		return null;
	}
	
	
}
