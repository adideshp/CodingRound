package main.java.com.clientManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionAcceptor implements Runnable{

	private int port;
	private ServerSocketChannel serverSocket = null;
	private  ConcurrentLinkedQueue<SocketChannel> channelQueue;
	
	private int count=0;
	
	public ConnectionAcceptor(int port,  ConcurrentLinkedQueue<SocketChannel> channelQueue) {
		this.port = port;
		this.channelQueue = channelQueue;
	}
	

	@Override
	public void run() {
		try{
			System.out.println("Starting the ConnectionAcceptor thread ...");
            this.serverSocket = ServerSocketChannel.open();
            this.serverSocket.bind(new InetSocketAddress(this.port));
        } catch(IOException e){
            e.printStackTrace();
            return;
        }
		
        while(true){
            try{
                SocketChannel socketChannel = this.serverSocket.accept();
                System.out.println("Connection accepted");
                this.count++;
                System.out.println("Connection Count : " + this.count );
                this.channelQueue.add(socketChannel);
                System.out.println("Channel added to the queue");
                
            } catch(IOException e){
                e.printStackTrace();
            }
        }
	}
}
