package main.java.com.connectionManager;

import java.util.LinkedList;

public class Client {

	private int id;
	private LinkedList<Integer> subscribers;
	
	public Client(int id) {
		this.id = id;
		this.subscribers = new LinkedList<Integer>();
	}

	public int getId() {
		return this.id;
	}
	
	public LinkedList<Integer> getSubscribers() {
		return this.subscribers;
	}
	
	
	public boolean subscribe(int clientId) {
		this.subscribers.add(clientId);
		return true;
	}
	
	public boolean unsubscribe(Client client) {
		if (this.subscribers.contains(client)) {
			this.subscribers.remove(client);
			return true;
		}
		return false;
	}
	
	
}
