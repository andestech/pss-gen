package pssgen.util;

/**
 * Raised when PSSGen cannot continue, or stops on request (-help). PSSGenMain.main
 * is the only place that reports it and ends the process, with getStatus() as the
 * exit status, so the rest of the code can be driven in-process without the JVM
 * exiting.
 */
public class PSSException extends RuntimeException {

    /** error ID such as "MAIN-0001" or ""; null for Fatal, usage and wrapped exceptions */
    private final String m_id;

    /** exact text for stdout, line terminator included; null when only the cause is reported */
    private final String m_report;

    /** exit status of the command line: 1 for a failure, 0 for a requested stop */
    private final int m_status;

    private PSSException(String id, String msg, String report, int status) {
        super(msg);
        m_id = id;
        m_report = report;
        m_status = status;
    }

    private PSSException(Throwable cause) {
        super(cause);
        m_id = null;
        m_report = null;
        m_status = 1;
    }

    /** The exception to throw for a failure that is reported by printing report. */
    public static RuntimeException create(String id, String msg, String report) {
        return new PSSException(id, msg, report, 1);
    }

    /** The exception that ends a run that was asked to stop (-help) after printing report. */
    public static RuntimeException stop(String report) {
        return new PSSException(null, "stop", report, 0);
    }

    /** e itself when it is a PSSException; otherwise e wrapped, reported by its stack trace. */
    public static RuntimeException wrap(Exception e) {
        return e instanceof PSSException ? (PSSException) e : new PSSException(e);
    }

    public String getID() {
        return m_id;
    }

    public String getReport() {
        return m_report;
    }

    public int getStatus() {
        return m_status;
    }

}
