import java.util.*;
import java.io.File;
import java.io.FileFilter;
import java.util.Scanner;
import java.lang.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class PSSGenOpt {
	ArrayList<String> m_input_flist;
	String m_output_file;
	String m_root_action;
	ArrayList<String> m_lib_dir;
	int m_seed;


	public PSSGenOpt(String[] args) {
		m_input_flist = new ArrayList<String>();
		m_output_file = "";
		m_lib_dir = new ArrayList<String>();
		m_root_action = "pss_top::root_a";
		m_seed = 1234;

		if (args.length == 0) {
			help();
		}

		int i=0;
		while (i < args.length) {
			String arg = args[i];

			if (arg.equals("-o")) {
				m_output_file = args[i+1];
				i+=2;
			}
			else if (arg.equals("-f")) {
				String flist = args[i+1];
				readFlist(flist);
				i+=2;
			}
			else if (arg.equals("-y")) {
				m_lib_dir.add(args[i+1]);
				i+=2;
			}
			else if (arg.equals("-info")) {
				PSSMessage.SetPrintInfoEn(true);
				i++;
			}
			else if (arg.equals("-debug")) {
				PSSMessage.SetPrintDebugEn(true);
				i++;
			}
			else if (arg.equals("-debug_random")) {
				PSSMessage.SetPrintDebugEn(true);
				PSSMessage.SetPrintRandEn(true);
				i++;
			}
			else if (arg.equals("-set_random_limit")) {
				PSSRandom.setRetryLimit(Integer.valueOf(args[i+1]));
				i+=2;
			}
			else if (arg.equals("-root")) {
				m_root_action = args[i+1];
				i+=2;
			}
			else if (arg.equals("-help")) {
				help();
			}
			else if (arg.equals("-seed")) {
				m_seed = Integer.valueOf(args[i+1]);
				i+=2;
			}
			else {
				m_input_flist.add(arg);
				i++;
			}

		}

		PSSMessage.Initial();
		PSSRandom.initRandom(m_seed);

		if (m_output_file.equals("")) {
			PSSMessage.Error("OPT", "output file is not specified");
		}

		for (int dir = 0; dir < m_lib_dir.size(); dir++)
		{
			addLibFile(m_lib_dir.get(dir));
		}
	}

	private void addLibFile(String lib_dir) {
		File dir = new File(lib_dir);
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().matches(".*\\.pss");
			}
		};
		File[] files = dir.listFiles(filter);
		for (int i = 0; i < files.length; i++) {
			m_input_flist.add(files[i].getAbsolutePath());
		}
	}

	public void parseFlistLine (String text) {
		String[] tokens = text.split(" ");
		if (tokens.length >= 2) {
			String arg = tokens[0];
			if (arg.equals("-o")) {
				m_output_file = tokens[1];
			}
			else if (arg.equals("-y")) {
				m_lib_dir.add(tokens[1]);
			}
			else if (arg.equals("-root")) {
				m_root_action = tokens[1];
			}
			else {
				PSSMessage.Error("FLIST", "unknown option '" + text + "'");
			}
		}
		else {
			m_input_flist.add(tokens[0]);
		}
	}

	private static Map<String, String> envMap = System.getenv();

	private String substEnv(String text) {
		Pattern pattern = Pattern.compile(".*\\$([0-9a-zA-Z_]+).*");
		Matcher m = pattern.matcher(text);
		while (m.find()) {
			String envVar = m.group(1);
			String envValue = envMap.get(envVar);
		        if (envValue == null) {
				envValue = "";
			}
			else {
				envValue = envValue.replace("\\", "\\\\");
			}
			text = text.replaceAll("\\$" + envVar, envValue);
		}
		return text;
	}

	public void readFlist(String flist) {
	        File fh = new File(flist);

		try {
			Scanner scan = new Scanner(fh);
	        	while (scan.hasNextLine()) {
				String line = substEnv(scan.nextLine().trim());

				if (line.length() != 0) {
					if (line.charAt(0) != '#') {
						parseFlistLine(line);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();			
			System.exit(1);
		}
	}

	void help() {
		System.out.println("Usage: PSSGenMan [options] file...");
		System.out.println("	-o <filename>		output file");
		System.out.println("	-f <filename>		file list");
		System.out.println("	-y <directory>		lib directory");
		System.out.println("	-info			enable display `PSS-INFO` message");
		System.out.println("	-debug			enable display `PSS-DEBUG` message");
		System.out.println("	-debug_random	enable display randomize sequence");
		System.out.println("	-set_random_limit <val>		change max. iterations of randomization (default: 1000)");
		System.out.println("	-root <root_action>");
		System.out.println("	-seed <seed>");
		System.exit(1);
	}

	public ArrayList<String> input_flist() {
		return m_input_flist;
	}
	public String output_file() {
		return m_output_file;
	}
	public String root_action() {
		return m_root_action;
	}
}
