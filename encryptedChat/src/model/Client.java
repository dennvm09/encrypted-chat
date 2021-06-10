package model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


import utils.DiffieHellmanKey;
import view.ClientChatUi;


public class Client {


	public String name;
	
	public Handler handler;
	
	private ClientChatUi clientChatUi;
	
	public Client(String name, String ip, int port) {
		
		this.name = name;

	
		clientChatUi = new ClientChatUi(name, this);
		
		handler = new Handler(name, ip, port);
		new Thread(handler).start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				handler.kill();
			}
		});
		
	}

	public void sendMessage(String recipient, String body, boolean encrypted) {
		
		String id = String.valueOf(System.currentTimeMillis() / 1000) + "_" + String.valueOf((int) (Math.random() * 1000));
		
		String[] message = new String[] {name, recipient, body};
		handler.messages.put(id, message);
		clientChatUi.refresh(handler.clients, handler.messages);
		if (encrypted) {
			int[] keyPair = DiffieHellmanKey.generateKeys();
			handler.keys.put(id, keyPair);
			handler.send("KeyRequest", id, recipient, String.valueOf(keyPair[1]));
		} else {
			handler.send("Message", id, String.join("\t", message));
		}
		
	}
	

	class Handler implements Runnable {
		
		private Socket server;
		
		private PrintWriter writer;
		
		private BufferedReader reader;
	
		private boolean alive = true;

	    public ArrayList<String> clients = new ArrayList<>();
	   
	    public TreeMap<String, String[]> messages = new TreeMap<String, String[]>();
	   
		private Map<String, int[]> keys = new HashMap<String, int[]>();

		public Handler(String name, String ip, int port) {
			
			try {
				
				server = new Socket(ip, port);
				
				OutputStream out = server.getOutputStream();
				writer = new PrintWriter(out);
				InputStream in = server.getInputStream();
				reader = new BufferedReader(new InputStreamReader(in));
				
				send("Client", name);
			
			} catch (Exception e) { e.printStackTrace(); }
			
			clientChatUi.refresh(clients, messages);
			
		}
	
		public void kill() {
			alive = false;
		}
	
		public void send(String ...bodyParts) {
			String body = String.join("\t", bodyParts);
			writer.write(body + "\n");
			writer.flush();
		}
	
		private void handleClients(String[] parts) {
			
			clients.clear();
			if (parts[1].equals("null")) return;
			
			for (int i = 1; i < parts.length; i++) {
				clients.add(parts[i]);
			}

			clientChatUi.refresh(clients, messages);
			
		}
	
		private void handleMessages(String[] parts) {
			
			if (parts.length < 2 || parts[1].equals("null")) {
				return;
			}
			
			for (int i = 1; i < parts.length; i += 4) {
				if (!messages.containsKey(parts[i])) {
					messages.put(parts[i], Arrays.copyOfRange(parts, i + 1, i + 4));
				}
			}
			clientChatUi.refresh(clients, messages);
			
		}
	
		private void handleNewKeyRequest(String[] parts) {

			String id = parts[1];
			String recipient = parts[2];
			int senderPublicKey = Integer.parseInt(parts[3]);
			
			if (!recipient.equals(name)) return;
			
			int[] keyPair = DiffieHellmanKey.generateKeys();
			keys.put(parts[1], new int[] {keyPair[0], senderPublicKey});
			
			send("KeyResponse", id, String.valueOf(keyPair[1]));
			
		}
		
	
		private void handleNewKeyResponse(String[] parts) {
			
			String id = parts[1];
			int recipientPublicKey = Integer.parseInt(parts[2]);

			if (messages.containsKey(id) && keys.containsKey(id)) {
				String[] message = Arrays.copyOf(messages.get(id), 3);
				int[] keyPair = new int[] {keys.get(id)[0], recipientPublicKey};
				message[2] = DiffieHellmanKey.encryptMessage(keyPair[0], keyPair[1], message[2]);
				send("Message", id, String.join("\t", message));
			}
			
		}
	
		private void handleNewMessage(String[] parts) {

			String id = parts[1];
			String[] message = Arrays.copyOfRange(parts, 2, 5);
			
			if (message[0].equals(name) || messages.containsKey(id)) return;
			
			if (keys.containsKey(id)) {
				int myPrivateKey = keys.get(id)[0];
				int senderPublicKey = keys.get(id)[1];
				message[2] = DiffieHellmanKey.decryptMessage(myPrivateKey, senderPublicKey, message[2]);
			}

			messages.put(id, message);
			clientChatUi.refresh(clients, messages);
			
		}
	
		public void run() {
			
			try {
				
				String body;
				
				while (!(body = reader.readLine()).startsWith("Quit") && alive) {
					
					System.out.println(body);
					String[] parts = body.split("\t");
					
					if (parts.length == 0) continue;
					
					if (parts[0].equals("Clients")) {
						handleClients(parts);
					} else if (parts[0].equals("Messages")) {
						handleMessages(parts);
					} else if (parts[0].equals("Message")) {
						handleNewMessage(parts);
					} else if (parts[0].equals("KeyRequest")) {
						handleNewKeyRequest(parts);
					} else if (parts[0].equals("KeyResponse")) {
						handleNewKeyResponse(parts);
					}
					
				}
				
				send("Quit");
				
				writer.close();
				reader.close();
				server.close();
				
			} catch (Exception e) { e.printStackTrace(); }
			
		}
		
	}
}
