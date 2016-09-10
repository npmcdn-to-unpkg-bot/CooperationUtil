package fqr.util;

public class Test {

	public static void main(String[] args) throws Exception {
		String hey = "Hello, 山哥!";
		String key = Encryption.getRandomStr();
		System.out.println(key);
		//System.out.println(Base64.getBase64(hey));
		String eHey = Encryption.EncryptAES(hey, key);
		System.out.println(eHey);
		String dHey = Encryption.DecryptAES(eHey, "wTM1x8tKpTJsdrJd");
		System.out.println(dHey);
	}
}
