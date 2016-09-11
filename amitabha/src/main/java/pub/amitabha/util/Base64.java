package pub.amitabha.util;

import java.nio.charset.StandardCharsets;

/**
 * This class is all about UTF-8 String, not binary thing.
 * @author Ems
 *
 */
public class Base64 {
	/**
	 * A binary byte array, to base64 byte array.
	 * 
	 * @param b
	 * @return
	 */
	public static byte[] toBase64(byte[] b) {
		return java.util.Base64.getEncoder().encode(b);
	}

	/**
	 * A base64 byte array, to original binary byte array.
	 * @param b
	 * @return
	 */
	public static byte[] fromBase64(byte[] b) {
		return java.util.Base64.getDecoder().decode(b);
	}
	
	/**
	 * Given an Original String in UTF-8, encode it into a base64 string in UTF-8.
	 * @param str
	 * @return
	 */
	public static String getBase64(String str) {
		return getBase64(str.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Given a binary array, encode it into a base64 string in UTF-8.
	 * @param b
	 * @return
	 */
	public static String getBase64(byte[] b) {
		return new String(toBase64(b), StandardCharsets.UTF_8);
	}

	/**
	 * Given a Base64 String in UTF-8 format. decode to binary byte.
	 * @param s
	 * @return
	 */
	public static byte[] base64ToByte(String s) {
		return fromBase64(s.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Given a base64 String in UTF-8, try to get it's original UTF-8 String.
	 * @param s
	 * @return
	 */
	public static String getFromBase64(String s) {
		return getFromBase64(s.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Given a base64 byte array, decode it to the original String in UTF-8.
	 * @param b
	 * @return
	 */
	public static String getFromBase64(byte[] b) {
		return new String(fromBase64(b), StandardCharsets.UTF_8);
	}
}