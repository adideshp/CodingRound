package main.java.com.messageProcessor;

public class Message {

	private String stringMsg;
	private long sequenceNum;
	private String msgType;
	private long source;
	private long destination;
	
	public Message(String message) {
		this.stringMsg = message;
		String[] msgParts = {null,null,null,null};
		msgParts = message.split("|");
		this.sequenceNum = Long.parseLong(msgParts[0]);
		this.msgType = msgParts[1];
		this.source = Long.parseLong(msgParts[2]);
		this.destination = Long.parseLong(msgParts[3]);
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
