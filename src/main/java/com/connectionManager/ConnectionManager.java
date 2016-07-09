package main.java.com.connectionManager;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionManager {

	private int tcpPort;
	private BlockingQueue<SocketChannel> channelQueue; 
	
	public ConnectionManager(int tcpPort) {
		System.out.println("Connection Manager created successfully...");
		this.channelQueue = new ArrayBlockingQueue<SocketChannel>(100);
		this.tcpPort = tcpPort;
	}
	
	public void start() {
		ConnectionAcceptor connAcceptor = new ConnectionAcceptor(this.tcpPort, this.channelQueue);
		ConnectionProcessor connProcessor = new ConnectionProcessor(this.channelQueue);
		Thread connectionAccWorkerThread  = new Thread(connAcceptor);
		Thread connectionProcWorkerThread = new Thread(connProcessor); 
		connectionAccWorkerThread.start();
		connectionProcWorkerThread.start();
	}

}
