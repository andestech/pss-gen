package pssgen.cli;

import java.util.*;
import java.io.File;
import java.io.FileFilter;
import java.util.Scanner;
import java.lang.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import pssgen.util.PSSException;
import pssgen.util.PSSLog;
import pssgen.util.PSSMessage;
import pssgen.util.PSSOptions;
import pssgen.util.PSSRun;


/**
 * Reads the command line into the options of a run and sets up the run's messages
 * and random source as it goes, so an option takes effect from its position on
 * (-info before -set_random_limit prints the change of the limit).
 */
public class PSSGenOpt {
	private final PSSOptions m_opt;

	public PSSGenOpt(String[] args, PSSRun run) {
		m_opt = run.options();
		PSSLog log = run.log();

		if (args.length == 0) {
			help(false);
		}

		log.start();

		int i=0;
		while (i < args.length) {
			String arg = args[i];

			if (arg.equals("-o")) {
				m_opt.setOutputFile(value(args, i));
				i+=2;
			}
			else if (arg.equals("-f")) {
				String flist = value(args, i);
				readFlist(flist);
				i+=2;
			}
			else if (arg.equals("-y")) {
				m_opt.libDirs().add(value(args, i));
				i+=2;
			}
			else if (arg.equals("-info")) {
				log.SetPrintInfoEn(true);
				i++;
			}
			else if (arg.equals("-debug")) {
				log.SetPrintDebugEn(true);
				i++;
			}
			else if (arg.equals("-debug_random")) {
				log.SetPrintDebugEn(true);
				log.SetPrintRandEn(true);
				i++;
			}
			else if (arg.equals("-set_random_limit")) {
				int limit = intValue(arg, value(args, i));
				if (limit < 1) {
					PSSMessage.Error("OPT", "invalid value '" + args[i+1] + "' for option '" + arg + "': expected an integer greater than 0");
				}
				run.random().setRetryLimit(limit);
				i+=2;
			}
			else if (arg.equals("-root")) {
				m_opt.setRootAction(value(args, i));
				i+=2;
			}
			else if (arg.equals("-help")) {
				help(true);
			}
			else if (arg.equals("-seed")) {
				m_opt.setSeed(longValue(arg, value(args, i)));
				i+=2;
			}
			else if (arg.startsWith("-")) {
				PSSMessage.Error("OPT", "unknown option '" + arg + "'");
			}
			else {
				m_opt.inputFiles().add(arg);
				i++;
			}

		}

		run.random().initRandom(m_opt.seed());

		if (m_opt.outputFile().equals("")) {
			PSSMessage.Error("OPT", "output file is not specified");
		}

		for (int dir = 0; dir < m_opt.libDirs().size(); dir++)
		{
			addLibFile(m_opt.libDirs().get(dir));
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
			m_opt.inputFiles().add(files[i].getAbsolutePath());
		}
	}

	public void parseFlistLine (String text) {
		String[] tokens = text.split(" ");
		if (tokens.length >= 2) {
			String arg = tokens[0];
			if (arg.equals("-o")) {
				m_opt.setOutputFile(tokens[1]);
			}
			else if (arg.equals("-y")) {
				m_opt.libDirs().add(tokens[1]);
			}
			else if (arg.equals("-root")) {
				m_opt.setRootAction(tokens[1]);
			}
			else {
				PSSMessage.Error("FLIST", "unknown option '" + text + "'");
			}
		}
		else {
			m_opt.inputFiles().add(tokens[0]);
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
		return m_opt.inputFiles();
	}
	public String output_file() {
		return m_opt.outputFile();
	}
	public String root_action() {
		return m_opt.rootAction();
	}
}
