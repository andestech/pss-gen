package pssgen.cli;

import java.util.*;
import java.io.File;
import java.io.FileFilter;
import java.util.Scanner;
import java.lang.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import pssgen.util.PSSException;
import pssgen.util.PSSMessage;
import pssgen.util.PSSRandom;


public class PSSGenOpt {
	ArrayList<String> m_input_flist;
	String m_output_file;
	String m_root_action;
	ArrayList<String> m_lib_dir;
	long m_seed;


	public PSSGenOpt(String[] args) {
		m_input_flist = new ArrayList<String>();
		m_output_file = "";
		m_lib_dir = new ArrayList<String>();
		m_root_action = "pss_top::root_a";
		m_seed = 1234;

		if (args.length == 0) {
			help(false);
		}

		PSSMessage.Initial();

		int i=0;
		while (i < args.length) {
			String arg = args[i];

			if (arg.equals("-o")) {
				m_output_file = value(args, i);
				i+=2;
			}
			else if (arg.equals("-f")) {
				String flist = value(args, i);
				readFlist(flist);
				i+=2;
			}
			else if (arg.equals("-y")) {
				m_lib_dir.add(value(args, i));
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
				int limit = intValue(arg, value(args, i));
				if (limit < 1) {
					PSSMessage.Error("OPT", "invalid value '" + args[i+1] + "' for option '" + arg + "': expected an integer greater than 0");
				}
				PSSRandom.setRetryLimit(limit);
				i+=2;
			}
			else if (arg.equals("-root")) {
				m_root_action = value(args, i);
				i+=2;
			}
			else if (arg.equals("-help")) {
				help(true);
			}
			else if (arg.equals("-seed")) {
				m_seed = longValue(arg, value(args, i));
				i+=2;
			}
			else if (arg.startsWith("-")) {
				PSSMessage.Error("OPT", "unknown option '" + arg + "'");
			}
			else {
				m_input_flist.add(arg);
				i++;
			}

		}

		PSSRandom.initRandom(m_seed);

		if (m_output_file.equals("")) {
			PSSMessage.Error("OPT", "output file is not specified");
		}

		for (int dir = 0; dir < m_lib_dir.size(); dir++)
		{
			addLibFile(m_lib_dir.get(dir));
		}
	}

	/** The value that follows the option args[i]; an error when the option is the last argument. */
	private static String value(String[] args, int i) {
		if (i + 1 >= args.length) {
			PSSMessage.Error("OPT", "option '" + args[i] + "' needs a value");
		}
		return args[i + 1];
	}

	private static long longValue(String opt, String text) {
		if (!isDecimal(text, "9223372036854775807", "9223372036854775808")) {
			PSSMessage.Error("OPT", "invalid value '" + text + "' for option '" + opt + "': expected an integer");
		}
		return Long.parseLong(text);
	}

	private static int intValue(String opt, String text) {
		if (!isDecimal(text, "2147483647", "2147483648")) {
			PSSMessage.Error("OPT", "invalid value '" + text + "' for option '" + opt + "': expected an integer");
		}
		return Integer.parseInt(text);
	}

	/**
	 * True when Long.parseLong / Integer.parseInt accept text: an optional sign and
	 * decimal digits whose magnitude is at most maxPositive, or maxNegative after a
	 * '-'.
	 */
	private static boolean isDecimal(String text, String maxPositive, String maxNegative) {
		boolean negative = text.startsWith("-");
		int start = (negative || text.startsWith("+")) ? 1 : 0;
		StringBuilder digits = new StringBuilder();
		for (int k = start; k < text.length(); k++) {
			int d = Character.digit(text.charAt(k), 10);
			if (d < 0) {
				return false;
			}
			if (digits.length() > 0 || d != 0) {
				digits.append((char) ('0' + d));
			}
		}
		if (start == text.length()) {
			return false;
		}
		String max = negative ? maxNegative : maxPositive;
		return digits.length() < max.length()
			|| (digits.length() == max.length() && digits.toString().compareTo(max) <= 0);
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
		if (files == null) {
			PSSMessage.Error("OPT", "cannot read library directory '" + lib_dir + "'");
		}
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

	private static final Map<String, String> envMap = System.getenv();

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
		if (!fh.isFile() || !fh.canRead()) {
			PSSMessage.Error("FLIST", "cannot read file list '" + flist + "'");
		}

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
			throw PSSException.wrap(e);
		}
	}

	/** Ends the run with the usage text: status 0 when -help asked for it, 1 when no argument was given. */
	void help(boolean requested) {
		String nl = System.lineSeparator();
		String usage =
			"Usage: PSSGenMain [options] file..." + nl
			+ "	-o <filename>		output file" + nl
			+ "	-f <filename>		file list" + nl
			+ "	-y <directory>		lib directory" + nl
			+ "	-info			enable display `PSS-INFO` message" + nl
			+ "	-debug			enable display `PSS-DEBUG` message" + nl
			+ "	-debug_random	enable display randomize sequence" + nl
			+ "	-set_random_limit <val>		change max. iterations of randomization (default: 2000)" + nl
			+ "	-root <root_action>" + nl
			+ "	-seed <seed>" + nl
			+ "	-help			print this text and exit" + nl;
		throw requested ? PSSException.stop(usage) : PSSException.create(null, "usage", usage);
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
