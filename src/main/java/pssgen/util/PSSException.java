package pssgen.util;

/**
 * Raised when PSSGen cannot continue, or stops on request (-help). Only PSSGenMain
 * reports it: main ends the process with getStatus() as the exit status, run returns
 * that status, so the rest of the code can be driven in-process without the JVM
 * exiting.
 */
public class PSSException extends RuntimeException {

    /** error ID such as "MAIN-0001" or ""; null for Fatal, usage and wrapped exceptions */
    private final String m_id;

    /** exact text for stdout, line terminator included; null when only the cause is reported or for a PSS-ERROR */
    private final String m_report;

    /** for a PSS-ERROR: when it was raised (System.currentTimeMillis()); -1 otherwise */
    private final long m_time;

    /** for a PSS-ERROR: the start time of the run, which its elapsed-time prefix counts from */
    private long m_start;

    /** exit status of the command line: 1 for a failure, 0 for a requested stop */
    private final int m_status;

    private PSSException(String id, String msg, String report, long time, int status) {
        super(msg);
        m_id = id;
        m_report = report;
        m_time = time;
        m_status = status;
    }

    private PSSException(Throwable cause) {
        super(cause);
        m_id = null;
        m_report = null;
        m_time = -1;
        m_status = 1;
    }

    /** The exception to throw for a failure that is reported by printing report. */
    public static RuntimeException create(String id, String msg, String report) {
        return new PSSException(id, msg, report, -1, 1);
    }

    /**
     * The exception for a PSS-ERROR raised at time; its report is
     * "[elapsed] PSS-ERROR: [id] msg" once the run has set its start time.
     */
    public static RuntimeException error(String id, String msg, long time) {
        return new PSSException(id, msg, null, time, 1);
    }

    /** The exception that ends a run that was asked to stop (-help) after printing report. */
    public static RuntimeException stop(String report) {
        return new PSSException(null, "stop", report, -1, 0);
    }

    /** e itself when it is a PSSException; otherwise e wrapped, reported by its stack trace. */
    public static RuntimeException wrap(Exception e) {
        return e instanceof PSSException ? (PSSException) e : new PSSException(e);
    }

    public String getID() {
        return m_id;
    }

    /** The start time of the run that raised this exception; a PSS-ERROR report needs it. */
    public void setStartTime(long start) {
        m_start = start;
    }

    public String getReport() {
        if (m_time >= 0) {
            double elapsedSeconds = (m_time - m_start) / 1000.0;
            return String.format("[%.3fs] PSS-ERROR: [%s] %s\n", elapsedSeconds, m_id, getMessage());
        }
        return m_report;
    }

    public int getStatus() {
        return m_status;
    }

}
