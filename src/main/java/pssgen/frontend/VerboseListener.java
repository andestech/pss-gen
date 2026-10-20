package pssgen.frontend;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;

import pssgen.util.PSSMessage;

public class VerboseListener extends BaseErrorListener {
	private final String filename;

	/** true: report a PSS-WARNING and let the recognizer continue */
	private final boolean warn;

	public VerboseListener() {
		this("<RefPath>");
	}

	public VerboseListener(String filename) {
		this(filename, false);
	}

	private VerboseListener(String filename, boolean warn) {
		this.filename = filename;
		this.warn = warn;
	}

	/** A listener that reports each error as a PSS-WARNING instead of a PSS-ERROR. */
	public static VerboseListener warning(String filename) {
		return new VerboseListener(filename, true);
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
			PSSMessage.Warning("[SYNTAX] " + where + "; the character is ignored");
		} else {
			PSSMessage.Error("SYNTAX", where);
		}
	}
}
