package main.java.com.eventManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

/*@description: Responsible for reading messages from the event source and 
 * adding them in the message queue.
 * 
 * */
public class EventReader implements Runnable{
	
	private int tcpPort = 0;
	private ServerSocketChannel eventReaderSocket = null;
	private ByteBuffer eventBuf = ByteBuffer.allocate(1024);
	//Shared Data structure
	private ConcurrentLinkedQueue<String> messageQueue;
	

	
	public EventReader(int tcpPort, ConcurrentLinkedQueue<String> messageQueue) throws IOException {
		this.tcpPort = tcpPort;
		this.messageQueue = messageQueue;
 	}
	
	/*Read Event source channel and tokenize the messages from the byte stream.
	 * Add the messages in the shared messageQueue for further processing.
	 */
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
					System.out.println("[EventReader-Thread][tokenize] Message: " + message + " added in messageQueue");
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
			//Create a Channel for communicating with the Event source.
			System.out.println("[EventReader-Thread][run] Starting the EventReader Thread ...");
            this.eventReaderSocket = ServerSocketChannel.open();
            this.eventReaderSocket.bind(new InetSocketAddress(this.tcpPort));
            SocketChannel eventReaderSocketChannel = this.eventReaderSocket.accept();
            System.out.println("[EventReader-Thread][run] Event source connected successfully on port :" + this.tcpPort);
            eventReaderSocket.close();
            // Read channel for Messages
            while(true) {
            	this.tokenize(eventReaderSocketChannel);
    		}
        } catch(IOException | InterruptedException e){
            e.printStackTrace();
            return;
        }
	}
}
