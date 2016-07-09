package main.java.com.clientManager;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

	private int tcpPort;
	private BlockingQueue<SocketChannel> channelQueue; 
	private Map<Long, Client> clientIdToClentObjMap;
	private Selector channelSelector;
	
	public ConnectionManager(int tcpPort) throws IOException {
		System.out.println("Connection Manager created successfully...");
		this.channelQueue = new ArrayBlockingQueue<SocketChannel>(100);
		this.tcpPort = tcpPort;
		this.channelSelector= Selector.open();
		this.clientIdToClentObjMap = new ConcurrentHashMap<Long, Client>(1050); // Should be initialzed in other place
	}
	
	public void start() {
		ConnectionAcceptor connAcceptor = new ConnectionAcceptor(this.tcpPort, this.channelQueue);
		ConnectionProcessor connProcessor = new ConnectionProcessor(this.channelQueue, this.channelSelector, this.clientIdToClentObjMap);
		Thread connectionAccWorkerThread  = new Thread(connAcceptor);
		Thread connectionProcWorkerThread = new Thread(connProcessor); 
		connectionAccWorkerThread.start();
		connectionProcWorkerThread.start();
	}

}
