package main.java.com.messageProcessor;

public class Message {

	private String stringMsg;
	private long sequenceNum;
	private String msgType;
	private long source;
	private long destination;
	
	public Message(String message) {
		this.stringMsg = message;
		
		String[] msgParts;
		msgParts = message.split("|");
		this.sequenceNum = -1;
		this.msgType = null;
		this.source = -1;
		this.destination = -1;
		try {
			this.sequenceNum = Long.parseLong(msgParts[0]);
			this.msgType = msgParts[1];
			this.source = Long.parseLong(msgParts[2]);
			this.destination = Long.parseLong(msgParts[3]);
		} catch (NumberFormatException e) {
			return;
		}		
	}
	
	public String getStringMsg() {
		return this.stringMsg;
	}
	
	public long getSequenceNum() {
		return this.sequenceNum;
	}

	public String getMsgType() {
		return this.msgType;
	}

	public long getSource() {
		return this.source;
	}


	public long getDestination() {
		return this.destination;
	}


}
