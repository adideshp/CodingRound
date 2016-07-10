package main.java.com.clientManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class Client {

	private long id;
	private LinkedList<Long> subscribers;
	private SelectionKey selectionKey;
	
	public Client(long id, SelectionKey selectionKey) {
		this.id = id;
		this.selectionKey = selectionKey;
		this.subscribers = new LinkedList<Long>();
	}

	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id ;
	}
	
	public SelectionKey getSelectionKey() {
		return this.selectionKey;
	}
	
	public LinkedList<Long> getSubscribers() {
		return this.subscribers;
	}
	
	
	public boolean subscribe(long l) {
		this.subscribers.add(l);
		return true;
	}
	
	public boolean unsubscribe(long clientId) {
		if (this.subscribers.contains(clientId)) {
			this.subscribers.remove(clientId);
			return true;
		}
		return false;
	}
	
	public String readFromChannel(SocketChannel socketChannel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(100);
		char bufChar;
		String value = "";
		long readSize = socketChannel.read(buffer);
		if (readSize != -1) {
			buffer.flip();
			while (buffer.hasRemaining()) {
				bufChar = (char) buffer.get();
				if (bufChar == '\n') {continue;}
				value += bufChar;
			}
		}
		return value;
	}
	
	public int writeToChannel(String msg, SocketChannel socketChannel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(100);
		byte[] bytes = msg.getBytes();
		buffer.put(bytes);
		int bytesWritten = socketChannel.write(buffer);
		return bytesWritten;
	}
	
	
}
