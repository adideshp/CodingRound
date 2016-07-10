package main.java.com.messageProcessor;

import java.util.LinkedList;

/*@description : Responsible for storing message in sorted order based on the 
 * sequence number of the messages. This acts as a buffer for messages whose predecessor 
 * message(s) have not yet arrived
 * */
public class MessageBuffer {
	
	private LinkedList<Message> earlyMsgBuffer;
	// Sequence number of message that has lowest sequence number in the buffer
	private long floorSeqNum;
	// Sequence number of message that has highest sequence number in the buffer
	private long ceilSeqNum;
	
	public MessageBuffer() {
		this.floorSeqNum = 999999999;
		this.ceilSeqNum = 0;
		this.earlyMsgBuffer = new LinkedList<Message>();
	}
	
	
	// Add message in Buffer in sorted order
	public void addMsgInBuffer(Message message) {
		long seqNum = message.getSequenceNum();
		//Message having smallest sequence number
		if (seqNum < this.floorSeqNum) {
			this.floorSeqNum = seqNum;
			//First message
			if (seqNum > this.ceilSeqNum) {
				// Case for first message
				this.ceilSeqNum = seqNum;
			}
			this.earlyMsgBuffer.addFirst(message);
		}
		//Message having largest sequence number
		else if (seqNum > this.ceilSeqNum) {
			this.ceilSeqNum = seqNum;
			this.earlyMsgBuffer.addLast(message);
		}
		// Message having sequence number in between smallest and largest sequence numbers
		else {
			int index=0;
			for(Message msg: this.earlyMsgBuffer) {
				long tempSeqNum = msg.getSequenceNum();
				if((seqNum-tempSeqNum) < 0) break;
				index++;
			}
			if (index !=0) {
				this.earlyMsgBuffer.add(index, message);
			}
		}
	}
	
	// Checks if the message with passed sequence number is in the Buffer. If present returns the message
	public String getMsgFromBuffer(long seqNum) {
		
			if(!this.earlyMsgBuffer.isEmpty()) {
				if (seqNum < this.floorSeqNum || seqNum > this.ceilSeqNum) {
					return null;
				} else if(seqNum == this.floorSeqNum) {
					String msg = this.earlyMsgBuffer.removeFirst().getStringMsg();
					try {
					this.floorSeqNum = this.earlyMsgBuffer.peekFirst().getSequenceNum();
					} catch(NullPointerException e) {
						this.floorSeqNum = 999999999;
					}
					return msg;
				} 
			}
		
		return null;
	}
	
	
}
