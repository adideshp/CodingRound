package main.java.com.eventManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

public class EventReader implements Runnable{
	
	private int tcpPort = 0;
	private ServerSocketChannel eventReaderSocket = null;
	private ByteBuffer eventBuf = ByteBuffer.allocate(1024);
	private BlockingQueue<String> messageQueue;

	
	public EventReader(int tcpPort, BlockingQueue<String> messageQueue) throws IOException {
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
					System.out.print(message);
					this.messageQueue.put(message);
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
			System.out.println("Starting the event reader worker thread ...");
            this.eventReaderSocket = ServerSocketChannel.open();
            this.eventReaderSocket.bind(new InetSocketAddress(tcpPort));
            SocketChannel eventReaderSocketChannel = this.eventReaderSocket.accept();
            System.out.println("Event source connected successfully on port :" + this.tcpPort);
            System.out.println("Contents:\n");
            while(true) {
            	this.tokenize(eventReaderSocketChannel);
    		}
        } catch(IOException | InterruptedException e){
            e.printStackTrace();
            return;
        }
	}
}
