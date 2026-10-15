import java.util.*;
import java.io.PrintWriter;  

public class PSSTest {
	static PrintWriter m_writer;

	public static void open(String output_file) throws Exception {
		PSSMessage.Info("open '" + output_file + "' to write");
		m_writer = new PrintWriter(output_file, "UTF-8");
	}
	public static void println(String str) {
		m_writer.println(str);
	}
	public static void close() {
		m_writer.close();
	}
}
