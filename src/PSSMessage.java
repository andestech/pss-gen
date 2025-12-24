import java.util.*;

public class PSSMessage {
	private static long startTime;
	private static boolean b_PrintInfoEn = false;	// Control of Info function
	private static boolean b_PrintDebugEn = false;	// Control of Debug function
	private static boolean b_PrintRandEn = false;	// Control of print random sequence

	public static void Initial () {
		startTime = System.currentTimeMillis();
	}
	public static void SetPrintInfoEn (boolean val) {
		b_PrintInfoEn = val;
	}
	public static void SetPrintDebugEn (boolean val) {
		b_PrintDebugEn = val;
	}
	public static void SetPrintRandEn (boolean val) {
		b_PrintRandEn = val;
	}

	public static boolean isPrintDebugEn () {
		return b_PrintDebugEn;
	}
	public static boolean isPrintRandEn () {
		return b_PrintRandEn;
	}

	public static void Fatal(String msg) {
		String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();;
		String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();

		System.out.println(className + "." + methodName + "(), Ln" + lineNumber + ": " + msg);
		System.exit(1);
	}
	public static void Info(String msg){
		double elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
		if (true == b_PrintInfoEn) System.out.printf("[%.3fs] PSS-INFO: %s\n", elapsedSeconds, msg);
	}
	public static void Error(String ID, String msg){
		double elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
		System.out.printf("[%.3fs] PSS-ERROR: [%s] %s\n", elapsedSeconds, ID, msg);
		System.exit(1);
	}
	public static void Warning(String msg){
		double elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
		System.out.printf("[%.3fs] PSS-WARNING: %s\n", elapsedSeconds, msg);
	}
	public static void Debug(String msg){
		double elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
		if (true == b_PrintDebugEn) System.out.printf("[%.3fs] PSS-DEBUG: %s\n", elapsedSeconds, msg);
	}
}
