package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import model.Client;


public class ClientChatUi {

	private String name;

	private JComboBox<String> receiver;
	
	private JTextArea textAreaMessage;
	
	private JScrollPane scroll;

	public  ClientChatUi(String name, Client client) {
		
		Random random = new Random();
		final float hue = random.nextFloat();
		final float saturation = (random.nextInt(2000) + 1000) / 10000f;
		final float luminance = 0.9f;
		final Color colorText = Color.getHSBColor(hue, saturation, luminance);
		
		this.name = name;
		
		JFrame frame = new JFrame(name);
		frame.setSize(400,400);
		frame.setResizable(false);
		frame.setBackground(colorText);
		JPanel panel = new JPanel(new BorderLayout());

		GridLayout grid = new GridLayout(3,1);
		
		
		JPanel controlsPanel = new JPanel();
		controlsPanel.setBackground(colorText);
		JTextField textField = new JTextField(30);
		JCheckBox encryptionCheckBox = new JCheckBox("Encriptar mensaje", false);
		receiver = new JComboBox<>();
		JButton sendButton = new JButton("Enviar");
		
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String recipient = receiver.getSelectedItem().toString();
				String body = textField.getText();
				Boolean encrypted = encryptionCheckBox.isSelected();
				client.sendMessage(recipient, body, encrypted);
				textField.setText("");
			}
		});
		
		controlsPanel.setLayout(grid);
		controlsPanel.add(textField);
		controlsPanel.add(receiver);
		controlsPanel.add(encryptionCheckBox);
		controlsPanel.add(sendButton);
		
		
		panel.add(controlsPanel, BorderLayout.SOUTH);
		
		frame.getContentPane().add(panel);
		
		textAreaMessage = new JTextArea();
		scroll = new JScrollPane(textAreaMessage);
		textAreaMessage.setBackground(colorText);
		textAreaMessage.setEditable(false);
		panel.add(scroll, BorderLayout.CENTER);
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.setSize(400, 400);
		frame.setVisible(true);
		frame.revalidate();
		frame.repaint();
		
	}

	@SuppressWarnings("unused")
	public void refresh(ArrayList<String> clients, TreeMap<String, String[]> messages) {
		
		if (messages != null) {
	        
	        String texto="     Hora:\tRemitente:\tDestinatario:\tMensaje:\n";
	        
		    for (Entry<String, String[]> messageEntry: messages.entrySet()) {

		        if (messageEntry.getKey().equals("null")) continue;

		        long milis = Long.valueOf(messageEntry.getKey().split("_")[0]) * 1000;

		        String time = (new SimpleDateFormat("HH:mm:ss")).format(new Date(milis));
		        String sender = messageEntry.getValue()[0];
		        String recipient = messageEntry.getValue()[1];
		        String body = messageEntry.getValue()[2];
				boolean isSender = messageEntry.getValue()[0].equals(name);
		        boolean isReceiver = messageEntry.getValue()[1].equals(name);
		        
		        texto+= "     "+time+"\t"+sender+"\t"+recipient+"\t"+body+"\n";

		    }
		    textAreaMessage.setText(texto);
		    
		}
		
		receiver.removeAllItems();
		
		for (String client: clients) {
			receiver.addItem(client);
		}
		
	}
	

}
