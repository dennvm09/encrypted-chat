package utils;


/**
 * Esta clase contiene los m�todos necesarios para que el algoritmo de Diffie Hellman funcione correctamente.
 * @author: Alexis Bonilla, Cristian Cobo, Dennys Mosquera
 */
public class DiffieHellmanKey {
	
	/**
	 * Elegimos estos n�meros a�n sabiendo que son f�ciles de vulnerar, debido a que esto es solo con un inter�s did�ctico.
	 */
	
	/**
	 * N�mero primo P cuya ra�z primitiva es 7
	 */
    public static final int P = 71;
    
    /**
	 * Ra�z primitiva del n�mero P 71
	 */
    public static final  int G = 7;

    
    
    /**
     * Metodo que calcula la clave p�blica a partir de un n�mero privado x menor que P. 
     * @return	Retorna la clave p�blica
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
     * M�todo que genera las claves p�blicas y privadas, n�tese que la clave privada tiene que ser un n�mero menor a P = 71, 
     * por lo que se elige un n�mero aleatorio entre 0 y 70.
     * Clave privada =  arreglo en la primera posici�n, clave p�blica = arreglo en la segunda posici�n.
     * @return Se retorna un arreglo que contiene las claves p�blicas y privadas.
     */
    public static int[] generateKeys() {
        int privateKey = (int) (Math.random() * 70);
        int publicKey = modularExponentiation(G, privateKey, P);
        return new int[] {privateKey, publicKey};
    }
    
    /**
     * M�todo encargado de encriptar un mensaje de texto plano utilizando la llave privada del 
     * emisor, la llave p�blica del receptor y el mensaje que se quiere encriptar
     */
    public static String encryptMessage(int myPrivateKey, int recipientPublicKey, String message) {
        int sharedKey = modularExponentiation(recipientPublicKey, myPrivateKey, P);
        return AdvancedEncryptionStandard.encrypt(message, sharedKey);
    }
    
    /**
     * M�todo encargado de desencriptar un mensaje de texto plano utilizando la llave privada del 
     * receptor, la llave p�blica del emisor y el mensaje que se quiere dsencriptar.
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