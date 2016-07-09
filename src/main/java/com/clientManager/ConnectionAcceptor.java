package main.java.com.clientManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

public class ConnectionAcceptor implements Runnable{

	private int port;
	private ServerSocketChannel serverSocket = null;
	private BlockingQueue<SocketChannel> channelQueue;
	
	private int count=0;
	
	public ConnectionAcceptor(int port, BlockingQueue<SocketChannel> channelQueue) {
		this.port = port;
		this.channelQueue = channelQueue;
	}
	

	@Override
	public void run() {
		try{
			System.out.println("Starting the connection acceptor worker thread ...");
            this.serverSocket = ServerSocketChannel.open();
            this.serverSocket.bind(new InetSocketAddress(this.port));
        } catch(IOException e){
            e.printStackTrace();
            return;
        }
		
        while(true){
            try{
                SocketChannel socketChannel = this.serverSocket.accept();
                System.out.println("Connection accepted: " + socketChannel);
                this.count++;
                System.out.println("Connection Count : " + this.count );
                this.channelQueue.put(socketChannel);
                System.out.println("Channel added to the queue");
                
            } catch(IOException | InterruptedException e){
                e.printStackTrace();
            }
        }
	}
}
