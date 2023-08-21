import java.util.*;

public class PSSMessage {
	private static boolean b_PrintInfoEn = false;	// Control of Info function
	private static boolean b_PrintDebugEn = false;	// Control of Info function

	public static void SetPrintInfoEn (boolean val) {
		b_PrintInfoEn = val;
	}

	public static void SetPrintDebugEn (boolean val) {
		b_PrintDebugEn = val;
	}

	public static boolean isPrintDebugEn () {
		return b_PrintDebugEn;
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
		if (true == b_PrintInfoEn) System.out.println("PSS-INFO: " + msg);
	}
	public static void Error(String ID, String msg){
		System.out.println("PSS-ERROR: [" + ID + "] " + msg);
		System.exit(1);
	}
	public static void Warning(String msg){
		System.out.println("PSS-WARNING: " + msg);
	}
	public static void Debug(String msg){
		if (true == b_PrintDebugEn) System.out.println("PSS-DEBUG: " + msg);
	}
}
