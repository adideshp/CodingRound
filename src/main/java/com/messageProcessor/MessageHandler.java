package main.java.com.messageProcessor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import main.java.com.clientManager.Client;

public class MessageHandler implements Runnable{

	private ConcurrentLinkedQueue<String> orderedMsgQueue;
	private Map<Long, Client> clientIdToClentObjMap;
	
	
	public MessageHandler(ConcurrentLinkedQueue<String> orderedMsgQueue, Map<Long, Client> clientIdToClentObjMap) {
		this.orderedMsgQueue = orderedMsgQueue;
		this.clientIdToClentObjMap = clientIdToClentObjMap;
	}
	
	public boolean handleFollow(Message message) throws IOException {
		System.out.println("Message type Follow detected" + message.getStringMsg() );
		Client source = this.clientIdToClentObjMap.get(message.getSource());
		Client destination = this.clientIdToClentObjMap.get(message.getDestination());
		source.subscribe(destination.getId());
		SelectionKey key = destination.getSelectionKey();
		SocketChannel socketChannel = (SocketChannel) key.channel();
		while(true) {
			if (key.isWritable()) {
				destination.writeToChannel(message.getStringMsg(), socketChannel);
				return true;
			}
		}
	}
	
	public boolean handleUnfollow(Message message) {
		System.out.println("Message type Unfollow detected" + message.getStringMsg() );
		Client source = this.clientIdToClentObjMap.get(message.getSource());
		Client destination = this.clientIdToClentObjMap.get(message.getDestination());
		source.unsubscribe(destination.getId());
		return true;
	}
	
	public boolean handleBroadcast(Message message) throws IOException {
		System.out.println("Message type Broadcast detected" + message.getStringMsg() );
		Client client;
		for (Map.Entry<Long, Client> entry : this.clientIdToClentObjMap.entrySet())
		{	
			client = entry.getValue();
			SelectionKey key = client.getSelectionKey();
			SocketChannel socketChannel = (SocketChannel) key.channel();
			while(true) {
				if (key.isWritable()) {
					client.writeToChannel(message.getStringMsg(), socketChannel);
					break;
				}
			}
		}
		return true;
	}
	
	
	public boolean handlePrivateMsg(Message message) throws IOException {
		System.out.println("Message type PrivateMsg detected" + message.getStringMsg() );
		Client destination = this.clientIdToClentObjMap.get(message.getDestination());
		SelectionKey key = destination.getSelectionKey();
		SocketChannel socketChannel = (SocketChannel) key.channel();
		while(true) {
			if (key.isWritable()) {
				destination.writeToChannel(message.getStringMsg(), socketChannel);
				return true;
			}
		}
	}
	
	public boolean handleStatusUpdate(Message message) throws IOException {
		System.out.println("Message type Status Update detected" + message.getStringMsg() );
		Client client;
		Client source = this.clientIdToClentObjMap.get(message.getSource());
		LinkedList<Long> subscribers = source.getSubscribers();
		for (Long subscriber : subscribers)
		{	
			client = this.clientIdToClentObjMap.get(subscriber);
			SelectionKey key = client.getSelectionKey();
			SocketChannel socketChannel = (SocketChannel) key.channel();
			while(true) {
				if (key.isWritable()) {
					client.writeToChannel(message.getStringMsg(), socketChannel);
					break;
				}
			}
		}
		return true;
	}

	
	public void processMessage(Message message) throws IOException {
		System.out.println("Processing Message:"+ message.getStringMsg());
		switch(message.getMsgType()) {
		case "F":
			handleFollow(message);
			break;
		case "U":
			handleUnfollow(message);
			break;
		case "B":
			handleBroadcast(message);
			break;
		case "P":
			handlePrivateMsg(message);
			break;
		case "S":
			handleStatusUpdate(message);
			break;
		default :
			break;
		}
	}

	@Override
	public void run() {
		System.out.println("Starting the MessageHandler Thread...");
		String message;
		while(true) {
			while ((message = this.orderedMsgQueue.poll()) != null) {
				System.out.println("Fetch from queue by MessageSequencer");
				Message msg = new Message(message);
				try {
					this.processMessage(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		}
	}

}
