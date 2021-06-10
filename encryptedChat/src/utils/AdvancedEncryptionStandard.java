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
 * Esta clase se encarga de la encriptación por medio del algoritmo AES-128.
 * La cual ontiene los métodos necesarios para encriptar textos planos y desencriptar textos cifrados.
 * Asimismo, se tiene el método generador de claves privadas.
 * @author: Alexis Bonilla, Cristian Cobo, Dennys Mosquera
 */
public class AdvancedEncryptionStandard {

	/**
     * Método estático que devuelve un objeto tipo SecretKeySpec con la clave privada. Hace uso del algortimo AES.
     * @param key El parámetro key almacena la clave privada del usuario que se usará para hacer la encriptación o desencriptación. 
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
     * Método estático que devuelve un objeto tipo String que devuelve el texto plano encriptado.
     * @param plaintext	El parámetro plaintext almacena el texto plano que debe ser encriptado.
     * @param key 		El parámetro key almacena la clave pública que se usará para hacer la encriptación.
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
     * Método estático que devuelve un objeto tipo String que devuelve el texto plano desencriptado.
     * @param ciphertext	El parámetro ciphertext almacena el texto encriptado que debe ser desencriptado.
     * @param key 			El parámetro key almacena la clave pública que se usó al momento de hacer la encriptación. 
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
