package hitonoriol.stressstrain;

public class Util {
	public static int toInt(boolean b) {
		return b ? 1 : 0;
	}
	
	public static void out(String str) {
		System.out.println(str);
	}
	
	public static void out(String str, Object...args) {
		System.out.println(String.format(str, args));
	}
}
