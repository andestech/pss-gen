package pssgen.util;

import java.util.ArrayList;

/** The option values of one run, as PSSGenOpt reads them from the command line. */
public final class PSSOptions {
	private final ArrayList<String> m_input_flist = new ArrayList<String>();
	private final ArrayList<String> m_lib_dir = new ArrayList<String>();
	private String m_output_file = "";
	private String m_root_action = "pss_top::root_a";
	private long m_seed = 1234;
	private String m_solver = "retry";

	/** the input files, in command-line order; library files are appended last */
	public ArrayList<String> inputFiles() {
		return m_input_flist;
	}
	/** the -y directories, in command-line order */
	public ArrayList<String> libDirs() {
		return m_lib_dir;
	}
	public String outputFile() {
		return m_output_file;
	}
	public void setOutputFile(String file) {
		m_output_file = file;
	}
	public String rootAction() {
		return m_root_action;
	}
	public void setRootAction(String root) {
		m_root_action = root;
	}
	public long seed() {
		return m_seed;
	}
	public void setSeed(long seed) {
		m_seed = seed;
	}
	/** "retry" (default) or "propagate"; see PSSSolver.solve and solvePropagate */
	public String solver() {
		return m_solver;
	}
	public void setSolver(String solver) {
		m_solver = solver;
	}
}
