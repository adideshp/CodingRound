package main.java.com.connectionManager;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

public class ConnectionProcessor implements Runnable{

	private BlockingQueue<SocketChannel> channelQueue;
	private Selector channelSelector; 
	private int clientCount;
	
	public ConnectionProcessor(BlockingQueue<SocketChannel> channelQueue){
		this.channelQueue = channelQueue;
		this.clientCount = 0;
	}
	
	public boolean createClient(SocketChannel socketChannel, Selector channelSelector) throws IOException {
		socketChannel.configureBlocking(false);
		SelectionKey selectionKey = socketChannel.register(this.channelSelector, SelectionKey.OP_READ);
		Client client = new Client(-1);
		selectionKey.attach(client);
		this.clientCount++;
		System.out.println("Client " + this.clientCount +" attached Successfully...");
		return true;
	}
	

	@Override
	public void run() {
		System.out.println("Starting the connection processor worker thread ...");
		while(true) {
			try {
				SocketChannel socketChannel = this.channelQueue.take();
				System.out.println("Fetched channel from queue");
				this.channelSelector= Selector.open();
				this.createClient(socketChannel,this.channelSelector);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}

}
