import java.util.*;

public class PSSMessage {
	public static void Fatal(String msg) {
		String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();;
		String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
	        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();

		System.out.println(className + "." + methodName + "(), Ln" + lineNumber + ": " + msg);
		System.exit(1);
	}
	public static void Info(String msg){
		System.out.println("PSS-INFO: " + msg);
	}
	public static void Error(String ID, String msg){
		System.out.println("PSS-ERROR: [" + ID + "] " + msg);
		System.exit(1);
	}
	public static void Warning(String msg){
		System.out.println("PSS-WARNING: " + msg);
	}
	public static void Debug(String msg){
		System.out.println("PSS-DEBUG: " + msg);
	}
}
