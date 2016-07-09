package main.java.com;

import java.io.IOException;

public class FollowerMaze {

	public static void main(String[] args) {
		try {
			EventReader eventReader = new EventReader(9090);
			ConnectionManager conMan = new ConnectionManager(9099);
			Thread connectionWorkerThread  = new Thread(conMan);
			eventReader.start();
			connectionWorkerThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
