package main.java.com.messageProcessor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import main.java.com.clientManager.Client;

/*@description: Responsible for reading ordered message from orderedMsgQueue and
 * handling the messages i.e, sending them to the respective clients 
 * 
 * */
public class MessageHandler implements Runnable{

	private ConcurrentLinkedQueue<String> orderedMsgQueue;
	private ConcurrentHashMap<Long, Client> clientIdToClentObjMap;
	
	
	public MessageHandler(ConcurrentLinkedQueue<String> orderedMsgQueue, ConcurrentHashMap<Long, Client> clientIdToClentObjMap) {
		this.orderedMsgQueue = orderedMsgQueue;
		this.clientIdToClentObjMap = clientIdToClentObjMap;
	}
	
	// Handles message of type F- Follow
	public boolean handleFollow(Message message) throws IOException {
		Client source = this.clientIdToClentObjMap.get(message.getSource());
		Client destination = this.clientIdToClentObjMap.get(message.getDestination());
		// Destination is valid
		if(destination != null) {
			if (source !=null) {source.subscribe(destination.getId());}
			SelectionKey key = destination.getSelectionKey();
			SocketChannel socketChannel = (SocketChannel) key.channel();
			while(true) {
				if (key.isWritable()) {
					destination.writeToChannel(message.getStringMsg(), socketChannel);
					System.out.println("[MessageHandler-Thread][handleFollow] Follow msg sent to: " + destination.getId() + ".Message:" + message.getStringMsg());
					return true;
				}
			}
		}
		return false;
		
	}
	
	//Handles message of type U- Unfollow
	public boolean handleUnfollow(Message message) {
		Client source = this.clientIdToClentObjMap.get(message.getSource());
		Client destination = this.clientIdToClentObjMap.get(message.getDestination());
		// If source and destination are valid
		if(source != null & destination != null) {
			source.unsubscribe(destination.getId());
			System.out.println("[MessageHandler-Thread][handleUnfollow] Unsubscribed successfully : " + destination.getId()+ ".Message:" + message.getStringMsg());
			return true;
		}
		return false;
	}
	
	//Handles message of type B-Broadcast
	public boolean handleBroadcast(Message message) throws IOException {
		Client client;
		for (ConcurrentHashMap.Entry<Long, Client> entry : this.clientIdToClentObjMap.entrySet())
		{	
			client = entry.getValue();
			SelectionKey key = client.getSelectionKey();
			SocketChannel socketChannel = (SocketChannel) key.channel();
			while(true) {
				if (key.isWritable()) {
					client.writeToChannel(message.getStringMsg(), socketChannel);
					System.out.println("[MessageHandler-Thread][handleBroadcast] Broadcast msg sent to:" + client.getId() + ".Message:" + message.getStringMsg());
					break;
				}
			}
		}
		return true;
	}
	
	//Handles message of type P- Private Message
	public boolean handlePrivateMsg(Message message) throws IOException {
		Client destination = this.clientIdToClentObjMap.get(message.getDestination());
		if (destination != null) {
			SelectionKey key = destination.getSelectionKey();
			SocketChannel socketChannel = (SocketChannel) key.channel();
			while(true) {
				if (key.isWritable()) {
					destination.writeToChannel(message.getStringMsg(), socketChannel);
					System.out.println("[MessageHandler-Thread][handlePrivateMsg] Private msg sent to:" +  destination.getId()+ ".Message:" + message.getStringMsg());
					return true;
				}
			}
		}
		return false;
	}
	
	//Handles message of type S- Status Update
	public boolean handleStatusUpdate(Message message) throws IOException {
		Client client;
		Client source = this.clientIdToClentObjMap.get(message.getSource());
		//check source is valid
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
							System.out.println("[MessageHandler-Thread][handlePrivateMsg] Status update msg sent to:" +  client.getId() + ".Message:" + message.getStringMsg());
							break;
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	//Distributes the message for processing based on the type
	public void processMessage(Message message) throws IOException {
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
			// To fill the clientIdToClentObjMap Map with clients
			Thread.sleep(5000); 
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("[MessageHandler-Thread][run]Starting the MessageHandler Thread...");
		String message;
		while(true) {
			// Fetch message from orderedMsgQueue - MessageSequencer
			while ((message = this.orderedMsgQueue.poll()) != null) {
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
