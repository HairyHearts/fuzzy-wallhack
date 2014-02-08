package com.hairyhearts.hairyhearts;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class Encoding {

	private final static String CRIPTE_TIPE = "AES"; // AES
	//	private final static String HEX = "0123456789ABCDEF";

	public static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(raw);
		KeyGenerator kg = KeyGenerator.getInstance(CRIPTE_TIPE);
		kg.init(128, sr);
		SecretKeySpec skeySpec = new SecretKeySpec((kg.generateKey()).getEncoded(), CRIPTE_TIPE);
		Cipher cipher = Cipher.getInstance(CRIPTE_TIPE);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	public static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(raw);
		KeyGenerator kg = KeyGenerator.getInstance(CRIPTE_TIPE);
		kg.init(128, sr);
		SecretKeySpec skeySpec = new SecretKeySpec((kg.generateKey()).getEncoded(), CRIPTE_TIPE);
		Cipher cipher = Cipher.getInstance(CRIPTE_TIPE);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}


	public static String encryptText(String message, String password) throws Exception{
		return new String(encrypt(password.getBytes(), message.getBytes()));
	}

	public static String decryptText(String message, String password) throws Exception{
		return new String(decrypt(password.getBytes(), message.getBytes()));
	}
}

