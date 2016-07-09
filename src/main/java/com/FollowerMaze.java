package main.java.com;

import java.io.IOException;

import main.java.com.connectionManager.ConnectionManager;
import main.java.com.sequentializer.EventReader;

public class FollowerMaze {

	public static void main(String[] args) {
		try {
			EventReader eventReader = new EventReader(9090);
			ConnectionManager connManager = new ConnectionManager(9099);
			eventReader.start();
			connManager.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
