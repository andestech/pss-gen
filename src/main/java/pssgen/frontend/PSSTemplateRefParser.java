package pssgen.frontend;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

import pssgen.expr.PSSRefPathExpression;
import pssgen.expr.PSSRefPathParser;
import pssgen.inst.PSSScope;
import pssgen.model.PSSModel;
import pssgen.parser.PSSLexer;
import pssgen.parser.PSSParser;
import pssgen.util.PSSLog;
import pssgen.util.PSSMessage;

/**
 * Parses the {@code {{...}}} references of target templates, each time a template
 * is expanded.
 */
public final class PSSTemplateRefParser implements PSSRefPathParser {

	/** the parser; it keeps no state */
	public static final PSSTemplateRefParser INSTANCE = new PSSTemplateRefParser();

	private PSSTemplateRefParser() {
	}

	@Override
	public PSSRefPathExpression fromString(PSSModel root, String str, PSSLog log) {
		PSSRefPathExpression res = null;

		try {
			InputStream is = new ByteArrayInputStream(str.getBytes());
			CharStream input = CharStreams.fromStream(is);
			PSSLexer lexer = new PSSLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			PSSParser parser = new PSSParser(tokens);

			lexer.removeErrorListeners();
			lexer.addErrorListener(VerboseListener.warning("<RefPath>", log));
			parser.removeErrorListeners();
			parser.addErrorListener(new VerboseListener("<RefPath>", log));

			ParseTree ref_path = parser.ref_path();
			if (tokens.LA(1) != Token.EOF) {
				log.Warning("[SYNTAX] <RefPath> : text after the reference is ignored, from '"
					+ tokens.LT(1).getText() + "' in '" + str + "'");
			}
			PSSGenVisitor eval = new PSSGenVisitor(log, root, PSSScope.Context.TEMPLATE);
			res = (PSSRefPathExpression) eval.visit(ref_path);
		} catch (IOException e) {
			PSSMessage.Fatal("Failed parsing " + str);
		}

		return res;
	}
}
