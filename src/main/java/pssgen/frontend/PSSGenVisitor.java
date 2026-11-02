package pssgen.frontend;

import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import pssgen.activity.PSSActivity;
import pssgen.activity.PSSBindActivity;
import pssgen.activity.PSSForeachActivity;
import pssgen.activity.PSSIfElseActivity;
import pssgen.activity.PSSMatchActivity;
import pssgen.activity.PSSRepeatActivity;
import pssgen.activity.PSSScheduleActivity;
import pssgen.activity.PSSSelectActivity;
import pssgen.activity.PSSSelectBranchActivity;
import pssgen.activity.PSSSequenceActivity;
import pssgen.activity.PSSTraversalActivity;
import pssgen.expr.PSSAddExpression;
import pssgen.expr.PSSAggregateExpression;
import pssgen.expr.PSSBinaryExpression;
import pssgen.expr.PSSBitwiseAndExpression;
import pssgen.expr.PSSBitwiseNotExpression;
import pssgen.expr.PSSBitwiseOrExpression;
import pssgen.expr.PSSBitwiseXorExpression;
import pssgen.expr.PSSCastExpression;
import pssgen.expr.PSSDivExpression;
import pssgen.expr.PSSEqualExpression;
import pssgen.expr.PSSExpression;
import pssgen.expr.PSSFunctionCallExpression;
import pssgen.expr.PSSGreaterEqualExpression;
import pssgen.expr.PSSGreaterThanExpression;
import pssgen.expr.PSSInExpression;
import pssgen.expr.PSSLeftShiftExpression;
import pssgen.expr.PSSLessEqualExpression;
import pssgen.expr.PSSLessThanExpression;
import pssgen.expr.PSSLogicalAndExpression;
import pssgen.expr.PSSLogicalNotExpression;
import pssgen.expr.PSSLogicalOrExpression;
import pssgen.expr.PSSMapExpression;
import pssgen.expr.PSSMemberPathElemExpression;
import pssgen.expr.PSSModExpression;
import pssgen.expr.PSSMulExpression;
import pssgen.expr.PSSNotEqualExpression;
import pssgen.expr.PSSOpenRangeListExpression;
import pssgen.expr.PSSOpenRangeValueExpression;
import pssgen.expr.PSSPrimaryExpression;
import pssgen.expr.PSSRefPathExpression;
import pssgen.expr.PSSRightShiftExpression;
import pssgen.expr.PSSSubExpression;
import pssgen.expr.PSSUnaryMinusExpression;
import pssgen.inst.PSSScope;
import pssgen.model.PSSActionModel;
import pssgen.model.PSSArrayModel;
import pssgen.model.PSSAttrField;
import pssgen.model.PSSBoolModel;
import pssgen.model.PSSComponentDataDeclModel;
import pssgen.model.PSSComponentModel;
import pssgen.model.PSSDataDeclModel;
import pssgen.model.PSSDataInstModel;
import pssgen.model.PSSDataTypeModel;
import pssgen.model.PSSEnumModel;
import pssgen.model.PSSExecKind;
import pssgen.model.PSSFlowRefModel;
import pssgen.model.PSSFunctionModel;
import pssgen.model.PSSFunctionParameter;
import pssgen.model.PSSFunctionParameterDir;
import pssgen.model.PSSFunctionPrototype;
import pssgen.model.PSSFunctionReturnType;
import pssgen.model.PSSImportStmtModel;
import pssgen.model.PSSIntModel;
import pssgen.model.PSSListModel;
import pssgen.model.PSSMapModel;
import pssgen.model.PSSModel;
import pssgen.model.PSSObjectKind;
import pssgen.model.PSSPackageModel;
import pssgen.model.PSSPlatformQualifier;
import pssgen.model.PSSSetModel;
import pssgen.model.PSSStringModel;
import pssgen.model.PSSStructKind;
import pssgen.model.PSSStructModel;
import pssgen.model.PSSTypeCategory;
import pssgen.parser.PSSBaseVisitor;
import pssgen.parser.PSSParser;
import pssgen.proc.PSSAssignProcStmt;
import pssgen.proc.PSSBreakProcStmt;
import pssgen.proc.PSSContinueProcStmt;
import pssgen.proc.PSSDataDeclProcStmt;
import pssgen.proc.PSSDataInstProcStmt;
import pssgen.proc.PSSExecBlock;
import pssgen.proc.PSSForeachProcStmt;
import pssgen.proc.PSSIfElseProcStmt;
import pssgen.proc.PSSMatchProcStmt;
import pssgen.proc.PSSProcStmt;
import pssgen.proc.PSSRepeatProcStmt;
import pssgen.proc.PSSRepeatWhileProcStmt;
import pssgen.proc.PSSReturnProcStmt;
import pssgen.proc.PSSSequenceProcStmt;
import pssgen.proc.PSSTargetCode;
import pssgen.proc.PSSVoidFunctionCallProcStmt;
import pssgen.proc.PSSWhileProcStmt;
import pssgen.solver.PSSConstraint;
import pssgen.solver.PSSExpressionConstraint;
import pssgen.solver.PSSForeachConstraint;
import pssgen.solver.PSSIfConstraint;
import pssgen.solver.PSSUniqueConstraint;
import pssgen.util.PSSLog;
import pssgen.util.PSSMessage;
import pssgen.value.PSSBoolVal;
import pssgen.value.PSSNumber;
import pssgen.value.PSSStringVal;
import pssgen.value.PSSRangeVal;
import pssgen.value.PSSVal;

/**
 * Builds the model of parse trees. Each visit method returns what it builds: an
 * expression, a type ({@link TypeSpec}), a statement, an activity, constraints
 * ({@link ConstraintSet}), a function prototype or parameter; declarations are
 * added to {@link #root}, the model they are declared in.
 */
public class PSSGenVisitor extends PSSBaseVisitor<Object> {

	/** A data type and the domain of an integer type's {@code in [...]}. */
	static final class TypeSpec {
		final PSSModel model;
		/** the closed ranges and single values of the domain; null when it has none */
		final PSSOpenRangeListExpression ranges;
		/**
		 * the open-ended items of the domain ({@code a..}, {@code ..b}), from the last
		 * to the first; they constrain the field after the ranges
		 */
		final List<PSSExpression> open_ended;
		/** the element type of an array or list type, whose domain each element has; else null */
		ParserRuleContext element;

		TypeSpec(PSSModel model) {
			this(model, null, Collections.<PSSExpression>emptyList());
		}

		boolean hasDomain() {
			return ranges != null || !open_ended.isEmpty();
		}

		TypeSpec(PSSModel model, PSSOpenRangeListExpression ranges, List<PSSExpression> open_ended) {
			this.model = model;
			this.ranges = ranges;
			this.open_ended = open_ended;
		}
	}

	/** The constraints of a constraint set, and the default constraints written in it. */
	static final class ConstraintSet {
		final List<PSSConstraint> constraints = new ArrayList<PSSConstraint>();
		final List<PSSConstraint> defaults = new ArrayList<PSSConstraint>();

		void addAll(ConstraintSet other) {
			constraints.addAll(other.constraints);
			defaults.addAll(other.defaults);
		}
	}

	/** the model that declarations are added to: the package, component, action, struct or enum being built */
	private PSSModel root;
	// The type models of every bool and every string declaration of this visitor's
	// model: declaring a field adds its type model to the declaring scope, so the
	// shared value types (PSSBoolModel.getInstance(), ...) are not used here.
	private final PSSBoolModel m_bool_type = new PSSBoolModel();
	private final PSSStringModel m_string_type = new PSSStringModel();
	/** the run's messages */
	private final PSSLog m_log;
	/** where the names being built are written; set around each construct that decides it */
	private PSSScope.Context m_context;
	/** The field names declared so far in each type, for the uniqueness check. */
	private final Map<PSSModel, Set<String>> m_field_ids = new IdentityHashMap<PSSModel, Set<String>>();

	/**
	 * Constructs a visitor that adds declarations to root.
	 *
	 * @param log  the run's messages
	 * @param root the model that top-level declarations are added to
	 */
	public PSSGenVisitor(PSSLog log, PSSModel root) {
		this(log, root, PSSScope.Context.DECLARATION);
	}

	/**
	 * Constructs a visitor whose names, outside the constructs that decide their
	 * context, are written in a given context.
	 *
	 * @param log     the run's messages
	 * @param root    the model that top-level declarations are added to
	 * @param context the context of names outside those constructs
	 */
	public PSSGenVisitor(PSSLog log, PSSModel root, PSSScope.Context context) {
		m_log = log;
		this.root = root;
		m_context = context;
	}

	@Override
	public Object visitPackage_declaration(PSSParser.Package_declarationContext ctx) {
		// PSS 2.0 Section 21.1.1.1 a): several package statements of one name declare the
		// members of one package; 'package outer::inner' declares inner inside outer.
		PSSModel outer = root;
		PSSPackageModel node = null;
		for (PSSParser.Package_identifierContext elem : ctx.package_id_path().package_identifier()) {
			String id = elem.getText();
			node = null;
			for (PSSModel c : root.getChildren()) {
				if (c instanceof PSSPackageModel && c.getId().equals(id))
					node = (PSSPackageModel) c;
			}
			if (node == null) {
				node = new PSSPackageModel(id);
				root.addChild(node);
			}
			root = node;
		}

		for (int i = 0; i < ctx.package_body_item().size(); i++) {
			visit(ctx.package_body_item(i));
		}

		root = outer;

		return node;
	}


	@Override
	public Object visitImport_stmt(PSSParser.Import_stmtContext ctx) {
		String type_id = ctx.package_import_pattern().type_identifier().getText();

		PSSParser.Package_import_qualifierContext q = ctx.package_import_pattern().package_import_qualifier();
		PSSImportStmtModel node = new PSSImportStmtModel(type_id, q != null && q.package_import_wildcard() != null);
		root.addChild(node);

		return node;
	}

	@Override
	public Object visitExtend_stmt(PSSParser.Extend_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	@Override
	public Object visitComponent_declaration(PSSParser.Component_declarationContext ctx) {
		if (ctx.component_super_spec() != null)
			visit(ctx.component_super_spec());
		String id = ctx.component_identifier().getText();

		PSSComponentModel node = new PSSComponentModel(id);

		root.addChild(node);
		root = node;

		for (int i = 0; i < ctx.component_body_item().size(); i++) {
			visit(ctx.component_body_item(i));
		}

		root = node.getParent();

		return node;
	}

	@Override
	public Object visitOverride_declaration(PSSParser.Override_declarationContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	@Override
	public Object visitComponent_pool_declaration(PSSParser.Component_pool_declarationContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	};

	@Override
	public Object visitComponent_data_declaration(PSSParser.Component_data_declarationContext ctx) {

		PSSDataDeclModel decl = (PSSDataDeclModel) visit(ctx.data_declaration());
		// PSS 2.0 Section 21.3: a static const is a static member of the component type;
		// it also stays readable through an instance.
		for (int i = 0; i < ctx.getChildCount(); i++) {
			if (ctx.getChild(i).getText().equals("static"))
				decl.declStatic(root);
		}

		PSSComponentDataDeclModel data_decl = new PSSComponentDataDeclModel(decl);
		root.addComponentDataDecl(data_decl);

		return data_decl;
	}

	@Override
	public Object visitEnum_declaration(PSSParser.Enum_declarationContext ctx) {
		String enum_id = ctx.enum_identifier().getText();
		PSSEnumModel node = new PSSEnumModel(enum_id);
		root.addChild(node);
		root = node;

		for (int i = 0; i < ctx.enum_item().size(); i++) {
			visit(ctx.enum_item(i));
		}

		root = node.getParent();
		return node;
	}

	@Override
	public Object visitEnum_item(PSSParser.Enum_itemContext ctx) {
		String id = ctx.identifier().getText();

		PSSEnumModel enum_model = (PSSEnumModel) root;

		if (ctx.constant_expression() != null) {
			PSSExpression expression = (PSSExpression) visit(ctx.constant_expression());
			PSSVal val = expression.eval(null);
			enum_model.addEnumItem(id, val);
		} else {
			enum_model.addEnumItem(id);
		}

		return null;
	}

	@Override
	public Object visitFunction_return_type(PSSParser.Function_return_typeContext ctx) {
		if (ctx.data_type() == null)
			return PSSFunctionReturnType.VOID;
		TypeSpec type = (TypeSpec) visit(ctx.data_type());
		return PSSFunctionReturnType.valueOf(type.model);
	}

	@Override
	public Object visitObject_kind(PSSParser.Object_kindContext ctx) {
		return PSSObjectKind.valueOf(ctx.getText());
	}

	@Override
	public Object visitStruct_kind(PSSParser.Struct_kindContext ctx) {
		return PSSStructKind.valueOf(ctx.getText());
	}

	@Override
	public Object visitType_category(PSSParser.Type_categoryContext ctx) {
		return PSSTypeCategory.valueOf(ctx.getText());
	}

	@Override
	public Object visitFunction_parameter(PSSParser.Function_parameterContext ctx) {
		PSSFunctionParameter param = null;
		String id = ctx.identifier().getText();
		if (ctx.type != null) {
			param = PSSFunctionParameter.newTypeParameter(id);
		} else if (ctx.ref != null) {
			PSSTypeCategory category = (PSSTypeCategory) visit(ctx.type_category());
			param = PSSFunctionParameter.newRefParameter(category, id);
		} else if (ctx.struct != null) {
			param = PSSFunctionParameter.newStructParameter(id);
		} else {
			PSSFunctionParameterDir dir = null;
			if (ctx.function_parameter_dir() != null)
				dir = PSSFunctionParameterDir.valueOf(ctx.function_parameter_dir().getText());
			PSSModel data_type = ((TypeSpec) visit(ctx.data_type())).model;
			PSSExpression exp = null;
			if (ctx.constant_expression() != null) {
				exp = (PSSExpression) visit(ctx.constant_expression());
			}
			param = PSSFunctionParameter.newPlainDataTypeParameter(dir, data_type, id, exp);
		}
		return param;
	}

	@Override
	public Object visitVarargs_parameter(PSSParser.Varargs_parameterContext ctx) {
		PSSFunctionParameter param = null;
		String id = ctx.identifier().getText();
		if (ctx.type != null) {
			param = PSSFunctionParameter.newVariadicTypeParameter(id);
		} else if (ctx.ref != null) {
			PSSTypeCategory category = (PSSTypeCategory) visit(ctx.type_category());
			param = PSSFunctionParameter.newVariadicRefParameter(category, id);
		} else if (ctx.struct != null) {
			param = PSSFunctionParameter.newVariadicStructParameter(id);
		} else {
			PSSModel data_type = ((TypeSpec) visit(ctx.data_type())).model;
			param = PSSFunctionParameter.newVariadicDataTypeParameter(data_type, id);
		}
		return param;
	}

	/** Returns the parameters; with a varargs parameter, only that one. */
	@Override
	public Object visitFunction_parameter_list_prototype(PSSParser.Function_parameter_list_prototypeContext ctx) {
		List<PSSFunctionParameter> params = new ArrayList<PSSFunctionParameter>();
		if (ctx.varargs_parameter() == null) {
			for (PSSParser.Function_parameterContext c : ctx.function_parameter())
				params.add((PSSFunctionParameter) visit(c));
		} else {
			params.add((PSSFunctionParameter) visit(ctx.varargs_parameter()));
		}

		return params;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object visitFunction_prototype(PSSParser.Function_prototypeContext ctx) {
		PSSFunctionReturnType rt = (PSSFunctionReturnType) visit(ctx.function_return_type());
		String id = ctx.function_identifier().getText();
		List<PSSFunctionParameter> params = (List<PSSFunctionParameter>) visit(ctx.function_parameter_list_prototype());
		return new PSSFunctionPrototype(rt, id, params);
	}

	@Override
	public Object visitFunction_decl(PSSParser.Function_declContext ctx) {
		boolean pure = ctx.pure != null;
		PSSFunctionPrototype prototype = (PSSFunctionPrototype) visit(ctx.function_prototype());
		PSSFunctionModel f = new PSSFunctionModel(pure, prototype);

		// Check 22.2 (shadowed function declaration) and 22.2.3 (c)
		PSSModel m = root.findDeclaration(prototype.getID());
		if (m instanceof PSSFunctionModel) {
			PSSFunctionModel fm = (PSSFunctionModel) m;

			if (root == fm.getParent())
				PSSMessage.Error("", "Redeclaring function " + prototype.getID());

			fm.redeclare(f);
		}

		root.addChild(f);
		return f;
	}

	@Override
	public Object visitProcedural_function(PSSParser.Procedural_functionContext ctx) {
		PSSPlatformQualifier qualifier = null;
		if (ctx.platform_qualifier() != null)
			qualifier = PSSPlatformQualifier.valueOf(ctx.platform_qualifier().getText());
		boolean pure = ctx.pure != null;
		PSSFunctionPrototype prototype = (PSSFunctionPrototype) visit(ctx.function_prototype());
		List<PSSProcStmt> stmts = new ArrayList<PSSProcStmt>();
		PSSScope.Context outer = m_context;
		m_context = PSSScope.Context.FUNCTION_BODY;
		try {
			for (PSSParser.Procedural_stmtContext c : ctx.procedural_stmt()) {
				stmts.add((PSSProcStmt) visit(c));
			}
		} finally {
			m_context = outer;
		}
		PSSFunctionModel f = new PSSFunctionModel(qualifier, pure, prototype, stmts);

		// Find existing declaration
		PSSFunctionModel fm = null;
		PSSModel m = root.findDeclaration(prototype.getID());
		if (m instanceof PSSFunctionModel) {
			// Redeclaration of a function
			fm = (PSSFunctionModel) m;
		}

		if (fm == null) {
			// This is the first definition
			root.addChild(f);
		} else {
			if (root == fm.getParent()) {
				// Both functions are in the same model
				if (fm.getStatements() == null) {
					fm.define(stmts);
				} else {
					PSSMessage.Error("", "Function " + prototype.getID() + " is already defined.");
				}
			} else {
				// Shadowing function fm
				fm.redeclare(f);
				root.addChild(f);
			}
		}

		return f;
	}

	@Override
	public Object visitCovergroup_declaration(PSSParser.Covergroup_declarationContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	@Override
	public Object visitTypedef_declaration(PSSParser.Typedef_declarationContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	@Override
	public Object visitAction_declaration(PSSParser.Action_declarationContext ctx) {
		if (ctx.action_super_spec() != null)
			visit(ctx.action_super_spec());
		String id = ctx.action_identifier().getText();

		PSSActionModel node = new PSSActionModel(id);
		root.addChild(node);
		root = node;

		for (int i = 0; i < ctx.action_body_item().size(); i++) {
			visit(ctx.action_body_item(i));
		}

		root = node.getParent();

		return node;
	}

	@Override
	public Object visitFlow_ref_field_declaration(PSSParser.Flow_ref_field_declarationContext ctx) {
		String direction = ctx.input_or_output().getText();
		String type = ctx.flow_object_type().getText();
		for (int i = 0; i < ctx.object_ref_field().size(); i++) {
			String id = ctx.object_ref_field(i).getText();
			PSSFlowRefModel node = new PSSFlowRefModel(id, direction.equals("output"), type);
			root.addFlowRef(node);
		}
		return null;
	}

	@Override
	public Object visitStruct_declaration(PSSParser.Struct_declarationContext ctx) {
		if (ctx.struct_super_spec() != null)
			visit(ctx.struct_super_spec());
		PSSStructKind kind = (PSSStructKind) visit(ctx.struct_kind());
		String id = ctx.struct_identifier().getText();

		PSSStructModel node = new PSSStructModel(kind, id);
		root.addChild(node);
		root = node;

		for (int i = 0; i < ctx.struct_body_item().size(); i++) {
			visit(ctx.struct_body_item(i));
		}

		root = node.getParent();

		return node;
	}

	@Override
	public Object visitExec_block(PSSParser.Exec_blockContext ctx) {
		PSSExecKind kind = PSSExecKind.valueOf(ctx.exec_kind().getText());

		PSSExecBlock block = new PSSExecBlock(kind);

		PSSScope.Context outer = m_context;
		m_context = PSSScope.Context.EXEC_BLOCK;
		try {
			for (int i = 0; i < ctx.exec_stmt().size(); i++) {
				PSSProcStmt stmt = (PSSProcStmt) visit(ctx.exec_stmt(i));
				block.addStmt(stmt);
			}
		} finally {
			m_context = outer;
		}

		root.addExecBlock(block, m_log);

		return block;

	}

	@Override
	public Object visitExec_stmt(PSSParser.Exec_stmtContext ctx) {

		if (ctx.procedural_stmt() != null) {
			return visit(ctx.procedural_stmt());
		} else {
			return visit(ctx.exec_super_stmt());
		}
	}

	// Type inheritance (LRM 19.1) is not modeled: a derived type would silently miss
	// the fields, constraints, exec blocks and activity of its base type.
	@Override
	public Object visitComponent_super_spec(PSSParser.Component_super_specContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	@Override
	public Object visitAction_super_spec(PSSParser.Action_super_specContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	@Override
	public Object visitStruct_super_spec(PSSParser.Struct_super_specContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	// An abstract action is only usable as a base type, and may not be traversed.
	@Override
	public Object visitAbstract_action_declaration(PSSParser.Abstract_action_declarationContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	@Override
	public Object visitExec_super_stmt(PSSParser.Exec_super_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	@Override
	public Object visitProcedural_assignment_stmt(PSSParser.Procedural_assignment_stmtContext ctx) {

		PSSRefPathExpression ref_exp = (PSSRefPathExpression) visit(ctx.ref_path());

		String op = ctx.assign_op().getText();

		PSSExpression expression = (PSSExpression) visit(ctx.expression());

		return new PSSAssignProcStmt(ref_exp, op, expression);
	}

	@Override
	public Object visitProcedural_sequence_block_stmt(PSSParser.Procedural_sequence_block_stmtContext ctx) {
		PSSSequenceProcStmt sequence_stmt = new PSSSequenceProcStmt();

		for (int i = 0; i < ctx.procedural_stmt().size(); i++) {
			PSSProcStmt stmt = (PSSProcStmt) visit(ctx.procedural_stmt(i));
			sequence_stmt.add(stmt);
		}
		return sequence_stmt;
	}

	@Override
	public Object visitData_type(PSSParser.Data_typeContext ctx) {
		if (ctx.scalar_data_type() != null) {
			return visit(ctx.scalar_data_type());
		} else if (ctx.collection_type() != null) {
			return visit(ctx.collection_type());
		} else if (ctx.reference_type() != null) {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
			return null;
		} else {
			String text = ctx.type_identifier().getText();
			return new TypeSpec(new PSSDataTypeModel(root, text));
		}
	}

	@Override
	public Object visitScalar_data_type(PSSParser.Scalar_data_typeContext ctx) {
		if (ctx.chandle_type() != null) {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
			return null;
		} else if (ctx.integer_type() != null) {
			return visit(ctx.integer_type());
		} else if (ctx.string_type() != null) {
			return visit(ctx.string_type());
		} else if (ctx.bool_type() != null) {
			return visit(ctx.bool_type());
		} else {
			if (ctx.enum_type().domain_open_range_list() != null) {
				PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.enum_type().getText() + "'");
			}
			String text = ctx.enum_type().getText();
			return new TypeSpec(new PSSDataTypeModel(root, text));
		}
	}

	@Override
	public Object visitArray_type(PSSParser.Array_typeContext ctx) {
		TypeSpec elem = (TypeSpec) visit(ctx.data_type());
		PSSModel data_model = elem.model;

		PSSExpression size_exp = (PSSExpression) visit(ctx.array_size_expression());

		TypeSpec type = new TypeSpec(new PSSArrayModel(data_model, size_exp));
		if (elem.hasDomain())
			type.element = ctx.data_type();
		return type;
	}

	@Override
	public Object visitList_type(PSSParser.List_typeContext ctx) {
		TypeSpec elem = (TypeSpec) visit(ctx.data_type());
		PSSModel data_model = elem.model;

		TypeSpec type = new TypeSpec(new PSSListModel(data_model));
		if (elem.hasDomain())
			type.element = ctx.data_type();
		return type;
	}

	@Override
	public Object visitMap_type(PSSParser.Map_typeContext ctx) {
		PSSModel key_data_type = ((TypeSpec) visit(ctx.data_type(0))).model;
		PSSModel val_data_type = ((TypeSpec) visit(ctx.data_type(1))).model;

		return new TypeSpec(new PSSMapModel(key_data_type, val_data_type));
	}

	@Override
	public Object visitSet_type(PSSParser.Set_typeContext ctx) {
		PSSModel data_type = ((TypeSpec) visit(ctx.data_type())).model;
		return new TypeSpec(new PSSSetModel(data_type));
	}

	@Override
	public Object visitInteger_type(PSSParser.Integer_typeContext ctx) {
		TypeSpec domain = null;
		String integer_atom_type = ctx.integer_atom_type().getText();
		Integer width;
		boolean sign = integer_atom_type.equals("int");

		// bit[7] // [constant_expression]
		// bit[3:0] // [constant_expression (':' number)? ]
		if (ctx.number() != null) {
			int lsb = Integer.valueOf(ctx.number().getText());
			int msb = Integer.valueOf(ctx.constant_expression().getText());
			width = msb - lsb + 1;
		} else if (ctx.constant_expression() != null) {
			width = Integer.valueOf(ctx.constant_expression().getText());
		} else {
			if (sign) {
				width = PSSIntModel.DEFAULT_INT_SIZE;
			} else {
				width = PSSIntModel.DEFAULT_BIT_SIZE;
			}
		}

		// in [0,1,2]	// domain_open_range_value,domain_open_range_value,domain_open_range_value
		// in [0..2]	// constant_expression..constant_expression
		// in [0..]		// constant_expression..
		// in [..2]		// ..constant_expression
		if (ctx.domain_open_range_list() != null) {
			domain = (TypeSpec) visit(ctx.domain_open_range_list());
			if ((domain.ranges != null ? 1 : 0) + domain.open_ended.size() > 1)
				domain = closedDomain(ctx.domain_open_range_list(), width, sign);
		}

		PSSIntModel model = new PSSIntModel(width, sign);
		return domain == null ? new TypeSpec(model) : new TypeSpec(model, domain.ranges, domain.open_ended);
	}

	/** Returns the domain of an integer type, as a {@link TypeSpec} without a model. */
	@Override
	public Object visitDomain_open_range_list(PSSParser.Domain_open_range_listContext ctx) {
		PSSOpenRangeListExpression open_range_list = new PSSOpenRangeListExpression();
		List<PSSExpression> open_ended = new ArrayList<PSSExpression>();
		boolean ranges = false;
		for (int i = 0; i < ctx.domain_open_range_value().size(); i++) {
			PSSExpression item = (PSSExpression) visit(ctx.domain_open_range_value(i));
			// visitDomain_open_range_value builds a range of the values with two bounds
			if (ctx.domain_open_range_value(i).constant_expression().size() == 2) {
				open_range_list.addOpenRangeValueExpression((PSSOpenRangeValueExpression)item);
				ranges = true;
			} else {
				open_ended.add(0, item);
			}
		}
		return new TypeSpec(null, ranges ? open_range_list : null, open_ended);
	}

	/**
	 * PSS 2.0 Section 8.2.1 d): a domain is the union of its items. For a domain that
	 * mixes an open-ended item with other items, each open-ended item becomes the closed
	 * range up to the bound of the type, so that a single 'in' holds the whole union.
	 */
	private TypeSpec closedDomain(PSSParser.Domain_open_range_listContext ctx, int width, boolean sign) {
		PSSVal type_range = PSSRangeVal.getIntRangeVal(width, sign);
		PSSOpenRangeListExpression open_range_list = new PSSOpenRangeListExpression();
		for (PSSParser.Domain_open_range_valueContext value : ctx.domain_open_range_value()) {
			PSSExpression item = (PSSExpression) visit(value);
			if (value.constant_expression().size() == 2) {
				open_range_list.addOpenRangeValueExpression((PSSOpenRangeValueExpression) item);
			} else {
				PSSExpression bound = (PSSExpression) visit(value.constant_expression(0));
				if (item instanceof PSSLessEqualExpression)
					open_range_list.addOpenRangeValueExpression(
						new PSSOpenRangeValueExpression(new PSSPrimaryExpression(type_range.getMin()), bound));
				else
					open_range_list.addOpenRangeValueExpression(
						new PSSOpenRangeValueExpression(bound, new PSSPrimaryExpression(type_range.getMax())));
			}
		}
		return new TypeSpec(null, open_range_list, Collections.<PSSExpression>emptyList());
	}

	@Override
	public Object visitDomain_open_range_value(PSSParser.Domain_open_range_valueContext ctx) {
		if (ctx.RANGE().size() != 1 || ctx.constant_expression().size() == 0) {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		}

		Token exp_node			= ctx.constant_expression(0).getStop();
		TerminalNode range_node = ctx.RANGE(0);
		int exp_position		= exp_node.getTokenIndex();
		int range_position		= range_node.getSymbol().getTokenIndex();

		if (ctx.constant_expression().size() == 2) {	// constant_expression '..' constant_expression
			PSSExpression begin = (PSSExpression) visit(ctx.constant_expression(0));
			PSSExpression end = (PSSExpression) visit(ctx.constant_expression(1));
			return new PSSOpenRangeValueExpression(begin, end);
		} else if (range_position < exp_position) {	// '..' constant_expression
			PSSExpression end = (PSSExpression) visit(ctx.constant_expression(0));
			return new PSSLessEqualExpression(null, end);
		} else {	// constant_expression ('..')?
			PSSExpression begin = (PSSExpression) visit(ctx.constant_expression(0));
			return new PSSGreaterEqualExpression(null, begin);
		}
	}

	@Override
	public Object visitString_type(PSSParser.String_typeContext ctx) {
		if (ctx.string_literal().size() != 0) {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		}
		return new TypeSpec(m_string_type);
	}

	@Override
	public Object visitBool_type(PSSParser.Bool_typeContext ctx) {
		return new TypeSpec(m_bool_type);
	}

	@Override
	public Object visitProcedural_data_declaration(PSSParser.Procedural_data_declarationContext ctx) {
		PSSModel type = ((TypeSpec) visit(ctx.data_type())).model;

		PSSDataDeclProcStmt data_decl = new PSSDataDeclProcStmt(type);
		for (int i = 0; i < ctx.procedural_data_instantiation().size(); i++) {
			PSSParser.Procedural_data_instantiationContext ctx_data_inst = ctx.procedural_data_instantiation(i);

			String id = ctx_data_inst.identifier().getText();

            PSSExpression array_dim = null;
            if (ctx_data_inst.array_dim() != null) {
                array_dim = (PSSExpression) visit(ctx_data_inst.array_dim().constant_expression());
            }

			PSSExpression expression = null;
			if (ctx_data_inst.expression() != null) {
				expression = (PSSExpression) visit(ctx_data_inst.expression());
			}

			PSSDataInstProcStmt stmt = new PSSDataInstProcStmt(id, array_dim, expression);
			data_decl.addInst(stmt);

		}
		return data_decl;
	}

	@Override
	public Object visitProcedural_void_function_call_stmt(PSSParser.Procedural_void_function_call_stmtContext ctx) {
		PSSExpression func = (PSSExpression) visit(ctx.function_call());
		if (!(func instanceof PSSFunctionCallExpression))
			PSSMessage.Fatal("Return type of function_call is not PSSFunctionCallExpression.");
		return new PSSVoidFunctionCallProcStmt((PSSFunctionCallExpression) func);
	}

	@Override
	public Object visitProcedural_return_stmt(PSSParser.Procedural_return_stmtContext ctx) {
		PSSExpression exp = null;
		if (ctx.expression() != null) {
			exp = (PSSExpression) visit(ctx.expression());
		}
		return new PSSReturnProcStmt(exp);
	}

	@Override
	public Object visitProcedural_repeat_stmt(PSSParser.Procedural_repeat_stmtContext ctx) {
        String index_id = null;
        if (ctx.index_identifier() != null)
            index_id = ctx.index_identifier().getText();

		PSSExpression cond = (PSSExpression) visit(ctx.expression());

		PSSProcStmt stmt = (PSSProcStmt) visit(ctx.procedural_stmt());

		return new PSSRepeatProcStmt(index_id, cond, stmt);
	}

	@Override
	public Object visitProcedural_repeat_while_stmt(PSSParser.Procedural_repeat_while_stmtContext ctx) {
		PSSExpression cond = (PSSExpression) visit(ctx.expression());

		PSSProcStmt stmt = (PSSProcStmt) visit(ctx.procedural_stmt());

		return new PSSRepeatWhileProcStmt(cond, stmt);
	}

	@Override
	public Object visitProcedural_while_stmt(PSSParser.Procedural_while_stmtContext ctx) {
		PSSExpression cond = (PSSExpression) visit(ctx.expression());

		PSSProcStmt stmt = (PSSProcStmt) visit(ctx.procedural_stmt());

		return new PSSWhileProcStmt(cond, stmt);
	}

	private String separate_index(PSSExpression exp) {
		if (!(exp instanceof PSSRefPathExpression))
			return null;

		PSSRefPathExpression e = (PSSRefPathExpression) exp;

		if (e.getBitSliceFrom() != null || e.getBitSliceTo() != null)
			return null;

		// Find the last member path element
		PSSMemberPathElemExpression me = e.getRefPath();
		while (me.getChild() != null)
			me = me.getChild();

		String index = null;
		if (me.getIndex() != null && me.getIndex() instanceof PSSRefPathExpression) {
			PSSRefPathExpression index_exp = (PSSRefPathExpression) me.getIndex();
			if ((index_exp.getTypeIdentifierElems() == null || index_exp.getTypeIdentifierElems().equals(""))
					&& index_exp.getBitSliceFrom() == null
					&& index_exp.getBitSliceTo() == null
					&& index_exp.getRefPath().getChild() == null) {
				index = index_exp.getText();
				me.setIndex(null);
			}
		}

		return index;
	}

	@Override
	public Object visitProcedural_foreach_stmt(PSSParser.Procedural_foreach_stmtContext ctx) {
		String iterator_id = null;
		PSSExpression exp = null;
		PSSProcStmt stmt = null;
		String index_id = null;
		if (ctx.iterator_identifier() != null)
			iterator_id = ctx.iterator_identifier().getText();
		exp = (PSSExpression) visit(ctx.expression());
		if (ctx.index_identifier() != null)
			index_id = ctx.index_identifier().getText();
		stmt = (PSSProcStmt) visit(ctx.procedural_stmt());

		/* try to separate index_identifier due to grammar ambiguity */
		if (index_id == null)
			index_id = separate_index(exp);

		return new PSSForeachProcStmt(iterator_id, exp, index_id, stmt);
	}

	@Override
	public Object visitProcedural_if_else_stmt(PSSParser.Procedural_if_else_stmtContext ctx) {
		PSSExpression cond = (PSSExpression) visit(ctx.expression());

		PSSProcStmt true_path = (PSSProcStmt) visit(ctx.procedural_stmt(0));
		PSSProcStmt false_path = null;

		if (ctx.procedural_stmt().size() == 2) {
			false_path = (PSSProcStmt) visit(ctx.procedural_stmt(1));
		}

		return new PSSIfElseProcStmt(cond, true_path, false_path);
	}

	@Override
	public Object visitProcedural_match_stmt(PSSParser.Procedural_match_stmtContext ctx) {
                PSSExpression cond = (PSSExpression) visit(ctx.match_expression());
                int size = ctx.procedural_match_choice().size();
                ArrayList<PSSExpression> ranges = new ArrayList<PSSExpression> (size);
                ArrayList<PSSProcStmt> stmts = new ArrayList<PSSProcStmt> (size);
                PSSProcStmt stmt = null;
                for (int i = 0; i < size; i++) {
                    if (ctx.procedural_match_choice(i).open_range_list() != null) {
                        // open range list
                        ranges.add((PSSExpression) visit(ctx.procedural_match_choice(i).open_range_list()));
                        stmts.add((PSSProcStmt) visit(ctx.procedural_match_choice(i).procedural_stmt()));
                    } else {
                        // default
                        stmt = (PSSProcStmt) visit(ctx.procedural_match_choice(i).procedural_stmt());
                    }
                }
		return new PSSMatchProcStmt(cond, ranges, stmts, stmt);
	}

	@Override
	public Object visitProcedural_break_stmt(PSSParser.Procedural_break_stmtContext ctx) {
            return new PSSBreakProcStmt();
	}

	@Override
	public Object visitProcedural_continue_stmt(PSSParser.Procedural_continue_stmtContext ctx) {
            return new PSSContinueProcStmt();
	}

	@Override
	public Object visitTarget_code_exec_block(PSSParser.Target_code_exec_blockContext ctx) {
		String language = ctx.language_identifier().getText();
		PSSExecKind exec_kind = PSSExecKind.valueOf(ctx.exec_kind().getText());
		String code = ctx.string_literal().QUOTED_STRING() != null ? quotedString(ctx.string_literal().getText())
				: ctx.string_literal().getText();
		PSSTargetCode node = new PSSTargetCode(root, exec_kind, language, code, PSSTemplateRefParser.INSTANCE);

		((PSSActionModel) root).addTargetCode(node);
		return node;
	}

	@Override
	public Object visitActivity_declaration(PSSParser.Activity_declarationContext ctx) {
		PSSScope.Context outer = m_context;
		m_context = PSSScope.Context.ACTIVITY;
		try {
			for (int i = 0; i < ctx.activity_declaration_body_stmt().size(); i++) {
				PSSActivity stmt = (PSSActivity) visit(ctx.activity_declaration_body_stmt(i));
				if (stmt != null) {
					((PSSActionModel) root).addActivityStmt(stmt);
				}
			}
		} finally {
			m_context = outer;
		}
		return null;
	}

	@Override
	public Object visitActivity_action_traversal_stmt(PSSParser.Activity_action_traversal_stmtContext ctx) {
		String handle_id = "";
		String type_id = "";

		if (ctx.identifier() != null) { // anonymous
			handle_id = ctx.identifier().getText();
		} else {
			type_id = ctx.type_identifier().getText();
		}

		PSSTraversalActivity node = new PSSTraversalActivity(handle_id, type_id);
		if (ctx.inline_constraints_or_empty().constraint_set() != null) {
			ConstraintSet set;
			PSSScope.Context outer = m_context;
			m_context = PSSScope.Context.INLINE_CONSTRAINT;
			try {
				set = constraintSet(ctx.inline_constraints_or_empty().constraint_set());
			} finally {
				m_context = outer;
			}

			for (PSSConstraint item : set.constraints) {
				node.addConstraint(item);
			}
			for (PSSConstraint item : set.defaults) {
				node.addDefaultConstraint(item);
			}
		}

		return node;
	}

	@Override
	public Object visitSymbol_declaration(PSSParser.Symbol_declarationContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	public Object visitConstraint_declaration(PSSParser.Constraint_declarationContext ctx) {
		ConstraintSet set;
		PSSScope.Context outer = m_context;
		m_context = PSSScope.Context.MEMBER_CONSTRAINT;
		try {
			if (ctx.identifier() != null) {
				set = constraintBlock(ctx.constraint_block());
			} else {
				set = constraintSet(ctx.constraint_set());
			}
		} finally {
			m_context = outer;
		}

		for (PSSConstraint item : set.constraints) {
			root.addConstraint(item);
		}

		for (PSSConstraint item : set.defaults) {
			root.addDefaultConstraint(item);
		}
		return set;
	}

	/** The constraints of a constraint set, in the order they are written. */
	private ConstraintSet constraintSet(PSSParser.Constraint_setContext ctx) {
		if (ctx.constraint_block() != null)
			return constraintBlock(ctx.constraint_block());
		return (ConstraintSet) visit(ctx.constraint_body_item());
	}

	/** The constraints of a constraint block, in the order they are written. */
	private ConstraintSet constraintBlock(PSSParser.Constraint_blockContext ctx) {
		ConstraintSet set = new ConstraintSet();
		for (PSSParser.Constraint_body_itemContext item : ctx.constraint_body_item())
			set.addAll((ConstraintSet) visit(item));
		return set;
	}

	@Override
	public Object visitConstraint_body_item(PSSParser.Constraint_body_itemContext ctx) {
		ConstraintSet set = new ConstraintSet();
		if        (ctx.expression_constraint_item() != null) {
			set.constraints.add((PSSConstraint) visit(ctx.expression_constraint_item()));
		} else if (ctx.foreach_constraint_item() != null) {
			set = (ConstraintSet) visit(ctx.foreach_constraint_item());
		} else if (ctx.forall_constraint_item() != null) {
			visit(ctx.forall_constraint_item());
		} else if (ctx.if_constraint_item() != null) {
			set = (ConstraintSet) visit(ctx.if_constraint_item());
		} else if (ctx.implication_constraint_item() != null) {
			visit(ctx.implication_constraint_item());
		} else if (ctx.unique_constraint_item() != null) {
			set.constraints.add((PSSConstraint) visit(ctx.unique_constraint_item()));
		} else if (ctx.constant_expression() != null) {
			// Syntax: 'default' hierarchical_id '==' constant_expression ';'
			PSSExpression const_exp = (PSSExpression) visit(ctx.constant_expression());
			PSSExpression id = (PSSExpression) visit(ctx.hierarchical_id());
			PSSEqualExpression equal_exp = new PSSEqualExpression(id, const_exp);
			PSSExpressionConstraint item = new PSSExpressionConstraint(equal_exp);
			set.defaults.add(item);
		} else if (ctx.hierarchical_id() != null) {
			// Syntax: 'default' 'disable' hierarchical_id ';'
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		}
		return set;
	}

	@Override
	public Object visitExpression_constraint_item(PSSParser.Expression_constraint_itemContext ctx) {
		PSSExpression exp = (PSSExpression) visit(ctx.expression());

		return new PSSExpressionConstraint(exp);
	}

	/** Returns the foreach constraint, and the default constraints written in its body. */
	@Override
	public Object visitForeach_constraint_item(PSSParser.Foreach_constraint_itemContext ctx) {
		String iterator_id = null;
		PSSExpression exp = null;
		String index_id = null;
		if (ctx.iterator_identifier() != null)
			iterator_id = ctx.iterator_identifier().getText();
		exp = (PSSExpression) visit(ctx.expression());
		if (ctx.index_identifier() != null)
			index_id = ctx.index_identifier().getText();
		/* try to separate index_identifier due to grammar ambiguity */
		if (index_id == null)
			index_id = separate_index(exp);

		ConstraintSet body = constraintSet(ctx.constraint_set());

		ConstraintSet set = new ConstraintSet();
		set.constraints.add(new PSSForeachConstraint(iterator_id, exp, index_id, body.constraints));
		set.defaults.addAll(body.defaults);
		return set;
	}

	@Override
	public Object visitForall_constraint_item(PSSParser.Forall_constraint_itemContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	/** Returns the if constraint, and the default constraints written in its branches. */
	@Override
	public Object visitIf_constraint_item(PSSParser.If_constraint_itemContext ctx) {
		PSSExpression exp = (PSSExpression) visit(ctx.expression());

		PSSIfConstraint constraint = new PSSIfConstraint(exp);
		ConstraintSet set = new ConstraintSet();

		ConstraintSet true_set = constraintSet(ctx.constraint_set(0));
		constraint.addTrueConstraint(true_set.constraints);
		for (PSSConstraint item : true_set.defaults)
			((PSSExpressionConstraint) item).addGuard(exp, true);
		set.defaults.addAll(true_set.defaults);

		if (ctx.constraint_set().size() > 1) {
			ConstraintSet false_set = constraintSet(ctx.constraint_set(1));
			constraint.addFalseConstraint(false_set.constraints);
			for (PSSConstraint item : false_set.defaults)
				((PSSExpressionConstraint) item).addGuard(exp, false);
			set.defaults.addAll(false_set.defaults);
		}

		set.constraints.add(constraint);

		return set;
	}

	@Override
	public Object visitImplication_constraint_item(PSSParser.Implication_constraint_itemContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	@Override
	public Object visitUnique_constraint_item(PSSParser.Unique_constraint_itemContext ctx) {
		PSSUniqueConstraint constraint = new PSSUniqueConstraint();

		for (int i = 0; i < ctx.hierarchical_id_list().hierarchical_id().size(); i++) {
			constraint.add((PSSMemberPathElemExpression) visit(ctx.hierarchical_id_list().hierarchical_id(i)));
		}

		return constraint;
	}

	private ParserRuleContext getBinaryOperator(PSSParser.ExpressionContext ctx) {
		if (ctx.binary_operator_precedence3() != null)
			return ctx.binary_operator_precedence3();
		if (ctx.binary_operator_precedence4() != null)
			return ctx.binary_operator_precedence4();
		if (ctx.binary_operator_precedence5() != null)
			return ctx.binary_operator_precedence5();
		if (ctx.binary_operator_precedence6() != null)
			return ctx.binary_operator_precedence6();
		if (ctx.binary_operator_precedence7() != null)
			return ctx.binary_operator_precedence7();
		if (ctx.binary_operator_precedence8() != null)
			return ctx.binary_operator_precedence8();
		if (ctx.binary_operator_precedence9() != null)
			return ctx.binary_operator_precedence9();
		if (ctx.binary_operator_precedence10() != null)
			return ctx.binary_operator_precedence10();
		if (ctx.binary_operator_precedence11() != null)
			return ctx.binary_operator_precedence11();
		if (ctx.binary_operator_precedence12() != null)
			return ctx.binary_operator_precedence12();
		if (ctx.binary_operator_precedence13() != null)
			return ctx.binary_operator_precedence13();
		return null;
	}

	@Override
	public Object visitExpression(PSSParser.ExpressionContext ctx) {
		ParserRuleContext binop = getBinaryOperator(ctx);
		if (binop != null) {
			PSSExpression left = (PSSExpression) visit(ctx.expression(0));
			PSSExpression right = (PSSExpression) visit(ctx.expression(1));
			String op = binop.getText();

			switch (op) {
				case "==":
					return new PSSEqualExpression(left, right);
				case "!=":
					return new PSSNotEqualExpression(left, right);
				case ">":
					return new PSSGreaterThanExpression(left, right);
				case ">=":
					return new PSSGreaterEqualExpression(left, right);
				case "<":
					return new PSSLessThanExpression(left, right);
				case "<=":
					return new PSSLessEqualExpression(left, right);
				case "+":
					return new PSSAddExpression(left, right);
				case "-":
					return new PSSSubExpression(left, right);
				case "*":
					return new PSSMulExpression(left, right);
				case "/":
					return new PSSDivExpression(left, right);
				case "%":
					return new PSSModExpression(left, right);
				case "<<":
					return new PSSLeftShiftExpression(left, right);
				case ">>":
					return new PSSRightShiftExpression(left, right);
				case "&&":
					return new PSSLogicalAndExpression(left, right);
				case "||":
					return new PSSLogicalOrExpression(left, right);
				case "&":
					return new PSSBitwiseAndExpression(left, right);
				case "|":
					return new PSSBitwiseOrExpression(left, right);
				case "^":
					return new PSSBitwiseXorExpression(left, right);
				default:
					return new PSSBinaryExpression(left, op, right);
			}

		} else if (ctx.unary_operator() != null) {
			String op = ctx.unary_operator().getText();
			PSSExpression exp = (PSSExpression) visit(ctx.primary());

			switch (op) {
				case "!":
					return new PSSLogicalNotExpression(exp);
				case "~":
					return new PSSBitwiseNotExpression(exp);
				case "-":
					return new PSSUnaryMinusExpression(exp);
				default:
					PSSMessage.Fatal("unary operator '" + op + "'is not yet supported");
					return null;
			}
		} else if (ctx.primary() != null) {
			return visit(ctx.primary());
		} else if (ctx.open_range_list() != null) {
			PSSExpression left = (PSSExpression) visit(ctx.expression(0));
			PSSExpression right = (PSSExpression) visit(ctx.open_range_list());
			return new PSSInExpression(left, right, bareIdentifier(ctx.expression(0)));
		} else if (ctx.collection_expression() != null) {
			PSSExpression left = (PSSExpression) visit(ctx.expression(0));
			PSSExpression right = (PSSExpression) visit(ctx.collection_expression());
			return new PSSInExpression(left, right, bareIdentifier(ctx.expression(0)));
		} else {
			PSSMessage.Error("VISITOR-0001", "visitExpression is not fully implemented yet");
			return null;
		}
	}

	@Override
	public Object visitPrimary(PSSParser.PrimaryContext ctx) {

		if (ctx.string_literal() != null) {
			String text = ctx.string_literal().QUOTED_STRING() != null ? quotedString(ctx.getText())
					: ctx.getText().replace("\"", "");
			return new PSSPrimaryExpression(new PSSStringVal(text));
		} else if (ctx.number() != null) {
			String text = ctx.getText();
			if (ctx.number().BIN_NUMBER() != null) {
				return new PSSPrimaryExpression(PSSNumber.newBinNumber(text));
			} else if (ctx.number().OCT_NUMBER() != null) {
				return new PSSPrimaryExpression(PSSNumber.newOctNumber(text));
			} else if (ctx.number().DEC_NUMBER() != null) {
				return new PSSPrimaryExpression(PSSNumber.newDecNumber(text));
			} else if (ctx.number().HEX_NUMBER() != null) {
				return new PSSPrimaryExpression(PSSNumber.newHexNumber(text));
			} else if (ctx.number().based_bin_number() != null) {
				return new PSSPrimaryExpression(PSSNumber.newBasedBinNumber(text));
			} else if (ctx.number().based_oct_number() != null) {
				return new PSSPrimaryExpression(PSSNumber.newBasedOctNumber(text));
			} else if (ctx.number().based_dec_number() != null) {
				return new PSSPrimaryExpression(PSSNumber.newBasedDecNumber(text));
			} else {
				return new PSSPrimaryExpression(PSSNumber.newBasedHexNumber(text));
			}
		} else if (ctx.bool_literal() != null) {
			String text = ctx.getText();
			return new PSSPrimaryExpression(PSSBoolVal.literal(text));
		} else if (ctx.ref_path() != null) {
			return visit(ctx.ref_path());
		} else if (ctx.paren_expr() != null) {
			return visit(ctx.paren_expr().expression());
		} else if (ctx.aggregate_literal() != null) {
			return visit(ctx.aggregate_literal());
		} else if (ctx.cast_expression() != null) {
			return visit(ctx.cast_expression());
		} else {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
			return null;
		}
	}

	@Override
	public Object visitCollection_expression(PSSParser.Collection_expressionContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public Object visitMember_path_elem(PSSParser.Member_path_elemContext ctx) {
		String id = ctx.identifier().getText();
		List<PSSExpression> args = null;
		PSSExpression index = null;
		if (ctx.function_parameter_list() != null) {
			args = new ArrayList<PSSExpression>();
			for (int i = 0; i < ctx.function_parameter_list().expression().size(); i++) {
				args.add((PSSExpression) visit(ctx.function_parameter_list().expression(i)));
			}
		}
		if (ctx.expression() != null) {
			index = (PSSExpression) visit(ctx.expression());
		}

		return new PSSMemberPathElemExpression(id, args, index, m_context);
	}

	@Override
	public Object visitHierarchical_id(PSSParser.Hierarchical_idContext ctx) {
		PSSMemberPathElemExpression r = null;
		PSSMemberPathElemExpression p = null;
		for (int i = 0; i < ctx.member_path_elem().size(); i++) {
			PSSMemberPathElemExpression e = (PSSMemberPathElemExpression) visit(ctx.member_path_elem(i));
			if (r == null)
				r = e;
			e.setParent(p);
			p = e;
		}

		return r;
	}

	@Override
	public Object visitRef_path(PSSParser.Ref_pathContext ctx) {
		if (ctx.static_ref_path() == null)
			PSSMessage.Fatal("super in ref_path is not implemented");

		String type_identifier_elem = null;
		if (ctx.static_ref_path().type_identifier_elem() != null) {
			List<String> strs = new ArrayList<String>();
			for (int i = 0; i < ctx.static_ref_path().type_identifier_elem().size(); i++) {
				strs.add(ctx.static_ref_path().type_identifier_elem(i).getText() + "::");
			}
			type_identifier_elem = String.join("", strs);
			// A leading '::' starts the qualifier at the outermost namespace (PSS 2.0 Section 21.3).
			if (ctx.static_ref_path().getChild(0).getText().equals("::"))
				type_identifier_elem = "::" + type_identifier_elem;
		}

		PSSMemberPathElemExpression static_ref_path = (PSSMemberPathElemExpression) visit(ctx.static_ref_path().member_path_elem());

		if (ctx.hierarchical_id() != null) {
			PSSMemberPathElemExpression hierarchical_id = (PSSMemberPathElemExpression) visit(ctx.hierarchical_id());
			hierarchical_id.setParent(static_ref_path);
		}

		PSSExpression bit_slice_from = null;
		PSSExpression bit_slice_to = null;
		if (ctx.bit_slice() != null) {
			bit_slice_to = (PSSExpression) visit(ctx.bit_slice().constant_expression(0));
			bit_slice_from = (PSSExpression) visit(ctx.bit_slice().constant_expression(1));
		}

		return new PSSRefPathExpression(type_identifier_elem, static_ref_path, bit_slice_from, bit_slice_to);
	}

	@Override
	public Object visitAggregate_literal(PSSParser.Aggregate_literalContext ctx) {
		if (ctx.empty_aggregate_literal() != null) {
			return new PSSAggregateExpression();
		} else if (ctx.value_list_literal() != null) {
			PSSAggregateExpression exp = new PSSAggregateExpression();
			for (int i = 0; i < ctx.value_list_literal().expression().size(); i++) {
				PSSExpression item = (PSSExpression) visit(ctx.value_list_literal().expression(i));
				exp.addExpression(item);
			}
			return exp;
		} else if (ctx.map_literal() != null) {
			PSSMapExpression exp = new PSSMapExpression();
			for (int i = 0; i < ctx.map_literal().map_literal_item().size(); i++) {
				PSSExpression key = (PSSExpression) visit(ctx.map_literal().map_literal_item(i).expression(0));
				PSSExpression val = (PSSExpression) visit(ctx.map_literal().map_literal_item(i).expression(1));
				exp.add(key, val);
			}
			return exp;
		} else {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
			return null;
		}
	}

	@Override
	public Object visitCast_expression(PSSParser.Cast_expressionContext ctx) {
		PSSModel cast_type = ((TypeSpec) visit(ctx.casting_type())).model;
		PSSExpression rhs_exp = (PSSExpression) visit(ctx.primary());
		return new PSSCastExpression(cast_type, rhs_exp);
	}

	@Override
	public Object visitCasting_type(PSSParser.Casting_typeContext ctx) {
		if (ctx.integer_type() != null) {
			return visit(ctx.integer_type());
		} else if (ctx.bool_type() != null) {
			PSSMessage.Fatal("Cast to bool_type is not yet supported: '" + ctx.getText() + "'");
		} else if (ctx.enum_type() != null) {
			PSSMessage.Fatal("Cast to enum_type is not yet supported: '" + ctx.getText() + "'");
		} else if (ctx.type_identifier() != null) {
			PSSMessage.Fatal("Cast to user-defined type is not yet supported: '" + ctx.getText() + "'");
		} else {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		}
		return null;
	}

	@Override
	public Object visitOpen_range_list(PSSParser.Open_range_listContext ctx) {
		PSSOpenRangeListExpression open_range_list = new PSSOpenRangeListExpression();
		for (int i = 0; i < ctx.open_range_value().size(); i++) {
			PSSOpenRangeValueExpression value = (PSSOpenRangeValueExpression) visit(ctx.open_range_value(i));
			open_range_list.addOpenRangeValueExpression(value);
		}
		return open_range_list;
	}

	@Override
	public Object visitOpen_range_value(PSSParser.Open_range_valueContext ctx) {
		if (ctx.expression().size() == 2 && ctx.RANGE() != null) {	// expression '..' expression
			PSSExpression begin = (PSSExpression) visit(ctx.expression(0));
			PSSExpression end = (PSSExpression) visit(ctx.expression(1));
			return new PSSOpenRangeValueExpression(begin, end);
		} else if (ctx.expression().size() == 1 && ctx.RANGE() == null) {	// expression
			PSSExpression begin = (PSSExpression) visit(ctx.expression(0));
			return new PSSOpenRangeValueExpression(begin);
		} else {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
			return null;
		}
	}

	@Override
	public Object visitFunction_call(PSSParser.Function_callContext ctx) {
		PSSFunctionCallExpression call = (PSSFunctionCallExpression) visit(ctx.function_ref_path());
		List<String> pkg = new ArrayList<String>();
		for (PSSParser.Type_identifier_elemContext elem : ctx.type_identifier_elem())
			pkg.add(elem.getText());
		// A leading '::' starts the qualifier at the outermost namespace (PSS 2.0 Section 21.3).
		String root_prefix = ctx.getChild(0).getText().equals("::") ? "::" : "";
		if (!pkg.isEmpty())
			call.setPackage(root_prefix + String.join("::", pkg));
		return call;
	}

	@Override
	public Object visitFunction_ref_path(PSSParser.Function_ref_pathContext ctx) {
		PSSMemberPathElemExpression path = null;
		PSSMemberPathElemExpression parent = null;
		for (int i = 0; i < ctx.member_path_elem().size(); i++) {
			PSSMemberPathElemExpression e = (PSSMemberPathElemExpression) visit(ctx.member_path_elem(i));
			if (path == null)
				path = e;
			e.setParent(parent);
			parent = e;
		}

		String id = ctx.identifier().getText();

		List<PSSExpression> args = new ArrayList<PSSExpression>();
		List<PSSParser.ExpressionContext> args_ctx = ctx.function_parameter_list().expression();
		for (int i = 0; i < args_ctx.size(); i++) {
			args.add((PSSExpression) visitExpression(args_ctx.get(i)));
		}

		return new PSSFunctionCallExpression(path, id, args, m_context);
	}

	@Override
	public Object visitActivity_bind_stmt(PSSParser.Activity_bind_stmtContext ctx) {
		String id = ctx.hierarchical_id().getText();

		PSSBindActivity node = new PSSBindActivity(id);

		// FIXME
		// activity_bind_item_or_list
		// : hierarchical_id
		// | '{' hierarchical_id_list '}'
		// ;
		String item = ctx.activity_bind_item_or_list().getText();
		node.addBindItem(item);

		return node;
	}

	@Override
	public Object visitActivity_if_else_stmt(PSSParser.Activity_if_else_stmtContext ctx) {
		PSSExpression cond = (PSSExpression) visit(ctx.expression());

		PSSIfElseActivity if_else_stmt = new PSSIfElseActivity(cond);

		PSSActivity true_stmt = (PSSActivity) visit(ctx.activity_stmt(0));
		if_else_stmt.addTrueStmt(true_stmt);

		if (ctx.activity_stmt().size() == 2) {
			PSSActivity false_stmt = (PSSActivity) visit(ctx.activity_stmt(1));
			if_else_stmt.addFalseStmt(false_stmt);
		}

		return if_else_stmt;
	}

	@Override
	public Object visitActivity_repeat_stmt(PSSParser.Activity_repeat_stmtContext ctx) {
		String index_id = "";

		if (ctx.index_identifier() != null) {
			index_id = ctx.index_identifier().getText();
		}

		PSSExpression count = (PSSExpression) visit(ctx.expression());

		PSSActivity stmt = (PSSActivity) visit(ctx.activity_stmt());

		return new PSSRepeatActivity(count, stmt, index_id);
	}

	@Override
	public Object visitActivity_sequence_block_stmt(PSSParser.Activity_sequence_block_stmtContext ctx) {
		PSSSequenceActivity sequence_stmt = new PSSSequenceActivity();
		for (int i = 0; i < ctx.activity_stmt().size(); i++) {
			PSSActivity stmt = (PSSActivity) visit(ctx.activity_stmt(i));
			sequence_stmt.addActivity(stmt);

		}
		return sequence_stmt;
	}

	@Override
	public Object visitActivity_parallel_stmt(PSSParser.Activity_parallel_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	@Override
	public Object visitActivity_schedule_stmt(PSSParser.Activity_schedule_stmtContext ctx) {
		PSSScheduleActivity schedule_stmt = new PSSScheduleActivity();
		for (int i = 0; i < ctx.activity_stmt().size(); i++) {
			PSSActivity stmt = (PSSActivity) visit(ctx.activity_stmt(i));
			schedule_stmt.addActivity(stmt);
		}
		return schedule_stmt;
	}

	@Override
	public Object visitActivity_foreach_stmt(PSSParser.Activity_foreach_stmtContext ctx) {
		String iterator_id = null;
		PSSExpression exp = null;
		PSSActivity stmt = null;
		String index_id = null;
		if (ctx.iterator_identifier() != null)
			iterator_id = ctx.iterator_identifier().getText();
		exp = (PSSExpression) visit(ctx.expression());
		if (ctx.index_identifier() != null)
			index_id = ctx.index_identifier().getText();
		stmt = (PSSActivity) visit(ctx.activity_stmt());

		/* try to separate index_identifier due to grammar ambiguity */
		if (index_id == null)
			index_id = separate_index(exp);

		return new PSSForeachActivity(iterator_id, exp, index_id, stmt);
	};

	@Override
	public Object visitActivity_select_stmt(PSSParser.Activity_select_stmtContext ctx) {
		PSSSelectActivity select_stmt = new PSSSelectActivity();

		for (int i = 0; i < ctx.select_branch().size(); i++) {
			PSSSelectBranchActivity select_branch = (PSSSelectBranchActivity) visit(ctx.select_branch(i));
			select_stmt.addBranch(select_branch);
		}

		return select_stmt;
	}

	@Override
	public Object visitSelect_branch(PSSParser.Select_branchContext ctx) {
		PSSExpression cond = null;
		PSSExpression weight = null;

		if (ctx.select_branch_cond() != null) {
			cond = (PSSExpression) visit(ctx.select_branch_cond().expression());
		}

		if (ctx.select_branch_weight() != null) {
			weight = (PSSExpression) visit(ctx.select_branch_weight().expression());
		}

		PSSActivity stmt = (PSSActivity) visit(ctx.activity_stmt());

		return new PSSSelectBranchActivity(stmt, cond, weight);
	}

	@Override
	public Object visitActivity_match_stmt(PSSParser.Activity_match_stmtContext ctx) {
                PSSExpression cond = (PSSExpression) visit(ctx.match_expression());
                PSSMatchActivity match_stmt = new PSSMatchActivity(cond);

                for (PSSParser.Match_choiceContext choice_ctx : ctx.match_choice()) {
                        if (choice_ctx.open_range_list() != null) {
                            // open range list
                            PSSExpression r = (PSSExpression) visit(choice_ctx.open_range_list());
                            PSSActivity s = (PSSActivity) visit(choice_ctx.activity_stmt());
                            match_stmt.addChoice(r, s);
                        } else {
                            // default
                            PSSActivity s = (PSSActivity) visit(choice_ctx.activity_stmt());
                            match_stmt.addDefaultChoice(s);
                        }
                }
		return match_stmt;
	}

	// Every activity statement builds exactly one activity node; the alternatives
	// that would build none are rejected here.
	@Override
	public Object visitActivity_stmt(PSSParser.Activity_stmtContext ctx) {
		if (ctx.labeled_activity_stmt() == null && ctx.activity_bind_stmt() == null) {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		}
		return visitChildren(ctx);
	}

	@Override
	public Object visitSymbol_call(PSSParser.Symbol_callContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	@Override
	public Object visitActivity_replicate_stmt(PSSParser.Activity_replicate_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	@Override
	public Object visitActivity_super_stmt(PSSParser.Activity_super_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return null;
	}

	@Override
	public Object visitAttr_field(PSSParser.Attr_fieldContext ctx) {
		boolean rand = ctx.rand_or_static_const() != null && ctx.rand_or_static_const().getText().equals("rand");

		PSSDataDeclModel decl = (PSSDataDeclModel) visit(ctx.data_declaration());
		// PSS 2.0 Section 21.3: a static const is a static member of the type; it also
		// stays readable through an instance, as a field that is not random.
		if (ctx.rand_or_static_const() != null && !rand)
			decl.declStatic(root);
		PSSAttrField attrfield = new PSSAttrField(rand, decl);
		root.addAttrField(attrfield);

		return attrfield;
	}

	/** PSS 2.0 Section 21.1.1.1 b): a const field of a package is a static constant. */
	@Override
	public Object visitConst_field_declaration(PSSParser.Const_field_declarationContext ctx) {
		PSSDataDeclModel decl = (PSSDataDeclModel) visit(ctx.data_declaration());
		decl.declStatic(root);
		return decl;
	}

	@Override
	public Object visitData_declaration(PSSParser.Data_declarationContext ctx) {
		TypeSpec type = (TypeSpec) visit(ctx.data_type());
		PSSModel data_type = type.model;
		root.addChild(data_type);

		PSSDataDeclModel data_decl = new PSSDataDeclModel(data_type);

		boolean domain = type.hasDomain();
		for (int i = 0; i < ctx.data_instantiation().size(); i++) {
			PSSParser.Data_instantiationContext data_inst = ctx.data_instantiation(i);

			String id = data_inst.identifier().getText();
			// PSS 2.0 Section 4.2: in a given namespace, identifiers shall be unique. Both
			// branches of a compile if are visited, so a field declared in each is not a
			// second declaration.
			if (!inCompileIf(ctx) && !m_field_ids.computeIfAbsent(root, r -> new HashSet<String>()).add(id))
				PSSMessage.Error("PSS 2.0 Section 4.2", "'" + id + "' is declared more than once in '" + root.getId() + "'");
			PSSExpression array_dim = null;
			if (data_inst.array_dim() != null) {
				array_dim = (PSSExpression) visit(data_inst.array_dim().constant_expression());
			}

			PSSExpression const_expression = null;
			if (data_inst.constant_expression() != null) {
				const_expression = (PSSExpression) visit(data_inst.constant_expression());
			}

			if (domain && array_dim == null) {
				// PSS 2.0 Section 8.2.1 e): the domain belongs to the type, so it constrains
				// every field declared with it. Each field gets its own domain items, as an
				// open-ended item holds the field it constrains.
				TypeSpec field_type = i == 0 ? type : (TypeSpec) visit(ctx.data_type());
				PSSMemberPathElemExpression variable = new PSSMemberPathElemExpression(id, PSSScope.Context.MEMBER_CONSTRAINT);
				for (PSSConstraint c : domainConstraints(field_type, variable))
					root.addConstraint(c);
			} else if (domain || type.element != null) {
				// The elements of an array field 'T x[N]', or of an array or list type whose
				// element type has a domain, each have the type of an element.
				TypeSpec elem_type = (TypeSpec) visit(domain ? ctx.data_type() : type.element);
				String index = "$index";
				PSSMemberPathElemExpression element = new PSSMemberPathElemExpression(id, null,
						new PSSMemberPathElemExpression(index, PSSScope.Context.MEMBER_CONSTRAINT),
						PSSScope.Context.MEMBER_CONSTRAINT);
				root.addConstraint(new PSSForeachConstraint(null,
						new PSSMemberPathElemExpression(id, PSSScope.Context.MEMBER_CONSTRAINT), index,
						domainConstraints(elem_type, element)));
			}

			PSSDataInstModel inst = new PSSDataInstModel(id, array_dim, const_expression);
			data_decl.addDataInst(inst);

		}
		return data_decl;
	}

	/** The constraints that keep target in the domain of type. */
	private static List<PSSConstraint> domainConstraints(TypeSpec type, PSSExpression target) {
		List<PSSConstraint> constraints = new ArrayList<PSSConstraint>();
		if (type.ranges != null)
			constraints.add(new PSSExpressionConstraint(new PSSInExpression(target, type.ranges)));
		for (PSSExpression item : type.open_ended) {
			item.setLeftExpression(target);
			constraints.add(new PSSExpressionConstraint(item));
		}
		return constraints;
	}

	/** True when ctx is inside a branch of a compile if of the type that declares it. */
	private static boolean inCompileIf(ParserRuleContext ctx) {
		for (ParserRuleContext p = ctx.getParent(); p != null; p = p.getParent()) {
			if (p instanceof PSSParser.Action_declarationContext || p instanceof PSSParser.Struct_declarationContext
					|| p instanceof PSSParser.Component_declarationContext || p instanceof PSSParser.Package_declarationContext)
				return false;
			if (p instanceof PSSParser.Package_body_compile_if_itemContext
					|| p instanceof PSSParser.Action_body_compile_if_itemContext
					|| p instanceof PSSParser.Component_body_compile_if_itemContext
					|| p instanceof PSSParser.Struct_body_compile_if_itemContext)
				return true;
		}
		return false;
	}

	/** The identifier when ctx is a name without qualifier, path, index or call; else null. */
	private static String bareIdentifier(PSSParser.ExpressionContext ctx) {
		if (ctx.primary() == null || ctx.primary().ref_path() == null)
			return null;
		String text = ctx.primary().ref_path().getText();
		return text.matches("[A-Za-z_][A-Za-z0-9_]*") ? text : null;
	}

	/**
	 * PSS 2.0 Section 4.7: the characters of a quoted string literal, each escape
	 * sequence of Table 3 converted to the one character it stands for.
	 *
	 * @param text the literal with its quotes
	 * @return the string without the quotes
	 */
	private static String quotedString(String text) {
		String body = text.substring(1, text.length() - 1);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < body.length(); i++) {
			char c = body.charAt(i);
			if (c != '\\') {
				sb.append(c);
				continue;
			}
			// the lexer puts a character after every backslash
			char e = body.charAt(++i);
			switch (e) {
			case '\'': case '"': case '?': case '\\': sb.append(e); break;
			case 'a': sb.append((char) 0x07); break;
			case 'b': sb.append((char) 0x08); break;
			case 'f': sb.append((char) 0x0C); break;
			case 'n': sb.append((char) 0x0A); break;
			case 'r': sb.append((char) 0x0D); break;
			case 't': sb.append((char) 0x09); break;
			case 'v': sb.append((char) 0x0B); break;
			default:
				if (i + 2 < body.length() && isOctal(e) && isOctal(body.charAt(i + 1)) && isOctal(body.charAt(i + 2))) {
					sb.append((char) Integer.parseInt(body.substring(i, i + 3), 8));
					i += 2;
				} else {
					PSSMessage.Error("PSS 2.0 Section 4.7", "illegal escape sequence '\\" + e + "' in string literal " + text);
				}
			}
		}
		return sb.toString();
	}

	private static boolean isOctal(char c) {
		return c >= '0' && c <= '7';
	}
}
