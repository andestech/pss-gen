package pssgen.util;

import java.io.PrintStream;

/**
 * The PSS-INFO, PSS-WARNING and PSS-DEBUG messages of one run: their switches, the
 * stream they go to and the start time their elapsed-time prefix counts from.
 */
public final class PSSLog {
	private final PrintStream m_out;
	private long m_startTime;
	private boolean m_printInfoEn = false;	// Control of Info function
	private boolean m_printDebugEn = false;	// Control of Debug function
	private boolean m_printRandEn = false;	// Control of print random sequence

	public PSSLog(PrintStream out) {
		m_out = out;
	}

	/** Starts the elapsed time of the message prefixes, PSS-ERROR reports included. */
	public void start() {
		m_startTime = System.currentTimeMillis();
	}
	public long startTime() {
		return m_startTime;
	}
	public PrintStream out() {
		return m_out;
	}

	public void SetPrintInfoEn (boolean val) {
		m_printInfoEn = val;
	}
	public void SetPrintDebugEn (boolean val) {
		m_printDebugEn = val;
	}
	public void SetPrintRandEn (boolean val) {
		m_printRandEn = val;
	}

	public boolean isPrintDebugEn () {
		return m_printDebugEn;
	}
	public boolean isPrintRandEn () {
		return m_printRandEn;
	}

	public void Info(String msg){
		double elapsedSeconds = (System.currentTimeMillis() - m_startTime) / 1000.0;
		if (true == m_printInfoEn) m_out.printf("[%.3fs] PSS-INFO: %s\n", elapsedSeconds, msg);
	}
	public void Warning(String msg){
		double elapsedSeconds = (System.currentTimeMillis() - m_startTime) / 1000.0;
		m_out.printf("[%.3fs] PSS-WARNING: %s\n", elapsedSeconds, msg);
	}
	public void Debug(String msg){
		double elapsedSeconds = (System.currentTimeMillis() - m_startTime) / 1000.0;
		if (true == m_printDebugEn) m_out.printf("[%.3fs] PSS-DEBUG: %s\n", elapsedSeconds, msg);
	}
}
