package pssgen.frontend;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;

import pssgen.util.PSSLog;
import pssgen.util.PSSMessage;

public class VerboseListener extends BaseErrorListener {
	private final String filename;

	/** true: report a PSS-WARNING and let the recognizer continue */
	private final boolean warn;

	/** where the PSS-WARNINGs go */
	private final PSSLog log;

	public VerboseListener(String filename, PSSLog log) {
		this(filename, false, log);
	}

	private VerboseListener(String filename, boolean warn, PSSLog log) {
		this.filename = filename;
		this.warn = warn;
		this.log = log;
	}

	/** A listener that reports each error as a PSS-WARNING instead of a PSS-ERROR. */
	public static VerboseListener warning(String filename, PSSLog log) {
		return new VerboseListener(filename, true, log);
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
		Object offendingSymbol,
		int line, int charPositionInLine,
		String msg,
		RecognitionException e)
	{
		//List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
		//Collections.reverse(stack);
		//System.err.println("rule stack: "+stack);
		String where = this.filename + ':' + line + ':' + charPositionInLine + " : " + msg;
		if (warn) {
			log.Warning("[SYNTAX] " + where + "; the character is ignored");
		} else {
			PSSMessage.Error("SYNTAX", where);
		}
	}
}
