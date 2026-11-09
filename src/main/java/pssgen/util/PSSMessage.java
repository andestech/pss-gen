package pssgen.util;

/**
 * The messages that end a run. They keep no state: the report of a PSS-ERROR gets
 * its elapsed time from the run that catches it (PSSException.setStartTime). The
 * other messages belong to the run's PSSLog.
 */
public class PSSMessage {
	public static void Fatal(String msg) {
		String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();;
		String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();

		throw PSSException.create(null, msg,
			className + "." + methodName + "(), Ln" + lineNumber + ": " + msg + System.lineSeparator());
	}
	public static void Error(String ID, String msg){
		throw PSSException.error(ID, msg, System.currentTimeMillis());
	}
}
