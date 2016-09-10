package fqr.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.*;

/**
 * This class is not completed. Do NOT use.
 * @author Ems
 *
 */
public class ZipUtil {
	private static final String TEMP_FILE = ZipUtil.class + ".ZIP";
	public static String zipB64ToUtf8(String b64Str) throws Exception {

		FileOutputStream pos = new FileOutputStream(TEMP_FILE);
		byte[] zipBytes = Base64.base64ToByte(b64Str); 
		pos.write(zipBytes);
		pos.close();

		FileInputStream pis = new FileInputStream(TEMP_FILE);
		ZipInputStream zis = new ZipInputStream(pis);
		byte[] buf = new byte[10240];

		ZipEntry entry = zis.getNextEntry();
		System.out.println("Entry: " + entry.getName());
		int count = zis.read(buf);
		System.out.println("Count is: " + count);
		byte[] b = new byte[count];
		System.arraycopy(buf, 0, b, 0, count);

		zis.close();
		pis.close();

		return Base64.getFromBase64(b);
	}

	/**
	 * Restore an object from a base64 String.
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String utf8ToZipB64Str(String utf8Str) throws Exception {
		byte[] buf = utf8Str.getBytes("UTF-8");

//		PipedInputStream pis = new PipedInputStream();
//		PipedOutputStream pos = new PipedOutputStream(pis);

		FileOutputStream pos = new FileOutputStream(TEMP_FILE);
		ZipOutputStream zos = new ZipOutputStream(pos);
		ZipEntry zipEntry = new ZipEntry(TEMP_FILE);
		zos.putNextEntry(zipEntry);
		
		zos.write(buf);
		zos.close();
		pos.close();
		FileInputStream pis = new FileInputStream(TEMP_FILE);
		int count = pis.available();
		byte[] b = new byte[count];
		pis.read(b);

		pis.close();
		Files.deleteIfExists(Paths.get(TEMP_FILE));


		return Base64.getBase64(b);
	}
	public static void main(String[] args) throws Exception {
		String hey = "Hello, 山哥!";
		
		//System.out.println(Base64.getBase64(hey));
		String zipedHey = Base64.getBase64(hey);
		System.out.println(zipedHey);
		System.out.println(Base64.getFromBase64(zipedHey));
	}
}
