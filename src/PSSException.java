
/**
 * Raised when PSSGen cannot continue. PSSGenMain.main is the only place that
 * reports it and ends the process, so the rest of the code can be driven
 * in-process without the JVM exiting.
 *
 * A run that succeeds must not load this class: each class read from the jar
 * uses up identity hash codes, and PSSSolver.reduceDomain walks a HashMap keyed
 * by identity (the -info domain lines and the variable an OVERCONSTRAINT error
 * names follow its order). PSSGen code therefore gets it only from create() and
 * wrap(), typed RuntimeException, and never catches it by name or uses a value
 * typed PSSException where a wider type is expected (the verifier would load it).
 */
public class PSSException extends RuntimeException {

    /** error ID such as "MAIN-0001" or ""; null for Fatal, usage and wrapped exceptions */
    private final String m_id;

    /** exact text for stdout, line terminator included; null when only the cause is reported */
    private final String m_report;

    private PSSException(String id, String msg, String report) {
        super(msg);
        m_id = id;
        m_report = report;
    }

    private PSSException(Throwable cause) {
        super(cause);
        m_id = null;
        m_report = null;
    }

    /** The exception to throw for a failure that is reported by printing report. */
    public static RuntimeException create(String id, String msg, String report) {
        return new PSSException(id, msg, report);
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

}
