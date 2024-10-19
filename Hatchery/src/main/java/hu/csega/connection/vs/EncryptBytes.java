package hu.csega.connection.vs;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptBytes {

	public static final int BLOCK_SIZE = 2048;

	public static final Charset UTF8 = Charset.forName("UTF-8");

	private byte[] keyBytes;
	private byte[] ivBytes;

	public EncryptBytes() {
		this.keyBytes = VirtualSpaceProperties.ENCRYPTION_KEY;
		this.ivBytes = VirtualSpaceProperties.ENCRYPTION_IV;
	}

	public String hash(String id) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(id.getBytes(UTF8));
			byte[] hashValue = digest.digest();
			return Base64.getEncoder().encodeToString(hashValue);
		} catch(Exception ex) {
			throw new RuntimeException("Error when initializing encryption layer!", ex);
		}
	}

	public byte[] encrypt(byte[] input) {
		try {
			SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
			IvParameterSpec iv = new IvParameterSpec(ivBytes);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            byte[] encrypted = cipher.doFinal(input);
			return encrypted;
		} catch(Exception ex) {
			throw new RuntimeException("Error during encryption!", ex);
		}
	}

	public byte[] decrypt(byte[] input) {
		try {
	        SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
	        IvParameterSpec iv = new IvParameterSpec(ivBytes);

	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

	        byte[] original = cipher.doFinal(input);
			return original;
		} catch(Exception ex) {
			throw new RuntimeException("Error during encryption!", ex);
		}
	}
}
