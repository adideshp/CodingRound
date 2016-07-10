package main.java.com.messageProcessor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import main.java.com.clientManager.Client;

public class MessageHandler implements Runnable{

	private ConcurrentLinkedQueue<String> orderedMsgQueue;
	private ConcurrentHashMap<Long, Client> clientIdToClentObjMap;
	
	
	public MessageHandler(ConcurrentLinkedQueue<String> orderedMsgQueue, ConcurrentHashMap<Long, Client> clientIdToClentObjMap) {
		this.orderedMsgQueue = orderedMsgQueue;
		this.clientIdToClentObjMap = clientIdToClentObjMap;
	}
	
	public boolean handleFollow(Message message) throws IOException {
		System.out.println("Message type Follow detected" + message.getStringMsg() );
		Client source = this.clientIdToClentObjMap.get(message.getSource());
		Client destination = this.clientIdToClentObjMap.get(message.getDestination());
		if(destination != null) {
			if (source !=null) {source.subscribe(destination.getId());}
			SelectionKey key = destination.getSelectionKey();
			SocketChannel socketChannel = (SocketChannel) key.channel();
			while(true) {
				if (key.isWritable()) {
					destination.writeToChannel(message.getStringMsg(), socketChannel);
					System.out.println("Follow msg sent to:" + destination.getId());
					return true;
				}
			}
		}
		return false;
		
	}
	
	public boolean handleUnfollow(Message message) {
		System.out.println("Message type Unfollow detected" + message.getStringMsg() );
		Client source = this.clientIdToClentObjMap.get(message.getSource());
		Client destination = this.clientIdToClentObjMap.get(message.getDestination());
		if(source != null & destination != null) {
			source.unsubscribe(destination.getId());
			System.out.println("Unsubscribed successfully : " + destination.getId());
			return true;
		}
		return false;
	}
	
	public boolean handleBroadcast(Message message) throws IOException {
		System.out.println("Message type Broadcast detected" + message.getStringMsg() );
		Client client;
		for (ConcurrentHashMap.Entry<Long, Client> entry : this.clientIdToClentObjMap.entrySet())
		{	
			client = entry.getValue();
			SelectionKey key = client.getSelectionKey();
			SocketChannel socketChannel = (SocketChannel) key.channel();
			while(true) {
				if (key.isWritable()) {
					client.writeToChannel(message.getStringMsg(), socketChannel);
					System.out.println("Broadcast msg sent to:" + client.getId());
					break;
				}
			}
		}
		return true;
	}
	
	
	public boolean handlePrivateMsg(Message message) throws IOException {
		System.out.println("Message type PrivateMsg detected : " + message.getStringMsg() );
		Client destination = this.clientIdToClentObjMap.get(message.getDestination());
		if (destination != null) {
			SelectionKey key = destination.getSelectionKey();
			SocketChannel socketChannel = (SocketChannel) key.channel();
			while(true) {
				if (key.isWritable()) {
					destination.writeToChannel(message.getStringMsg(), socketChannel);
					System.out.println("Private msg sent to:" +  destination.getId());
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean handleStatusUpdate(Message message) throws IOException {
		System.out.println("Message type Status Update detected : " + message.getStringMsg() );
		Client client;
		Client source = this.clientIdToClentObjMap.get(message.getSource());
		if(source !=null){
			LinkedList<Long> subscribers = source.getSubscribers();
			for (Long subscriber : subscribers)
			{	
				client = this.clientIdToClentObjMap.get(subscriber);
				if (client != null) {
					SelectionKey key = client.getSelectionKey();
					SocketChannel socketChannel = (SocketChannel) key.channel();
					while(true) {
						if (key.isWritable()) {
							client.writeToChannel(message.getStringMsg(), socketChannel);
							System.out.println("Status update msg sent to:" +  client.getId());
							break;
						}
					}
				}
			}
			return true;
		}
		return false;
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
		try {
			Thread.sleep(5000); // To fill the clientIdToClentObjMap Map with clients
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
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
