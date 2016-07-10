package main.java.com.clientManager;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/*@description: Responsible for creating and maintaining connection with many Clients
 * */
public class ConnectionManager {

	private int tcpPort;
	//Shared Data Structure between ConnectionAcceptor and ConnectorProcessor Thread
	private ConcurrentLinkedQueue<SocketChannel> channelQueue; 
	// Shared with MessageProcessor
	private ConcurrentHashMap<Long, Client> clientIdToClentObjMap;

	
	public ConnectionManager(int tcpPort, ConcurrentHashMap<Long, Client> clientIdToClentObjMap) throws IOException {
		this.channelQueue = new ConcurrentLinkedQueue<SocketChannel>();
		this.tcpPort = tcpPort;
		this.clientIdToClentObjMap = clientIdToClentObjMap;
	}
	
	public void start() throws IOException {
		System.out.println("[ConnectionManager][start] Connection Manager started successfully...");
		ConnectionAcceptor connAcceptor = new ConnectionAcceptor(this.tcpPort, this.channelQueue);
		ConnectionProcessor connProcessor = new ConnectionProcessor(this.channelQueue, this.clientIdToClentObjMap);
		Thread connectionAccWorkerThread  = new Thread(connAcceptor);
		Thread connectionProcWorkerThread = new Thread(connProcessor); 
		connectionAccWorkerThread.start();
		connectionProcWorkerThread.start();
	}

}
