package pssgen.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/** The output file of one run. */
public final class PSSTest {
	private final PSSLog m_log;
	private FileOutputStream m_stream;
	private PrintWriter m_writer;

	public PSSTest(PSSLog log) {
		m_log = log;
	}

	public void open(String output_file) throws Exception {
		m_log.Info("open '" + output_file + "' to write");
		try {
			// The writer stack of new PrintWriter(output_file, "UTF-8"), with the file
			// stream kept for abandon().
			m_stream = new FileOutputStream(output_file);
			m_writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(m_stream, StandardCharsets.UTF_8)));
		} catch (IOException e) {
			PSSMessage.Error("OUTPUT", "cannot write output file '" + output_file + "'");
		}
	}
	public void println(String str) {
		m_writer.println(str);
	}
	public void close() {
		m_writer.close();
	}

	/**
	 * Closes the file of a run that failed without writing what the writer still
	 * buffers, so the file is left as the command line leaves it when it exits.
	 */
	public void abandon() {
		if (m_stream == null) {
			return;
		}
		try {
			m_stream.close();
		} catch (IOException e) {
			// the run has already failed; its report is what matters
		}
	}
}
