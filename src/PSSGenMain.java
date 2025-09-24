import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;


public class PSSGenMain {
	public static void main(String[] args) throws Exception {
		PSSGenOpt opt = new PSSGenOpt(args);

		ArrayList<ParseTree> tree_list = parseFiles(opt.input_flist());
		PSSModel             model     = createModel(tree_list);
		PSSActionInst        inst      = traverseRootAction(model, opt.root_action());

		realizeTest(inst, opt.output_file());
	}

	public static ArrayList<ParseTree> parseFiles(ArrayList<String> flist) throws Exception {
		PSSMessage.Info("parseFiles");
		ArrayList<ParseTree> tree_list = new ArrayList<ParseTree>();

		for (int i = 0; i < flist.size(); i++) {
			String file = flist.get(i);
			PSSMessage.Info("parse file '" + file + "'");

			InputStream is    = new FileInputStream(file);
			CharStream  input = CharStreams.fromStream(is);

			PSSLexer          lexer  = new PSSLexer          (input );
			CommonTokenStream tokens = new CommonTokenStream (lexer );
			PSSParser         parser = new PSSParser         (tokens);

			parser.removeErrorListeners();
			parser.addErrorListener(new VerboseListener(file));

			ParseTree tree = parser.model();
			tree_list.add(tree);
		}

		return tree_list;
	}

	public static PSSModel createModel(ArrayList<ParseTree> tree_list) {
		PSSMessage.Info("createModel");
		PSSModel      model = new PSSModel("");
		PSSGenVisitor eval  = new PSSGenVisitor();

		PSSMessage.Debug("tree_list.size = " + tree_list.size());
		for (int i = 0; i < tree_list.size(); i++) {
			eval.root = model;
			ParseTree tree_cur = tree_list.get(i);
			eval.visit(tree_list.get(i));
		}

		//model.dump("");
		//System.exit(0);

		return model;
	}

	public static PSSActionInst traverseRootAction(PSSModel model, String root_action) {
		PSSMessage.Info("traverseRootAction");
		PSSActionModel action_model = (PSSActionModel) model.findObject(root_action);
		if (action_model == null) {
			PSSMessage.Error("MAIN-0001", "Cannot find root action '" + root_action + "'");
		}

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
}
