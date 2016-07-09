package main.java.com;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class EventReaderWorker implements Runnable{
	
	private int tcpPort = 0;
	private ServerSocketChannel eventReaderSocket = null;
	private ByteBuffer eventBuf = ByteBuffer.allocate(1000); 
	
	public EventReaderWorker(int tcpPort) throws IOException {
		this.tcpPort = tcpPort;
 	}
	
	public boolean readMessage(SocketChannel eventReaderSocketChannel) throws IOException {
		int readSize = eventReaderSocketChannel.read(eventBuf);
		System.out.println(readSize);
		if (readSize != -1) {
			eventBuf.flip();
			while(eventBuf.hasRemaining()){
			      System.out.print((char) eventBuf.get()); // read 1 byte at a time
			  }
			eventBuf.clear(); //make buffer ready for writing
		}
		return true;
	}
	
	
	@Override
	public void run() {
		try{
			System.out.println("\nStarting the event reader worker thread ...");
            this.eventReaderSocket = ServerSocketChannel.open();
            this.eventReaderSocket.bind(new InetSocketAddress(tcpPort));
            SocketChannel eventReaderSocketChannel = this.eventReaderSocket.accept();
            System.out.println("\nEvent source connected successfully on port :" + this.tcpPort);
            System.out.println("\nContents:\n");
            while(true) {
            	this.readMessage(eventReaderSocketChannel);
    		}
        } catch(IOException e){
            e.printStackTrace();
            return;
        }
	}
}
