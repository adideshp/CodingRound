package main.java.com.clientManager;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/*@description: Responsible for adding channels to selector, binding the SelectionKey with the client,
 * updating the client IDs and updating the clientIdToClentObjMap Map.
 * */
public class ConnectionProcessor implements Runnable{

	private ConcurrentLinkedQueue<SocketChannel> channelQueue;
	private Selector channelSelector; 
	//Shared Data Structure with MessageProcessor
	private ConcurrentHashMap<Long, Client> clientIdToClentObjMap; // Concurrent hash map
	
	public ConnectionProcessor(ConcurrentLinkedQueue<SocketChannel> channelQueue, ConcurrentHashMap<Long, Client> clientIdToClentObjMap) throws IOException{
		this.channelQueue = channelQueue;
		this.clientIdToClentObjMap = clientIdToClentObjMap;
		this.channelSelector = Selector.open();
	}
	
	/*@description : Create a client and attach it to the SelectionKey 
	 * */
	public boolean createClient(SocketChannel socketChannel, Selector channelSelector) throws IOException {
		socketChannel.configureBlocking(false);

		SelectionKey selectionKey = socketChannel.register(this.channelSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		// client id is not yet available hence initializing it with -1
		Client client = new Client(-1,selectionKey);
		selectionKey.attach(client);
		return true;
	}
	
	/*@description : Get Id by reading the client channel and update the Client and the 
	 * clientIdToClentObjMap Map.
	 * */
	public void updateIdForReadReadyClients(Selector channelSelector) throws IOException {
		int readyChannels = channelSelector.selectNow();
		if(readyChannels == 0) return;
		
		Set<SelectionKey> selectedKeys = channelSelector.selectedKeys();
		Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
		
		//Check for all the read ready channels
		while(keyIterator.hasNext()) {
		    SelectionKey key = keyIterator.next();
		    if (key.isReadable()) {
		    	SocketChannel channel = (SocketChannel) key.channel();
		    	Client client = (Client) key.attachment();
		    	long id = Long.parseLong(client.readFromChannel(channel));
		    	client.setId(id);
		    	System.out.println("[ConnectionProcessor-Thread][updateIdForReadReadyClients] Client ID :" + id + " mapped");
		    	this.clientIdToClentObjMap.put(id, client);
		    } 
		    keyIterator.remove();
	    }
	}
	

	@Override
	public void run() {
		System.out.println("[ConnectionProcessor-Thread][run] Starting the connection processor worker thread ...");
		SocketChannel socketChannel;
		while(true) {
			try {
				//Fetch channel from channelQueue
				while ((socketChannel = this.channelQueue.poll()) != null) {
					this.createClient(socketChannel, this.channelSelector);
					this.updateIdForReadReadyClients(channelSelector);
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
