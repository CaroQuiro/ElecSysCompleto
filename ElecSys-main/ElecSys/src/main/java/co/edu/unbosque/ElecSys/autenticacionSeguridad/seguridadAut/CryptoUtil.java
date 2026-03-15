package co.edu.unbosque.ElecSys.autenticacionSeguridad.seguridadAut;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Utilidad para el cifrado y descifrado de datos sensibles.
 * Implementa el algoritmo AES para proteger información antes de guardarla o transmitirla.
 */
public class CryptoUtil {

    private static final String ALGORITHM = "AES";

    private static final String SECRET_KEY = "vcElectricosClave256bits";

    /**
     * Prepara la llave secreta en el formato requerido por el cifrador.
     */
    private static SecretKeySpec getKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
    }

    /**
     * Convierte un texto plano en una cadena cifrada en Base64.
     */
    public static String encriptar(String texto) {
        try {
            if (texto == null) return null;

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());

            byte[] encrypted = cipher.doFinal(texto.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar", e);
        }
    }

    /**
     * Revierte un texto cifrado en Base64 a su estado original.
     */
    public static String desencriptar(String textoEncriptado) {
        try {
            if (textoEncriptado == null) return null;

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey());

            byte[] decoded = Base64.getDecoder().decode(textoEncriptado);
            byte[] decrypted = cipher.doFinal(decoded);

            return new String(decrypted);

        } catch (Exception e) {
            throw new RuntimeException("Error al desencriptar", e);
        }
    }
}
