package pssgen.cli;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import pssgen.frontend.PSSGenVisitor;
import pssgen.frontend.PSSUnsupportedScan;
import pssgen.frontend.VerboseListener;
import pssgen.inst.PSSActionInst;
import pssgen.inst.PSSComponentInst;
import pssgen.model.PSSActionModel;
import pssgen.model.PSSComponentModel;
import pssgen.model.PSSModel;
import pssgen.parser.PSSLexer;
import pssgen.parser.PSSParser;
import pssgen.util.PSSException;
import pssgen.util.PSSMessage;
import pssgen.util.PSSRun;

public class PSSGenMain {
	public static void main(String[] args) throws Exception {
		PSSRun run = new PSSRun(System.out);
		try {
			PSSGenOpt opt = new PSSGenOpt(args, run);

			ArrayList<ParseTree> tree_list = parseFiles(run, opt.input_flist());
			PSSModel             model     = createModel(run, tree_list);
			PSSActionInst        inst      = traverseRootAction(run, model, opt.root_action());

			realizeTest(run, inst, opt.output_file());
		} catch (PSSException e) { exit(run, e); }
	}

	/**
	 * Runs one generation in this JVM with the command-line arguments args: messages
	 * and reports go to out, the stack trace of a wrapped exception to err. Returns
	 * the exit status the command line would have; never ends the JVM. Exceptions
	 * that are not PSSException propagate, as they end the command line. main does
	 * not call this method, so that the stack traces of those exceptions stay as they
	 * are.
	 */
	public static int run(String[] args, PrintStream out, PrintStream err) throws Exception {
		PSSRun run = new PSSRun(out);
		boolean done = false;
		try {
			PSSGenOpt opt = new PSSGenOpt(args, run);

			ArrayList<ParseTree> tree_list = parseFiles(run, opt.input_flist());
			PSSModel             model     = createModel(run, tree_list);
			PSSActionInst        inst      = traverseRootAction(run, model, opt.root_action());

			realizeTest(run, inst, opt.output_file());
			done = true;
			return 0;
		} catch (PSSException e) {
			return report(run, e, out, err);
		} finally {
			if (!done) run.test().abandon();
		}
	}

	public static ArrayList<ParseTree> parseFiles(PSSRun run, ArrayList<String> flist) throws Exception {
		ArrayList<ParseTree> tree_list = new ArrayList<ParseTree>();

		for (String file : flist) {
			CharStream input = null;
			ParseTree  tree  = null;
			try {
				run.log().Info("parse file '" + file + "'");
				input = CharStreams.fromFileName(file);
			} catch (IOException e) {
				PSSMessage.Error("INPUT", "cannot read input file '" + file + "'");
			}
			if (null == input) PSSMessage.Error("", "Fail to read '" + file + "'");

			PSSLexer          lexer  = new PSSLexer          (input );
			CommonTokenStream tokens = new CommonTokenStream (lexer );
			PSSParser         parser = new PSSParser         (tokens);

			// A character the lexer cannot match is dropped with a PSS-WARNING.
			lexer.removeErrorListeners();
			lexer.addErrorListener(VerboseListener.warning(file, run.log()));

			// Enhance performance by starting with SLL then failing to combined SLL/LL (Two-Stage Parsing)
			// See https://www.antlr.org/api/Java/org/antlr/v4/runtime/atn/ParserATNSimulator.html
			try {
				parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
				parser.removeErrorListeners();
				parser.setErrorHandler(new BailErrorStrategy());
				tree = parser.model();
			} catch (ParseCancellationException ex) {
				// Retry with LL mode
				tokens.seek(0); // rewind
				parser.reset();
				parser.removeErrorListeners();
				parser.addErrorListener(new VerboseListener(file, run.log()));
				// The default strategy reports every error to the listener; BailErrorStrategy
				// would end a token mismatch with an unreported ParseCancellationException.
				parser.setErrorHandler(new DefaultErrorStrategy());
				parser.getInterpreter().setPredictionMode(PredictionMode.LL);
				tree = parser.model();
			}
			// Parsing stops after the last complete declaration; what follows is ignored,
			// which existing patterns rely on (a stray closing brace, for example).
			Token rest = tokens.LT(1);
			if (rest.getType() != Token.EOF) {
				run.log().Warning("[SYNTAX] " + file + ":" + rest.getLine() + ":" + rest.getCharPositionInLine()
					+ " : text after the last declaration is ignored, from '" + rest.getText() + "'");
			}

			tree_list.add(tree);
		}

		return tree_list;
	}

	public static PSSModel createModel(PSSRun run, ArrayList<ParseTree> tree_list) {
		run.log().Info("createModel");
		PSSModel      model = new PSSModel("");
		PSSGenVisitor eval  = new PSSGenVisitor(run.log(), model);

		run.log().Debug("tree_list.size = " + tree_list.size());
		// With one such construct the visitor reports it as before; with several, all
		// of them are reported at once.
		List<PSSUnsupportedScan.Finding> unsupported = PSSUnsupportedScan.scan(tree_list);
		if (unsupported.size() > 1) {
			StringBuilder msg = new StringBuilder(unsupported.size() + " unsupported constructs:");
			for (PSSUnsupportedScan.Finding f : unsupported) {
				msg.append("\n").append(f);
			}
			PSSMessage.Error("UNSUPPORTED", msg.toString());
		}
		for (ParseTree tree_cur : tree_list) {
			eval.visit(tree_cur);
		}

		return model;
	}

	public static PSSActionInst traverseRootAction(PSSRun run, PSSModel model, String root_action) {
		run.log().Info("traverseRootAction");
		PSSModel root = model.findObject(root_action);
		if (root == null) PSSMessage.Error("MAIN-0001", "Cannot find root action '" + root_action + "'");
		if (!(root instanceof PSSActionModel)) PSSMessage.Error("MAIN-0002", "Root '" + root_action + "' is not an action");
		PSSActionModel action_model = (PSSActionModel) root;

		PSSComponentModel comp_model = action_model.getComp();
		PSSComponentInst  solution   = comp_model.declInst(true);
		solution.setRun(run);
		solution.init_down();
		solution.init_up();

		PSSActionInst inst = action_model.declInst(true);
		solution.addInst(inst);
		action_model.traverse(inst);

		return inst;
	}

	public static void realizeTest(PSSRun run, PSSActionInst inst, String output_file) throws Exception {
		run.log().Info("realizeTest");
		run.test().open(output_file);
		inst.execute();
		run.test().close();
	}

	/**
	 * Prints the cause's stack trace (err) or the report (out) and returns the
	 * exception's status.
	 */
	private static int report(PSSRun run, PSSException e, PrintStream out, PrintStream err) {
		e.setStartTime(run.log().startTime());
		if (e.getCause() != null) e.getCause().printStackTrace(err);
		if (e.getReport() != null) out.print(e.getReport());
		return e.getStatus();
	}

	/** Reports e as report does on stdout and stderr, then exits with its status. */
	private static void exit(PSSRun run, PSSException e) {
		System.exit(report(run, e, System.out, System.err));
	}
}
