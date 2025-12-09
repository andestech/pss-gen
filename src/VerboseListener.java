import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;

public class VerboseListener extends BaseErrorListener {
	static String filename;

	public VerboseListener() {
		this.filename = "<RefPath>";
	}

	public VerboseListener(String filename) {
		this.filename = filename;
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
		PSSMessage.Error("SYNTAX", this.filename + ':' + line + ':' + charPositionInLine + " : " + msg);

		System.exit(1);
	}
}
