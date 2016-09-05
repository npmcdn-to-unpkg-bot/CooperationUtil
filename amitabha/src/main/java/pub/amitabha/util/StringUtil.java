package pub.amitabha.util;

public class StringUtil {
	public static String implode(String[] strings) {
		StringBuilder sb = new StringBuilder();
		for (String str : strings) {
			sb.append(str);
		}
		return sb.toString();
	}

	public static String getHexString(String s) throws Exception {
		return org.apache.commons.codec.binary.Hex.encodeHexString(s.getBytes("UTF-8"));
	}

	public static String getStringFromHex(String s) throws Exception {
		byte[] b = org.apache.commons.codec.binary.Hex.decodeHex(s.toCharArray());
		return new String(b, "UTF-8");
	}

}
