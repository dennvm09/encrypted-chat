package app;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import model.Client;



public class encryptedChatApp {

	private static final String IP="localhost";

	public static void main(String[] args) {

	
		String nombre = JOptionPane.showInputDialog(null,"Nombre del usuario", "Registro chat",JOptionPane.INFORMATION_MESSAGE);
		JOptionPane.showMessageDialog(null,nombre + " registrado con éxito.");
		
		if (nombre!=null) {
			new Client(nombre, IP, 4000);
		}
		
	}

}
