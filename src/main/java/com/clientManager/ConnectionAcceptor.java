package main.java.com.clientManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
/*@description: Responsible for accepting connections and adding their handles in a shared queue.
 * */
public class ConnectionAcceptor implements Runnable{

	private int port;
	private ServerSocketChannel serverSocket = null;
	private  ConcurrentLinkedQueue<SocketChannel> channelQueue;
	
	private int connectionCount=0;
	
	public ConnectionAcceptor(int port,  ConcurrentLinkedQueue<SocketChannel> channelQueue) {
		this.port = port;
		this.channelQueue = channelQueue;
	}
	

	@Override
	public void run() {
		try{
			//Creating a ServerSocket
			System.out.println("[ConnectionAcceptor-Thread][run] Starting the ConnectionAcceptor thread ...");
            this.serverSocket = ServerSocketChannel.open();
            this.serverSocket.bind(new InetSocketAddress(this.port));
        } catch(IOException e){
            e.printStackTrace();
            return;
        }
		
        while(true){
            try{
                SocketChannel socketChannel = this.serverSocket.accept();
                System.out.println("[ConnectionAcceptor-Thread][run] Connection accepted");
                this.connectionCount++;
                System.out.println("[ConnectionAcceptor-Thread][run] Connection Count : " + this.connectionCount );
                this.channelQueue.add(socketChannel);
                System.out.println("[ConnectionAcceptor-Thread][run] Channel added to the channelQueue");
            } catch(IOException e){
                e.printStackTrace();
            }
        }
	}
}
