package fqr.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

	public static String SHA1(String decript) {
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * The sKey must be 16 bytes. Can use
	 * com.qq.weixin.mp.aes.WXBizMsgCrypt.getRandomStr() to generate.
	 * 
	 * @param sSrc
	 * @param sKey
	 * @return AES String in BASE64
	 */
	public static String EncryptAES(String sSrc, String sKey) {
		if (sKey == null) {
			System.out.print("Encryption AES DEBUG: Key is null");
			return null;
		}

		// 判断Key是否为16位
		if (sKey.length() != 16) {
			System.out.print("Encryption AES DEBUG: Key length not 16");
			return null;
		}

		try {
			byte[] raw = sKey.getBytes("utf-8");

			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

			// "算法/模式/补码方式"
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

			byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

			// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
			return Base64.getBase64(encrypted);
		} catch (Exception ex) {
			System.out.println("Encryption AES DEBUG: " + ex.toString());
			return null;
		}

	}

	/**
	 * The sKey must be 16 bytes
	 * 
	 * @param sSrc
	 * @param sKey
	 * @return Decrypted String.
	 */
	public static String DecryptAES(String sSrc, String sKey) {
		try {
			// 判断Key是否正确
			if (sKey == null) {
				System.out.print("Encryption AES DEBUG: Key is null");
				return null;
			}

			// 判断Key是否为16位
			if (sKey.length() != 16) {
				System.out.print("Encryption AES DEBUG: Key length not 16");
				return null;
			}

			byte[] raw = sKey.getBytes("utf-8");

			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

			cipher.init(Cipher.DECRYPT_MODE, skeySpec);

			byte[] encrypted1 = Base64.base64ToByte(sSrc);// 先用base64解密

			try {
				byte[] original = cipher.doFinal(encrypted1);
				String originalString = new String(original, "utf-8");
				return originalString;
			} catch (Exception e) {
				System.out.println(e.toString());
				return null;
			}

		} catch (Exception ex) {
			System.out.println("Encryption AES DEBUG: " + ex.toString());
			return null;

		}

	}

	// 随机生成16位字符串
	public static String getRandomStr() {
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 16; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
}
