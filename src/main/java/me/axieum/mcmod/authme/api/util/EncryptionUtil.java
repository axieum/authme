package me.axieum.mcmod.authme.api.util;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import me.axieum.mcmod.authme.impl.AuthMe;

public final class EncryptionUtil
{
    private static final Cipher CIPHER;

    static {
        try {
            CIPHER = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (Exception e) { // can't happen
            throw new RuntimeException(e);
        }
    }

    private EncryptionUtil() {}

    /**
     * Generates a random 128-bit key.
     *
     * @return Base64 encoded key
     */
    public static String generateKey()
    {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(256);
            SecretKey key = keygen.generateKey();
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (NoSuchAlgorithmException e) { // can't happen
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String b64ciphertext, String b64key)
    {
        try {
            byte[] key = Base64.getDecoder().decode(b64key);
            byte[] ciphertext = Base64.getDecoder().decode(b64ciphertext);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(key, 0, 16);
            CIPHER.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return new String(CIPHER.doFinal(ciphertext));
        } catch (Exception e) {
            AuthMe.LOGGER.error("An unexpected error during encryption occurred:");
            e.printStackTrace();
            return "";
        }
    }

    public static String encrypt(String plaintext, String b64key)
    {
        try {
            byte[] key = Base64.getDecoder().decode(b64key);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(key, 0, 16);
            CIPHER.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return Base64.getEncoder().encodeToString(CIPHER.doFinal(plaintext.getBytes()));
        } catch (Exception e) {
            AuthMe.LOGGER.error("An unexpected error during decryption occurred:");
            e.printStackTrace();
            return "";
        }
    }
}
