package pssgen.cli;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.util.*;

import pssgen.frontend.PSSGenVisitor;
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
import pssgen.util.PSSTest;

public class PSSGenMain {
	public static void main(String[] args) throws Exception {
		try {
			PSSGenOpt opt = new PSSGenOpt(args);

			ArrayList<ParseTree> tree_list = parseFiles(opt.input_flist());
			PSSModel             model     = createModel(tree_list);
			PSSActionInst        inst      = traverseRootAction(model, opt.root_action());

			realizeTest(inst, opt.output_file());
		} catch (PSSException e) { exit(e); }
	}
	public static ArrayList<ParseTree> parseFiles(ArrayList<String> flist) throws Exception {
		ArrayList<ParseTree> tree_list = new ArrayList<ParseTree>();

		for (String file : flist) {
			CharStream input = null;
			ParseTree  tree  = null;
			try {
				PSSMessage.Info("parse file '" + file + "'");
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
			lexer.addErrorListener(VerboseListener.warning(file));

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
				parser.addErrorListener(new VerboseListener(file));
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
				PSSMessage.Warning("[SYNTAX] " + file + ":" + rest.getLine() + ":" + rest.getCharPositionInLine()
					+ " : text after the last declaration is ignored, from '" + rest.getText() + "'");
			}

			tree_list.add(tree);
		}

		return tree_list;
	}

	public static PSSModel createModel(ArrayList<ParseTree> tree_list) {
		PSSMessage.Info("createModel");
		PSSModel      model = new PSSModel("");
		PSSGenVisitor eval  = new PSSGenVisitor();
		eval.root = model;

		PSSMessage.Debug("tree_list.size = " + tree_list.size());
		for (ParseTree tree_cur : tree_list) {
			eval.visit(tree_cur);
		}

		return model;
	}

	public static PSSActionInst traverseRootAction(PSSModel model, String root_action) {
		PSSMessage.Info("traverseRootAction");
		PSSModel root = model.findObject(root_action);
		if (root == null) PSSMessage.Error("MAIN-0001", "Cannot find root action '" + root_action + "'");
		if (!(root instanceof PSSActionModel)) PSSMessage.Error("MAIN-0002", "Root '" + root_action + "' is not an action");
		PSSActionModel action_model = (PSSActionModel) root;

		PSSComponentModel comp_model = action_model.getComp();
		PSSComponentInst  solution   = comp_model.declInst(true);
		solution.init_down();
		solution.init_up();

		PSSActionInst inst = action_model.declInst(true);
		solution.addInst(inst);
		action_model.traverse(inst);

		return inst;
	}

	public static void realizeTest(PSSActionInst inst, String output_file) throws Exception {
		PSSMessage.Info("realizeTest");
		PSSTest.open(output_file);
		inst.execute();
		PSSTest.close();
	}

	/**
	 * Prints the cause's stack trace (stderr) or the report (stdout), then exits
	 * with the exception's status.
	 */
	private static void exit(PSSException e) {
		if (e.getCause() != null) e.getCause().printStackTrace();
		if (e.getReport() != null) System.out.print(e.getReport());
		System.exit(e.getStatus());
	}
}
