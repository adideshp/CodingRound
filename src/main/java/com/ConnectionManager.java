package main.java.com;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ConnectionManager implements Runnable{

	private int port;
	private ServerSocketChannel serverSocket = null;
	private int count=0;
	
	public ConnectionManager(int port) {
		this.port = port;
	}
	

	@Override
	public void run() {
		try{
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
                System.out.println("Connection Count : " + ++this.count);
            } catch(IOException e){
                e.printStackTrace();
            }
        }
	}
}
