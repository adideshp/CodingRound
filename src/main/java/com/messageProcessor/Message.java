package main.java.com.messageProcessor;


/*@description : Utility class for accessing the values in the message
 * */
public class Message {

	private String stringMsg;
	private long sequenceNum;
	private String msgType;
	private long source;
	private long destination;
	
	public Message(String message) {
		this.stringMsg = message;
		
		String[] validStr, msgParts;
		validStr = message.split("\\\n");
		msgParts = validStr[0].split("\\|");
		this.sequenceNum = -1;
		this.msgType = null;
		this.source = -1;
		this.destination = -1;
		try {
			if (msgParts.length >=2) {
				this.sequenceNum = Long.parseLong(msgParts[0]);
				this.msgType = msgParts[1];
				if (msgParts.length >=3) {
					this.source = Long.parseLong(msgParts[2]);
					if (msgParts.length ==4) {
						this.destination = Long.parseLong(msgParts[3]);
					}
				}
			}
		} catch (NumberFormatException e) {
			return;
		}		
	}
	
	public String getStringMsg() {
		return this.stringMsg + "\r\n";
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
