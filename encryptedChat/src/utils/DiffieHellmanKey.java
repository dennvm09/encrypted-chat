package utils;


/**
 * Esta clase contiene los métodos necesarios para que el algoritmo de Diffie Hellman funcione correctamente.
 * @author: Alexis Bonilla, Cristian Cobo, Dennys Mosquera
 */
public class DiffieHellmanKey {
	
	/**
	 * Elegimos estos números aún sabiendo que son fáciles de vulnerar, debido a que esto es solo con un interés didáctico.
	 */
	
	/**
	 * Número primo P cuya raíz primitiva es 7
	 */
    public static final int P = 71;
    
    /**
	 * Raíz primitiva del número P 71
	 */
    public static final  int G = 7;

    
    
    /**
     * Metodo que calcula la clave pública a partir de un número privado x menor que P. 
     * @return	Retorna la clave pública
     */
    private static int modularExponentiation(int g, int x, int p) {
        if (x == 0) {
            return 1;
        } else if (x == 1) {
            return g % p;
        } else if (x % 2 == 1) {
            int h = modularExponentiation(g, x - 1, p);
            return (h * g) % p;
        } else {
            int h = modularExponentiation(g, x / 2, p);
            return (h * h) % p;
        }
    }
    
    /**
     * Método que genera las claves públicas y privadas, nótese que la clave privada tiene que ser un número menor a P = 71, 
     * por lo que se elige un número aleatorio entre 0 y 70.
     * Clave privada =  arreglo en la primera posición, clave pública = arreglo en la segunda posición.
     * @return Se retorna un arreglo que contiene las claves públicas y privadas.
     */
    public static int[] generateKeys() {
        int privateKey = (int) (Math.random() * 70);
        int publicKey = modularExponentiation(G, privateKey, P);
        return new int[] {privateKey, publicKey};
    }
    
    /**
     * Método encargado de encriptar un mensaje de texto plano utilizando la llave privada del 
     * emisor, la llave pública del receptor y el mensaje que se quiere encriptar
     */
    public static String encryptMessage(int myPrivateKey, int recipientPublicKey, String message) {
        int sharedKey = modularExponentiation(recipientPublicKey, myPrivateKey, P);
        return AdvancedEncryptionStandard.encrypt(message, sharedKey);
    }
    
    /**
     * Método encargado de desencriptar un mensaje de texto plano utilizando la llave privada del 
     * receptor, la llave pública del emisor y el mensaje que se quiere dsencriptar.
     * @param myPrivateKey
     * @param senderPublicKey
     * @param message
     * @return Retorna el mensaje desencriptado
     */
    public static String decryptMessage(int myPrivateKey, int senderPublicKey, String message) {
    	
        int sharedKey = modularExponentiation(senderPublicKey, myPrivateKey, P);
        return AdvancedEncryptionStandard.decrypt(message, sharedKey);
    }
}