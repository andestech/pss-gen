package pssgen.util;

import java.io.PrintStream;

/**
 * Everything one generation run owns: its options, its messages, its random source
 * and its output file. PSSGenMain creates one per run; the frontend holds it, an
 * instance reaches it through its root instance (PSSInst.run()), and values and
 * domains get the PSSRandom or PSSLog they need as an argument. Nothing of a run is
 * kept in a static field, so runs in one JVM do not see each other.
 */
public final class PSSRun {
	private final PSSOptions m_options;
	private final PSSLog m_log;
	private final PSSRandom m_random;
	private final PSSTest m_test;

	/** A run whose messages and reports go to out. */
	public PSSRun(PrintStream out) {
		m_options = new PSSOptions();
		m_log = new PSSLog(out);
		m_random = new PSSRandom(m_log);
		m_test = new PSSTest(m_log);
	}

	public PSSOptions options() {
		return m_options;
	}
	public PSSLog log() {
		return m_log;
	}
	public PSSRandom random() {
		return m_random;
	}
	public PSSTest test() {
		return m_test;
	}
}
