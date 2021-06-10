package utils;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;


/**
 * Esta clase se encarga de la encriptaci�n por medio del algoritmo AES-128.
 * La cual ontiene los m�todos necesarios para encriptar textos planos y desencriptar textos cifrados.
 * Asimismo, se tiene el m�todo generador de claves privadas.
 * @author: Alexis Bonilla, Cristian Cobo, Dennys Mosquera
 */
public class AdvancedEncryptionStandard {

	/**
     * M�todo est�tico que devuelve un objeto tipo SecretKeySpec con la clave privada. Hace uso del algortimo AES.
     * @param key El par�metro key almacena la clave privada del usuario que se usar� para hacer la encriptaci�n o desencriptaci�n. 
     * @return La clave privada en formato bytes.
     */
	private static SecretKeySpec getSecretKey(String key) {

		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] keyBytes = sha.digest(key.getBytes("UTF-8"));
			keyBytes = Arrays.copyOf(keyBytes, 16);
			return new SecretKeySpec(keyBytes, "AES");
		} catch (Exception e) { e.printStackTrace(); }

		return null;
	}

	/**
     * M�todo est�tico que devuelve un objeto tipo String que devuelve el texto plano encriptado.
     * @param plaintext	El par�metro plaintext almacena el texto plano que debe ser encriptado.
     * @param key 		El par�metro key almacena la clave p�blica que se usar� para hacer la encriptaci�n.
     * @return 			El texto plano encriptado o null en caso de error. 
     */
	public static String encrypt(String plaintext, int key) {

		try {
			SecretKeySpec secretKey = getSecretKey(String.valueOf(key));
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(plaintext.getBytes("UTF-8")));
		} catch (Exception e) { e.printStackTrace(); }

		return plaintext;

	}
	

	/**
     * M�todo est�tico que devuelve un objeto tipo String que devuelve el texto plano desencriptado.
     * @param ciphertext	El par�metro ciphertext almacena el texto encriptado que debe ser desencriptado.
     * @param key 			El par�metro key almacena la clave p�blica que se us� al momento de hacer la encriptaci�n. 
     * @return 				El texto cifrado desencriptado o null en caso de error. 
     */
	public static String decrypt(String ciphertext, int key) {

		try {
			SecretKeySpec secretKey = getSecretKey(String.valueOf(key));
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)));
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (Exception e) { e.printStackTrace(); }
		
		return ciphertext;
		
	}

}
