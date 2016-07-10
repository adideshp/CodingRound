package main.java.com.clientManager;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class ConnectionProcessor implements Runnable{

	private BlockingQueue<SocketChannel> channelQueue;
	private Selector channelSelector; 
	private int clientCount;
	private Map<Long, Client> clientIdToClentObjMap; // Concurrent hash map
	
	public ConnectionProcessor(BlockingQueue<SocketChannel> channelQueue, Selector channelSelector, Map<Long, Client> clientIdToClentObjMap){
		this.channelQueue = channelQueue;
		this.clientIdToClentObjMap = clientIdToClentObjMap;
		this.channelSelector = channelSelector;
		this.clientCount = 0;
	}
	
	
	public boolean createClient(SocketChannel socketChannel, Selector channelSelector) throws IOException {
		socketChannel.configureBlocking(false);
		SelectionKey selectionKey = socketChannel.register(this.channelSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		Client client = new Client(-1,selectionKey);
		selectionKey.attach(client);
		this.clientCount++;
		System.out.println("Client " + this.clientCount +" attached Successfully...");
		return true;
	}
	
	
	public void updateIdForReadReadyClients(Selector channelSelector) throws IOException {
		int readyChannels = channelSelector.selectNow();
		if(readyChannels == 0) return;
		
		Set<SelectionKey> selectedKeys = channelSelector.selectedKeys();
		Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
		
		while(keyIterator.hasNext()) {
		    SelectionKey key = keyIterator.next();
		    if (key.isReadable()) {
		    	SocketChannel channel = (SocketChannel) key.channel();
		    	Client client = (Client) key.attachment();
		    	long id = Long.parseLong(client.readFromChannel(channel));
		    	client.setId(id);
		    	this.clientIdToClentObjMap.put(id, client);
		    } 
		    keyIterator.remove();
	    }
	}
	

	@Override
	public void run() {
		System.out.println("Starting the connection processor worker thread ...");
		while(true) {
			try {
				SocketChannel socketChannel = this.channelQueue.take();
				System.out.println("Fetched channel from queue");
				this.createClient(socketChannel,this.channelSelector);
				this.updateIdForReadReadyClients(channelSelector);
				
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}

}