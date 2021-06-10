package model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;


public class Server {
	
	public Map<String, Handler> clients = new HashMap<String, Handler>();
	public TreeMap<String, String[]> messages = new TreeMap<String, String[]>();
	
	public Server(int port) {
		
		ServerSocket server;
		
		try {
			
			server = new ServerSocket(port);
			System.out.println("El servidor está corriendo.");
			
			while (true) {
				try {
					Socket client = server.accept();
					Handler handler = new Handler(client);
					new Thread(handler).start();
				} catch (Exception e) { e.printStackTrace(); }
			}
			
		} catch (Exception e) { e.printStackTrace(); }

		
	}
	

	public void sendToAll(String ...bodyParts) {
		for (Entry<String, Handler> clientEntry: clients.entrySet()) {
			clientEntry.getValue().sendToThis(bodyParts);
		}
	}
	

	private String clientsToString() {
		int i = 0;
		String[] clientsStrings = new String[clients.size()];
		for (Handler clientValue: clients.values()) {
			clientsStrings[i++] = clientValue.name;
		}
		return String.join("\t", clientsStrings);
	}
	
	private String messagesToString() {
		int i = 0;
		String[] messagesStrings = new String[messages.size()];
		for (Entry<String, String[]> messageEntry: messages.entrySet()) {
			messagesStrings[i++] = messageEntry.getKey() + "\t" + String.join("\t", messageEntry.getValue());
		}
		return String.join("\t", messagesStrings);
	}
	
	
	private class Handler implements Runnable {
		
		
		public Socket client;
		private PrintWriter writer;
		private BufferedReader reader;
		public String name;
		private boolean alive = true;

		public Handler(Socket client) {
			
			this.client = client;
			
			try {
				OutputStream out = client.getOutputStream();
				writer = new PrintWriter(out);
				InputStream in = client.getInputStream();
				reader = new BufferedReader(new InputStreamReader(in));
			} catch (Exception e) { e.printStackTrace(); }
			
		}
		
		
		public void kill() {
			alive = false;
		}
		
		
		public void sendToThis(String ...bodyParts) {
			String body = String.join("\t", bodyParts);
			writer.write(body + "\n");
			writer.flush();
		}
		
		
		private void handleNewClient(String[] parts) {
			
			String name = parts[1];
			this.name = name;
			
			if (clients.containsKey(name)) {
				clients.get(name).kill();
				clients.remove(name);
			}
			
			clients.put(name, this);
			sendToAll("Clients", clientsToString());
			sendToThis("Messages", messagesToString());
		}
		
		private void handleNewMessage(String[] parts) {
			String id = parts[1];
			String[] message = Arrays.copyOfRange(parts, 2, 5);
			
			messages.put(id, message);
		}

		public void run() {
			
			try {
				
				String body;
				while (!(body = reader.readLine()).startsWith("Quit") && alive) {
					
					String[] parts = body.split("\t");
					
					if (parts.length == 0) continue;
					
					if (parts[0].equals("Client")) {
						handleNewClient(parts);
					} else if (parts[0].equals("Message")) {
						handleNewMessage(parts);
					}

					sendToAll(body);
					
				}
				
				if (clients.containsKey(name)) {
					clients.remove(name);
				}

				sendToThis("Quit");
				sendToAll("Clients", clientsToString());
				
				writer.close();
				reader.close();
				client.close();
				
			} catch (Exception e) { e.printStackTrace(); }
			
		}

	}
	
	public static void main(String[] args) {
		new Server(4000);
	}


}
