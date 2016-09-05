package pub.amitabha.util;

import java.nio.charset.StandardCharsets;

public class Base64 {
	// 加密
	public static String getBase64(String str) {
		return java.util.Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
	}

	public static String getBase64(byte[] b) {
		return java.util.Base64.getEncoder().encodeToString(b);
	}

	// 解密
	public static byte[] base64ToByte(String s) {
		return java.util.Base64.getDecoder().decode(s);
	}

	// 解密
	public static String getFromBase64(String s) {
		return new String(java.util.Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
	}
}