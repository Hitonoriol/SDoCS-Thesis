package hitonoriol.stressstrain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

	public static void out(String str) {
		System.out.println(str);
	}

	public static void out(String str, Object... args) {
		System.out.println(String.format(str, args));
	}

	public static String timestamp() {
		return dateFormat.format(new Date());
	}

	private final static String extPattern = "(?<!^)[.][^.]*$";

	public static String removeFileExtension(String filename) {
		if (filename == null || filename.isEmpty())
			return filename;
		return filename.replaceAll(extPattern, "");
	}
}
