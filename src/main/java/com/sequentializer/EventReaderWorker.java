package main.java.com.sequentializer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class EventReaderWorker implements Runnable{
	
	private int tcpPort = 0;
	private ServerSocketChannel eventReaderSocket = null;
	private ByteBuffer eventBuf = ByteBuffer.allocate(1024); 

	
	public EventReaderWorker(int tcpPort) throws IOException {
		this.tcpPort = tcpPort;
 	}
	
	public boolean tokenize(SocketChannel eventReaderSocketChannel) throws IOException {
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
        } catch(IOException e){
            e.printStackTrace();
            return;
        }
	}
}
