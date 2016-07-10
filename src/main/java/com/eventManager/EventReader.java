package main.java.com.eventManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventReader implements Runnable{
	
	private int tcpPort = 0;
	private ServerSocketChannel eventReaderSocket = null;
	private ByteBuffer eventBuf = ByteBuffer.allocate(1024);
	private ConcurrentLinkedQueue<String> messageQueue;
	

	
	public EventReader(int tcpPort, ConcurrentLinkedQueue<String> messageQueue) throws IOException {
		this.tcpPort = tcpPort;
		this.messageQueue = messageQueue;
 	}
	
	public boolean tokenize(SocketChannel eventReaderSocketChannel) throws IOException, InterruptedException {
		char bufChar;
		String message = "";
		long readSize = eventReaderSocketChannel.read(this.eventBuf);
		if (readSize != -1) {
			this.eventBuf.flip();
			while (eventBuf.hasRemaining()) {
				bufChar = (char) eventBuf.get();
				if(bufChar != '\n') {
					message += bufChar; 
				} else {
					message += '\n';
					this.messageQueue.add(message);
					System.out.println("Message" + message + " added in queue");
					message = "";
				}
			}
			this.eventBuf.clear();
		}
		return true;
	}
	
	
	@Override
	public void run() {
		try{
			System.out.println("Starting the EventReader Thread ...");
            this.eventReaderSocket = ServerSocketChannel.open();
            this.eventReaderSocket.bind(new InetSocketAddress(tcpPort));
            SocketChannel eventReaderSocketChannel = this.eventReaderSocket.accept();
            System.out.println("Event source connected successfully on port :" + this.tcpPort);
            while(true) {
            	this.tokenize(eventReaderSocketChannel);
    		}
        } catch(IOException | InterruptedException e){
            e.printStackTrace();
            return;
        }
	}
}
