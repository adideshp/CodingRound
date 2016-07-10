package main.java.com;


/*@description: Entry Class for the Exercise*/
public class FollowerMaze {

	public static void main(String[] args) {
		Server server = new Server(9099,9090);
		server.start();
	}
}
