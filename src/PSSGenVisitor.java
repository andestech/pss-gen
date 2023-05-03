import java.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;


public class PSSGenVisitor extends PSSBaseVisitor<Integer> {
	PSSModel root;
	Stack<PSSExpression> exp_stack;
	Stack<PSSActivity> activity_stack;
	ArrayList<PSSProcStmt> proc_stmt_list;
	ArrayList<PSSConstraint> constraint_list;
	PSSModel cur_data_type;
	PSSDataDeclModel cur_data_decl;
	PSSVal cur_val;

	PSSGenVisitor() {
		exp_stack = new Stack<PSSExpression>();
		activity_stack = new Stack<PSSActivity>();
		proc_stmt_list = new ArrayList<PSSProcStmt>();
		constraint_list = new ArrayList<PSSConstraint>();
	}
	@Override
	public Integer visitPackage_declaration(PSSParser.Package_declarationContext ctx) {
		String id = ctx.package_id_path().getText();
		PSSPackageModel node = new PSSPackageModel(id);
		root.addChild(node);
		root = node;

		for (int i=0; i<ctx.package_body_item().size(); i++) {
			visit(ctx.package_body_item(i));
		}

		root = node.m_parent;

		return 0;
	}
	@Override
	public Integer visitImport_stmt(PSSParser.Import_stmtContext ctx) {
		String type_id = ctx.package_import_pattern().type_identifier().getText();

		PSSImportStmtModel node = new PSSImportStmtModel(type_id);
		root.addChild(node);

		return 0;
	}

	@Override
	public Integer visitExtend_stmt(PSSParser.Extend_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitComponent_declaration(PSSParser.Component_declarationContext ctx) {
		String id = ctx.component_identifier().getText();

		PSSComponentModel node = new PSSComponentModel(id);

		root.addChild(node);
		root = node;

		for (int i=0; i<ctx.component_body_item().size(); i++) {
			visit(ctx.component_body_item(i));
		}
		
		root = node.m_parent;

		return 0;
	}
	@Override
	public Integer visitOverride_declaration(PSSParser.Override_declarationContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitComponent_pool_declaration(PSSParser.Component_pool_declarationContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	};
	@Override
	public Integer visitComponent_data_declaration(PSSParser.Component_data_declarationContext ctx) {

		visit(ctx.data_declaration());

		PSSComponentDataDeclModel data_decl = new PSSComponentDataDeclModel(cur_data_decl);
		root.addComponentDataDecl(data_decl);

		return 0;
	}

	@Override
	public Integer visitEnum_declaration(PSSParser.Enum_declarationContext ctx) {
		String enum_id = ctx.enum_identifier().getText();
		PSSEnumModel node = new PSSEnumModel(enum_id);
		root.addChild(node);
		root = node;

		for (int i=0; i<ctx.enum_item().size(); i++) {
			visit(ctx.enum_item(i));
		}

		root = node.m_parent;
		return 0;
	}
	@Override
	public Integer visitEnum_item(PSSParser.Enum_itemContext ctx) {
		String id = ctx.identifier().getText();

		PSSEnumModel enum_model = (PSSEnumModel)root;

		if (ctx.constant_expression() != null) {
			visit(ctx.constant_expression());
			PSSExpression expression = exp_stack.pop();
			PSSVal val = expression.eval(null);
			enum_model.addEnumItem(id, val);
		}
		else {
			enum_model.addEnumItem(id);
		}

		return visitChildren(ctx);
	}

	@Override
	public Integer visitProcedural_function(PSSParser.Procedural_functionContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}
	@Override
	public Integer visitCovergroup_declaration(PSSParser.Covergroup_declarationContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitTypedef_declaration(PSSParser.Typedef_declarationContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitAction_declaration(PSSParser.Action_declarationContext ctx) {
		String id = ctx.action_identifier().getText();

		PSSAction node = new PSSAction(id);
		root.addChild(node);
		root = node;

		for (int i=0; i<ctx.action_body_item().size(); i++) {
			visit(ctx.action_body_item(i));
		}

		root = node.m_parent;

		return 0;
	}

	@Override
	public Integer visitFlow_ref_field_declaration(PSSParser.Flow_ref_field_declarationContext ctx) {
		String direction = ctx.input_or_output().getText();
		String type = ctx.flow_object_type().getText();
		for (int i=0; i<ctx.object_ref_field().size(); i++) {
			String id = ctx.object_ref_field(i).getText();
			PSSFlowRef node = new PSSFlowRef(id, direction.equals("output"), type);
			root.addFlowRef(node);
		}
		return 0;
	}

	@Override
	public Integer visitStruct_declaration(PSSParser.Struct_declarationContext ctx) {
		String id = ctx.struct_identifier().getText();

		PSSStructModel node = new PSSStructModel(id);
		root.addChild(node);
		root = node;

		for (int i=0; i<ctx.struct_body_item().size(); i++) {
			visit(ctx.struct_body_item(i));
		}

		root = node.m_parent;

		return 0;
	}

	@Override
	public Integer visitExec_block(PSSParser.Exec_blockContext ctx) {
		String kind = ctx.exec_kind().getText();

		PSSExecBlock block = new PSSExecBlock(kind);

		for (int i=0; i<ctx.exec_stmt().size(); i++) {
			visit(ctx.exec_stmt(i));

			PSSProcStmt stmt = proc_stmt_list.remove(0);
			block.addStmt(stmt);
		}

		root.addExecBlock(block);

		return 0;

	}

	@Override
	public Integer visitExec_stmt(PSSParser.Exec_stmtContext ctx) {

		if (ctx.procedural_stmt() != null) {
			visit(ctx.procedural_stmt());
		}
		else {
			visit(ctx.exec_super_stmt());
			
		}

		return 0;
	}
	@Override
	public Integer visitExec_super_stmt(PSSParser.Exec_super_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitProcedural_assignment_stmt(PSSParser.Procedural_assignment_stmtContext ctx) {

		visit(ctx.ref_path());
		PSSRefPathExpression ref_exp = (PSSRefPathExpression) exp_stack.pop();

		String op = ctx.assign_op().getText();

		visit(ctx.expression());
		PSSExpression expression = exp_stack.pop();

		PSSAssignProcStmt stmt = new PSSAssignProcStmt(ref_exp, op, expression);
		proc_stmt_list.add(stmt);
		return 0;
	}

	@Override
	public Integer visitProcedural_sequence_block_stmt(PSSParser.Procedural_sequence_block_stmtContext ctx) {
		PSSSequenceProcStmt sequence_stmt = new PSSSequenceProcStmt();

		for (int i=0; i<ctx.procedural_stmt().size(); i++) {
			visit(ctx.procedural_stmt(i));

			PSSProcStmt stmt = proc_stmt_list.remove(0);
			sequence_stmt.add(stmt);
		}
		proc_stmt_list.add(sequence_stmt);
		return 0;
	}
	@Override
	public Integer visitData_type(PSSParser.Data_typeContext ctx) {
		if (ctx.scalar_data_type() != null) {
			visit(ctx.scalar_data_type());
		}
		else if (ctx.collection_type() != null) {
			visit(ctx.collection_type());
		}
		else if (ctx.reference_type() != null) {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		}
		else {
			String  text = ctx.type_identifier().getText();
			cur_data_type = new PSSDataTypeModel(text);
		}

		return 0;
	}

	@Override
	public Integer visitScalar_data_type(PSSParser.Scalar_data_typeContext ctx) {
		if (ctx.chandle_type() != null) {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		}
		else if (ctx.integer_type() != null) {
			visit(ctx.integer_type());
		}
		else if (ctx.string_type() != null) {
			visit(ctx.string_type());
		}
		else if (ctx.enum_type() != null) {
			String  text = ctx.enum_type().getText();
			cur_data_type = new PSSDataTypeModel(text);
		}

		return 0;
	}
	@Override
	public Integer visitArray_type(PSSParser.Array_typeContext ctx) {
		visit(ctx.data_type());
		PSSModel data_model = cur_data_type;

		visit(ctx.array_size_expression());
		PSSExpression size_exp = exp_stack.pop();

		cur_data_type = new PSSArrayModel(data_model, size_exp);
		return 0;
	}

	@Override
	public Integer visitList_type(PSSParser.List_typeContext ctx) {
		visit(ctx.data_type());
		PSSModel data_model = cur_data_type;

		cur_data_type = new PSSListModel(data_model);
		return 0;
	}

	@Override
	public Integer visitMap_type(PSSParser.Map_typeContext ctx) {
        visit(ctx.data_type(0));
        PSSModel key_data_type = cur_data_type;
        visit(ctx.data_type(1));
        PSSModel val_data_type = cur_data_type;

        cur_data_type = new PSSMapModel(key_data_type, val_data_type);
		return 0;
	}

	@Override
	public Integer visitSet_type(PSSParser.Set_typeContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitInteger_type(PSSParser.Integer_typeContext ctx) {
		// bit[7]	// [constant_expression]
		// bit[3:0]	// [constant_expression (':' number)? ]
		
		if (ctx.domain_open_range_list() != null) {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		}

		String integer_atom_type = ctx.integer_atom_type().getText();
		Integer width;
		boolean sign = integer_atom_type.equals("int");

		if (ctx.number() != null) {
			int lsb = Integer.valueOf(ctx.number().getText());
			int msb = Integer.valueOf(ctx.constant_expression().getText());
			width = msb-lsb+1;
		}
		else if (ctx.constant_expression() != null) {
			width = Integer.valueOf(ctx.constant_expression().getText());
		}
		else {
			if (sign) {
				width = 32;
			}
			else {
				width = 1;
			}
		}

		cur_data_type = new PSSIntModel(width, sign);

		return 0;
	}

	@Override
	public Integer visitString_type(PSSParser.String_typeContext ctx) {
		cur_data_type = new PSSStringModel();
		return 0;
	}

	@Override
	public Integer visitBool_type(PSSParser.Bool_typeContext ctx) {
		cur_data_type = new PSSBoolModel();
		return 0;
	}

	@Override
	public Integer visitProcedural_data_declaration(PSSParser.Procedural_data_declarationContext ctx) {
		visit(ctx.data_type());
		PSSModel type = cur_data_type;

		PSSDataDeclProcStmt data_decl = new PSSDataDeclProcStmt(type);
		for (int i=0; i<ctx.procedural_data_instantiation().size(); i++) {
			PSSParser.Procedural_data_instantiationContext ctx_data_inst = ctx.procedural_data_instantiation(i);

			String id = ctx_data_inst.identifier().getText();
			PSSExpression expression = null;

			if (ctx_data_inst.expression() != null) {
				visit(ctx_data_inst.expression());
				expression = exp_stack.pop();
			}

			PSSDataInstProcStmt stmt = new PSSDataInstProcStmt(id, expression);
			data_decl.addInst(stmt);

		}
		proc_stmt_list.add(data_decl);
		return 0;
	}

	@Override
	public Integer visitProcedural_void_function_call_stmt(PSSParser.Procedural_void_function_call_stmtContext ctx) {
		visit(ctx.function_call());
		PSSExpression func = exp_stack.pop();
		if (!(func instanceof PSSFunctionCall))
			PSSMessage.Fatal("Return type of function_call is not PSSFunctionCall.");
		PSSVoidFunctionCall stmt = new PSSVoidFunctionCall((PSSFunctionCall)func);
		proc_stmt_list.add(stmt);
		return 0;
	}

	@Override
	public Integer visitProcedural_return_stmt(PSSParser.Procedural_return_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitProcedural_repeat_stmt(PSSParser.Procedural_repeat_stmtContext ctx) {
		visit(ctx.expression());
		PSSExpression cond = exp_stack.pop();

		visit(ctx.procedural_stmt());	
		PSSProcStmt stmt = proc_stmt_list.remove(0);

		PSSRepeatProcStmt repeat_stmt = new PSSRepeatProcStmt(cond, stmt);
		proc_stmt_list.add(repeat_stmt);
		return 0;
	}

	@Override
	public Integer visitProcedural_repeat_while_stmt(PSSParser.Procedural_repeat_while_stmtContext ctx) {
		visit(ctx.expression());
		PSSExpression cond = exp_stack.pop();

		visit(ctx.procedural_stmt());	
		PSSProcStmt stmt = proc_stmt_list.remove(0);

		PSSRepeatWhileProcStmt repeat_while_stmt = new PSSRepeatWhileProcStmt(cond, stmt);
		proc_stmt_list.add(repeat_while_stmt);
		return 0;
	}

	@Override
	public Integer visitProcedural_while_stmt(PSSParser.Procedural_while_stmtContext ctx) {
		visit(ctx.expression());
		PSSExpression cond = exp_stack.pop();

		visit(ctx.procedural_stmt());	
		PSSProcStmt stmt = proc_stmt_list.remove(0);

		PSSWhileProcStmt while_stmt = new PSSWhileProcStmt(cond, stmt);
		proc_stmt_list.add(while_stmt);
		return 0;
	}

	@Override
	public Integer visitProcedural_foreach_stmt(PSSParser.Procedural_foreach_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}
	@Override
	public Integer visitProcedural_if_else_stmt(PSSParser.Procedural_if_else_stmtContext ctx) {
		visit(ctx.expression());
		PSSExpression cond = exp_stack.pop();

		visit(ctx.procedural_stmt(0));	
		PSSProcStmt true_path = proc_stmt_list.remove(0);
		PSSProcStmt false_path = null;

		if (ctx.procedural_stmt().size() == 2) {
			visit(ctx.procedural_stmt(1));	
			false_path = proc_stmt_list.remove(0);
		}

		PSSIfElseProcStmt if_else_stmt = new PSSIfElseProcStmt(cond, true_path, false_path);
		proc_stmt_list.add(if_else_stmt);
		return 0;
	}

	@Override
	public Integer visitProcedural_match_stmt(PSSParser.Procedural_match_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitProcedural_break_stmt(PSSParser.Procedural_break_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitProcedural_continue_stmt(PSSParser.Procedural_continue_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitTarget_code_exec_block(PSSParser.Target_code_exec_blockContext ctx) {
		String language = ctx.language_identifier().getText();
		String exec_kind = ctx.exec_kind().getText();
		String code = ctx.string_literal().getText();
		PSSTargetCode node = new PSSTargetCode(root, exec_kind, language, code);


		((PSSAction)root).addTargetCode(node);
		return 0;
	}

	@Override
	public Integer visitActivity_declaration(PSSParser.Activity_declarationContext ctx) {
		for (int i=0; i<ctx.activity_declaration_body_stmt().size(); i++) {
			visit(ctx.activity_declaration_body_stmt(i));
			PSSActivity stmt = activity_stack.pop();
			if (stmt != null) {
				((PSSAction)root).addActivityStmt(stmt);
			}
		}
		return 0;
	}
	@Override
	public Integer visitActivity_action_traversal_stmt(PSSParser.Activity_action_traversal_stmtContext ctx) {
		String handle_id = "";
		String type_id = "";

		if (ctx.identifier() != null) {	// anonymous
			handle_id = ctx.identifier().getText();
		}
		else {
			type_id = ctx.type_identifier().getText();
		}


		PSSTraversalActivity node = new PSSTraversalActivity(handle_id, type_id);
		if (ctx.inline_constraints_or_empty().constraint_set() != null) {
			visit(ctx.inline_constraints_or_empty().constraint_set());

			for (int i=0; i<constraint_list.size(); i++) {
				PSSConstraint c = constraint_list.get(i);
				node.addConstraint(c);
			}
			constraint_list.clear();
			activity_stack.push(node);
		}
		activity_stack.push(node);


		return 0;
	}

	@Override
	public Integer visitSymbol_declaration(PSSParser.Symbol_declarationContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	public Integer visitConstraint_declaration(PSSParser.Constraint_declarationContext ctx) {

		visit(ctx.constraint_set());

		for (int i=0; i<constraint_list.size(); i++) {
			PSSConstraint c = constraint_list.get(i);
			root.addConstraint(c);
		}
		constraint_list.clear();
		return 0;
	}

	@Override
	public Integer visitExpression_constraint_item(PSSParser.Expression_constraint_itemContext ctx) {
		visit(ctx.expression());
		PSSExpression exp = exp_stack.pop();

		PSSExpressionConstraint item = new PSSExpressionConstraint(exp);

		constraint_list.add(item);
		return 0;
	}

	@Override
	public Integer visitForeach_constraint_item(PSSParser.Foreach_constraint_itemContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitForall_constraint_item(PSSParser.Forall_constraint_itemContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}
	@Override
	public Integer visitIf_constraint_item(PSSParser.If_constraint_itemContext ctx) {
		visit(ctx.expression());

		PSSExpression exp = exp_stack.pop();
		PSSIfConstraint constraint = new PSSIfConstraint(exp);

		visit(ctx.constraint_set(0));
		for (int i=0; i<constraint_list.size(); i++) {
			PSSConstraint c = constraint_list.get(i);
			constraint.addTrueConstraint(c);
		}
		constraint_list.clear();

		if (ctx.constraint_set().size() > 1) {
			visit(ctx.constraint_set(1));
			for (int i=0; i<constraint_list.size(); i++) {
				PSSConstraint c = constraint_list.get(i);
				constraint.addFalseConstraint(c);
			}
			constraint_list.clear();
		}

		constraint_list.add(constraint);
		
		return 0;
	}
	@Override
	public Integer visitImplication_constraint_item(PSSParser.Implication_constraint_itemContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}
	@Override
	public Integer visitUnique_constraint_item(PSSParser.Unique_constraint_itemContext ctx) {
		PSSUniqueConstraint constraint = new PSSUniqueConstraint();
		
		for (int i=0; i<ctx.hierarchical_id_list().hierarchical_id().size(); i++) {
			visit(ctx.hierarchical_id_list().hierarchical_id(i));
			constraint.add((PSSHierarchicalIDExpression)exp_stack.pop());
		}

		constraint_list.add(constraint);
		return 0;
	}

	@Override
	public Integer visitExpression(PSSParser.ExpressionContext ctx) {
		if (ctx.binary_operator() != null) {
			visit(ctx.expression(0));
			visit(ctx.expression(1));
			PSSExpression right = exp_stack.pop();
			PSSExpression left = exp_stack.pop();
			String op = ctx.binary_operator().getText();
			

			switch (op) {
			case "==":
				exp_stack.push(new PSSEqualExpression(left, right));
				break;
			case "!=":
				exp_stack.push(new PSSNotEqualExpression(left, right));
				break;
			case ">":
				exp_stack.push(new PSSGreaterThanExpression(left, right));
				break;
			case ">=":
				exp_stack.push(new PSSGreaterEqualExpression(left, right));
				break;
			case "<":
				exp_stack.push(new PSSLessThanExpression(left, right));
				break;
			case "<=":
				exp_stack.push(new PSSLessEqualExpression(left, right));
				break;
			case "+":
				exp_stack.push(new PSSAddExpression(left, right));
				break;
			case "-":
				exp_stack.push(new PSSSubExpression(left, right));
				break;
			case "*":
				exp_stack.push(new PSSMulExpression(left, right));
				break;
			case "/":
				exp_stack.push(new PSSDivExpression(left, right));
				break;
			case "%":
				exp_stack.push(new PSSModExpression(left, right));
				break;
			case "<<":
				exp_stack.push(new PSSLeftShiftExpression(left, right));
				break;
			case ">>":
				exp_stack.push(new PSSRightShiftExpression(left, right));
				break;
			case "&&":
				exp_stack.push(new PSSLogicalAndExpression(left, right));
				break;
			case "||":
				exp_stack.push(new PSSLogicalOrExpression(left, right));
				break;
			case "&":
				exp_stack.push(new PSSBitwiseAndExpression(left, right));
				break;
			case "|":
				exp_stack.push(new PSSBitwiseOrExpression(left, right));
				break;
			default:
				exp_stack.push(new PSSBinaryExpression(left, op, right));
				break;
			}

		}
		else if (ctx.unary_operator() != null) {
			String op = ctx.unary_operator().getText();
			visit(ctx.primary());
			PSSExpression exp = exp_stack.pop();

			switch (op) {
			case "!":
				exp_stack.push(new PSSLogicalNotExpression(exp));
				break;
			case "~":
				exp_stack.push(new PSSBitwiseNotExpression(exp));
				break;
			case "-":
				exp_stack.push(new PSSUnaryMinusExpression(exp));
				break;
			default:
				PSSMessage.Fatal("unary operator '" + op + "'is not yet supported");
				break;
			}
		}
		else if (ctx.primary() != null) {
			visit(ctx.primary());
		}
		else if (ctx.open_range_list() != null) {
			visit(ctx.expression(0));
			visit(ctx.open_range_list());
			PSSExpression right = exp_stack.pop();
			PSSExpression left = exp_stack.pop();
			exp_stack.push(new PSSInExpression(left, right));
		}
		else {
			PSSMessage.Error("VISITOR-0001", "visitExpression is not fully implemented yet");
		}
		return 0;
	}
	@Override
	public Integer visitPrimary(PSSParser.PrimaryContext ctx) {

		if (ctx.string_literal() != null) {
			String text = ctx.getText().replace("\"", "");
			exp_stack.push(new PSSPrimaryExpression(new PSSStringVal(text)));
		}
		else if (ctx.number() != null) {
			String text = ctx.getText();
			if (ctx.number().OCT_NUMBER() != null) {
				exp_stack.push(new PSSPrimaryExpression(PSSNumber.newOctNumber(text)));
			}
			else if (ctx.number().DEC_NUMBER() != null) {
				exp_stack.push(new PSSPrimaryExpression(PSSNumber.newDecNumber(text)));
			}
			else if (ctx.number().HEX_NUMBER() != null) {
				exp_stack.push(new PSSPrimaryExpression(PSSNumber.newHexNumber(text)));
			}
			else if (ctx.number().based_bin_number() != null) {
				exp_stack.push(new PSSPrimaryExpression(PSSNumber.newBasedBinNumber(text)));
			}
			else if (ctx.number().based_oct_number() != null) {
				exp_stack.push(new PSSPrimaryExpression(PSSNumber.newBasedOctNumber(text)));
			}
			else if (ctx.number().based_dec_number() != null) {
				exp_stack.push(new PSSPrimaryExpression(PSSNumber.newBasedDecNumber(text)));
			}
			else if (ctx.number().based_hex_number() != null) {
				exp_stack.push(new PSSPrimaryExpression(PSSNumber.newBasedHexNumber(text)));
			}
		}
		else if (ctx.bool_literal() != null) {
			String text = ctx.getText();
			exp_stack.push(new PSSPrimaryExpression(new PSSBoolVal(text)));
		}
		else if (ctx.ref_path() != null) {
            visit(ctx.ref_path());
		}
		else if (ctx.paren_expr() != null) {
			visit(ctx.paren_expr().expression());
		}
		else if (ctx.aggregate_literal() != null) {
			visit(ctx.aggregate_literal());
		}
		else {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		}
		return 0;
	}

    @Override
    public Integer visitMember_path_elem(PSSParser.Member_path_elemContext ctx) {
		String id = ctx.identifier().getText();
		List<PSSExpression> args = null;
		PSSExpression index = null;
        if (ctx.function_parameter_list() != null) {
			args = new ArrayList<PSSExpression>();
            for (int i=0; i<ctx.function_parameter_list().expression().size(); i++) {
                visit(ctx.function_parameter_list().expression(i));
				args.add(exp_stack.pop());
            }
        }
		if (ctx.expression() != null) {
			visit(ctx.expression());
			index = exp_stack.pop();
		}

		PSSMemberPathElemExpression e = new PSSMemberPathElemExpression(id, args, index);
        exp_stack.push(e);

        return 0;
    }

    @Override
    public Integer visitHierarchical_id(PSSParser.Hierarchical_idContext ctx) {
        PSSHierarchicalIDExpression e = new PSSHierarchicalIDExpression();
        for (int i=0; i<ctx.member_path_elem().size(); i++) {
            visit(ctx.member_path_elem(i));
            e.addMemberElement((PSSMemberPathElemExpression)exp_stack.pop());
        }
        exp_stack.push(e);

        return 0;
    }

    @Override
    public Integer visitRef_path(PSSParser.Ref_pathContext ctx) {
        if (ctx.static_ref_path() == null)
			PSSMessage.Fatal("super in ref_path is not implemented");

		String type_identifier_elem = null;
		if (ctx.static_ref_path().type_identifier_elem() != null) {
			List<String> strs = new ArrayList<String>();
			for (int i = 0; i < ctx.static_ref_path().type_identifier_elem().size(); i++) {
				strs.add(ctx.static_ref_path().type_identifier_elem(i).getText() + "::");
			}
			type_identifier_elem = String.join("", strs);
		}

		visit(ctx.static_ref_path().member_path_elem());
		PSSMemberPathElemExpression static_ref_path = (PSSMemberPathElemExpression) exp_stack.pop();

		PSSHierarchicalIDExpression hierarchical_id = null;
        if (ctx.hierarchical_id() != null) {
            visit(ctx.hierarchical_id());
			hierarchical_id = (PSSHierarchicalIDExpression) exp_stack.pop();
        }

		PSSExpression bit_slice_from = null;
		PSSExpression bit_slice_to = null;
		if (ctx.bit_slice() != null) {
			visit(ctx.bit_slice().constant_expression(0));
			bit_slice_from = exp_stack.pop();
			visit(ctx.bit_slice().constant_expression(1));
			bit_slice_to = exp_stack.pop();
		}

        PSSRefPathExpression e = new PSSRefPathExpression(type_identifier_elem, static_ref_path, hierarchical_id, null, null);
        exp_stack.push(e);

        return 0;
    }

	@Override
	public Integer visitAggregate_literal(PSSParser.Aggregate_literalContext ctx) {
		PSSExpression res = null;
		if (ctx.value_list_literal() != null) {
			PSSAggregateExpression exp = new PSSAggregateExpression();
			for (int i=0; i<ctx.value_list_literal().expression().size(); i++) {
				visit(ctx.value_list_literal().expression(i));
				PSSExpression item = exp_stack.pop();
				exp.addExpression(item);
			}
			res = exp;
		}
        else if (ctx.map_literal() != null) {
			PSSMapExpression exp = new PSSMapExpression();
            for (int i=0; i<ctx.map_literal().map_literal_item().size(); i++) {
                visit(ctx.map_literal().map_literal_item(i).expression(0));
                PSSExpression key = exp_stack.pop();
                visit(ctx.map_literal().map_literal_item(i).expression(1));
                PSSExpression val = exp_stack.pop();
				exp.add(key, val);
            }
			res = exp;
        }
		else {
			PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		}
		if (res != null)
			exp_stack.push(res);
		return 0;
	}

	@Override
	public Integer visitOpen_range_list(PSSParser.Open_range_listContext ctx) {
		PSSOpenRangeListExpression open_range_list = new PSSOpenRangeListExpression();

		for (int i=0; i<ctx.open_range_value().size(); i++) {
			
			visit(ctx.open_range_value(i));
			PSSOpenRangeValueExpression value = (PSSOpenRangeValueExpression) exp_stack.pop();

			open_range_list.addOpenRangeValueExpression(value);
			
		}

		exp_stack.push(open_range_list);

		return 0;
	}

	@Override
	public Integer visitOpen_range_value(PSSParser.Open_range_valueContext ctx) {
		if (ctx.expression().size() == 2) {
			visit(ctx.expression(0));
			visit(ctx.expression(1));
			PSSExpression end = exp_stack.pop();
			PSSExpression begin = exp_stack.pop();
			PSSOpenRangeValueExpression item = new PSSOpenRangeValueExpression(begin, end);
			exp_stack.push(item);
		}
		else {
			visit(ctx.expression(0));
			PSSExpression begin = exp_stack.pop();
			PSSOpenRangeValueExpression item = new PSSOpenRangeValueExpression(begin);
			exp_stack.push(item);
		}

		return 0;
	}

	@Override
	public Integer visitFunction_call(PSSParser.Function_callContext ctx) {
		visit(ctx.function_ref_path());
		return 0;
	}

	@Override
	public Integer visitFunction_ref_path(PSSParser.Function_ref_pathContext ctx) {
		String path = "";
		String id = ctx.identifier().getText();

		for (int i=0; i<ctx.member_path_elem().size(); i++) {
			path = path + ctx.member_path_elem(i).getText() + ".";
		}

		List<PSSExpression> args = new ArrayList<PSSExpression>();
		List<PSSParser.ExpressionContext> args_ctx = ctx.function_parameter_list().expression();
		for (int i = 0; i < args_ctx.size(); i++) {
			visitExpression(args_ctx.get(i));
			args.add(exp_stack.pop());
		}

		PSSFunctionCall func_call = new PSSFunctionCall(path, id, args);
		exp_stack.push(func_call);
		//PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitActivity_bind_stmt(PSSParser.Activity_bind_stmtContext ctx) {
		String id = ctx.hierarchical_id().getText();

		PSSBindActivity node = new PSSBindActivity(id);

		activity_stack.push(node);

		// FIXME
		// activity_bind_item_or_list
		// 	: hierarchical_id
		// 	| '{' hierarchical_id_list '}'
		// ;
		String item = ctx.activity_bind_item_or_list().getText();
		node.addBindItem(item);

		return 0;
	}
	@Override
	public Integer visitActivity_if_else_stmt(PSSParser.Activity_if_else_stmtContext ctx) {
		visit(ctx.expression());
		PSSExpression cond = exp_stack.pop();

		PSSIfElseActivity if_else_stmt = new PSSIfElseActivity(cond);

		visit(ctx.activity_stmt(0));	
		PSSActivity true_stmt = activity_stack.pop();
		if_else_stmt.addTrueStmt(true_stmt);

		if (ctx.activity_stmt().size() == 2) {
			visit(ctx.activity_stmt(1));	
			PSSActivity false_stmt = activity_stack.pop();
			if_else_stmt.addFalseStmt(false_stmt);
		}

		activity_stack.push(if_else_stmt);
		return 0;
	}
	@Override
	public Integer visitActivity_repeat_stmt(PSSParser.Activity_repeat_stmtContext ctx) {
		String index_id = "";

		if (ctx.index_identifier() != null) {
			index_id = ctx.index_identifier().getText();
		}

		visit(ctx.expression());
		PSSExpression count = exp_stack.pop();

		visit(ctx.activity_stmt());	
		PSSActivity stmt = activity_stack.pop();

		PSSRepeatActivity repeat_stmt = new PSSRepeatActivity(count, stmt, index_id);
		activity_stack.push(repeat_stmt);
		return 0;
	}
	@Override
	public Integer visitActivity_sequence_block_stmt(PSSParser.Activity_sequence_block_stmtContext ctx) {
		PSSSequenceActivity sequence_stmt = new PSSSequenceActivity();
		for (int i=0; i<ctx.activity_stmt().size(); i++) {
			visit(ctx.activity_stmt(i));
			PSSActivity stmt = activity_stack.pop();
			sequence_stmt.addActivity(stmt);
			
		}
		activity_stack.push(sequence_stmt);
		return 0;
	}

	@Override
	public Integer visitActivity_parallel_stmt(PSSParser.Activity_parallel_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitActivity_schedule_stmt(PSSParser.Activity_schedule_stmtContext ctx) {
		PSSScheduleActivity schedule_stmt = new PSSScheduleActivity();
		for (int i=0; i<ctx.activity_stmt().size(); i++) {
			visit(ctx.activity_stmt(i));
			PSSActivity stmt = activity_stack.pop();
			schedule_stmt.addActivity(stmt);
		}
		activity_stack.push(schedule_stmt);
		return 0;
	}

	@Override
	public Integer visitActivity_foreach_stmt(PSSParser.Activity_foreach_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	};

	@Override
	public Integer visitActivity_select_stmt(PSSParser.Activity_select_stmtContext ctx) {
		PSSSelectActivity select_stmt = new PSSSelectActivity();

		for (int i=0; i<ctx.select_branch().size(); i++) {
			visit(ctx.select_branch(i));
			PSSSelectBranchActivity select_branch = (PSSSelectBranchActivity) activity_stack.pop();
			select_stmt.addBranch(select_branch);
		}


		activity_stack.push(select_stmt);
		return 0;
	}

	@Override
	public Integer visitSelect_branch(PSSParser.Select_branchContext ctx) {
		PSSExpression cond = null;
		PSSExpression weight = null;

		if (ctx.select_branch_cond() != null) {
			visit(ctx.select_branch_cond());
			cond = exp_stack.pop();
		}

		if (ctx.select_branch_weight() != null) {
			visit(ctx.select_branch_weight());
			weight = exp_stack.pop();
		}

		visit(ctx.activity_stmt());
		PSSActivity stmt = activity_stack.pop();

		PSSSelectBranchActivity branch = new PSSSelectBranchActivity(stmt, cond, weight);
		activity_stack.push(branch);
		return 0;
	}

	@Override
	public Integer visitActivity_match_stmt(PSSParser.Activity_match_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitActivity_replicate_stmt(PSSParser.Activity_replicate_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitActivity_super_stmt(PSSParser.Activity_super_stmtContext ctx) {
		PSSMessage.Fatal("Syntax is not yet supported: '" + ctx.getText() + "'");
		return 0;
	}

	@Override
	public Integer visitAttr_field(PSSParser.Attr_fieldContext ctx) {
		boolean rand = false;
		if (ctx.rand_or_static_const() != null) {
			rand = true;
		}

		visit(ctx.data_declaration());
		PSSAttrField attrfield = new PSSAttrField(rand, cur_data_decl);
		root.addAttrField(attrfield);

		return 0;
	}
	@Override
	public Integer visitData_declaration(PSSParser.Data_declarationContext ctx) {
		visit(ctx.data_type());
		PSSModel data_type = cur_data_type;
		root.addChild(data_type);

		PSSDataDeclModel data_decl = new PSSDataDeclModel(data_type);

		for (int i=0; i<ctx.data_instantiation().size(); i++) {
			PSSParser.Data_instantiationContext data_inst = ctx.data_instantiation(i); 
			
			String id = data_inst.identifier().getText();


			PSSExpression array_dim = null;
			if (data_inst.array_dim() != null) {
				visit(data_inst.array_dim());
				array_dim = exp_stack.pop();
			}

			PSSExpression const_expression = null;
			if (data_inst.constant_expression() != null) {
				visit(data_inst.constant_expression());

				const_expression = exp_stack.pop();
			}

			PSSDataInstModel inst = new PSSDataInstModel(id, array_dim, const_expression);
			data_decl.addDataInst(inst);

		}
		cur_data_decl = data_decl;
		return 0;
	}

}

