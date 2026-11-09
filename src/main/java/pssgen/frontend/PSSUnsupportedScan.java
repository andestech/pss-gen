package pssgen.frontend;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import pssgen.parser.PSSParser;

/**
 * Finds, in parse trees, the constructs that PSSGenVisitor rejects as soon as it
 * visits them, whatever the rest of the input is: a rule it never builds a model
 * for, recognized by the rule alone. The visitor visits every such node (it skips
 * no subtree that can hold one), so an input with a finding never builds a model.
 * The scan does not descend into a finding, as the visitor stops there.
 *
 * Constructs whose rejection depends on where they appear (types and expressions
 * in subtrees the visitor does not visit, exec blocks of structs, ...) are left to
 * the visitor.
 */
public final class PSSUnsupportedScan {

	/** one rejected construct: where it starts and what the visitor reports for it */
	public static final class Finding {
		public final String file;
		public final int line;
		public final int col;
		/** the method of PSSGenVisitor that rejects it, as its Fatal message names it */
		public final String method;
		/** the visitor's message, without the location prefix */
		public final String text;

		Finding(String file, int line, int col, String method, String text) {
			this.file = file;
			this.line = line;
			this.col = col;
			this.method = method;
			this.text = text;
		}

		@Override
		public String toString() {
			return file + ":" + line + ":" + col + ": PSSGenVisitor." + method + "(): " + text;
		}
	}

	private final List<Finding> m_findings = new ArrayList<Finding>();

	private PSSUnsupportedScan() {
	}

	/**
	 * Returns the findings of the trees, in the order PSSGenVisitor would meet them:
	 * tree by tree, each in document order.
	 */
	public static List<Finding> scan(List<ParseTree> trees) {
		PSSUnsupportedScan s = new PSSUnsupportedScan();
		for (ParseTree t : trees) {
			s.walk(t);
		}
		return s.m_findings;
	}

	private void walk(ParseTree t) {
		if (t instanceof ParserRuleContext) {
			ParserRuleContext ctx = (ParserRuleContext) t;
			String method = rejectingMethod(ctx);
			if (method != null) {
				Token start = ctx.getStart();
				m_findings.add(new Finding(start.getTokenSource().getSourceName(), start.getLine(),
					start.getCharPositionInLine(), method, "Syntax is not yet supported: '" + ctx.getText() + "'"));
				return;
			}
		}
		for (int i = 0; i < t.getChildCount(); i++) {
			walk(t.getChild(i));
		}
	}

	/**
	 * The PSSGenVisitor method that rejects ctx on entry with "Syntax is not yet
	 * supported", or null. Each predicate is the visitor method's own test.
	 */
	static String rejectingMethod(ParserRuleContext ctx) {
		if (ctx instanceof PSSParser.Extend_stmtContext)
			return "visitExtend_stmt";
		if (ctx instanceof PSSParser.Override_declarationContext)
			return "visitOverride_declaration";
		if (ctx instanceof PSSParser.Component_pool_declarationContext)
			return "visitComponent_pool_declaration";
		if (ctx instanceof PSSParser.Covergroup_declarationContext)
			return "visitCovergroup_declaration";
		if (ctx instanceof PSSParser.Typedef_declarationContext)
			return "visitTypedef_declaration";
		if (ctx instanceof PSSParser.Component_super_specContext)
			return "visitComponent_super_spec";
		if (ctx instanceof PSSParser.Action_super_specContext)
			return "visitAction_super_spec";
		if (ctx instanceof PSSParser.Struct_super_specContext)
			return "visitStruct_super_spec";
		if (ctx instanceof PSSParser.Abstract_action_declarationContext)
			return "visitAbstract_action_declaration";
		if (ctx instanceof PSSParser.Exec_super_stmtContext)
			return "visitExec_super_stmt";
		if (ctx instanceof PSSParser.Symbol_declarationContext)
			return "visitSymbol_declaration";
		if (ctx instanceof PSSParser.Constraint_body_itemContext) {
			PSSParser.Constraint_body_itemContext c = (PSSParser.Constraint_body_itemContext) ctx;
			// 'default' 'disable' hierarchical_id ';'
			if (c.expression_constraint_item() == null && c.foreach_constraint_item() == null
				&& c.forall_constraint_item() == null && c.if_constraint_item() == null
				&& c.implication_constraint_item() == null && c.unique_constraint_item() == null
				&& c.constant_expression() == null && c.hierarchical_id() != null)
				return "visitConstraint_body_item";
			return null;
		}
		if (ctx instanceof PSSParser.Forall_constraint_itemContext)
			return "visitForall_constraint_item";
		if (ctx instanceof PSSParser.Implication_constraint_itemContext)
			return "visitImplication_constraint_item";
		if (ctx instanceof PSSParser.Activity_parallel_stmtContext)
			return "visitActivity_parallel_stmt";
		if (ctx instanceof PSSParser.Activity_stmtContext) {
			PSSParser.Activity_stmtContext a = (PSSParser.Activity_stmtContext) ctx;
			if (a.labeled_activity_stmt() == null && a.activity_bind_stmt() == null)
				return "visitActivity_stmt";
			return null;
		}
		if (ctx instanceof PSSParser.Symbol_callContext)
			return "visitSymbol_call";
		if (ctx instanceof PSSParser.Activity_replicate_stmtContext)
			return "visitActivity_replicate_stmt";
		if (ctx instanceof PSSParser.Activity_super_stmtContext)
			return "visitActivity_super_stmt";
		return null;
	}
}
