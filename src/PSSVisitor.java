// Generated from PSS.g4 by ANTLR 4.9.3
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PSSParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PSSVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PSSParser#model}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel(PSSParser.ModelContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#portable_stimulus_description}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPortable_stimulus_description(PSSParser.Portable_stimulus_descriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#package_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackage_declaration(PSSParser.Package_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#package_id_path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackage_id_path(PSSParser.Package_id_pathContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#package_body_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackage_body_item(PSSParser.Package_body_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#import_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImport_stmt(PSSParser.Import_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#package_import_pattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackage_import_pattern(PSSParser.Package_import_patternContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#package_import_qualifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackage_import_qualifier(PSSParser.Package_import_qualifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#package_import_wildcard}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackage_import_wildcard(PSSParser.Package_import_wildcardContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#package_import_alias}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackage_import_alias(PSSParser.Package_import_aliasContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#extend_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtend_stmt(PSSParser.Extend_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#const_field_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConst_field_declaration(PSSParser.Const_field_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#stmt_terminator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt_terminator(PSSParser.Stmt_terminatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#action_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_declaration(PSSParser.Action_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#abstract_action_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAbstract_action_declaration(PSSParser.Abstract_action_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#action_super_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_super_spec(PSSParser.Action_super_specContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#action_body_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_body_item(PSSParser.Action_body_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_declaration(PSSParser.Activity_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_declaration_body_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_declaration_body_stmt(PSSParser.Activity_declaration_body_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#action_field_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_field_declaration(PSSParser.Action_field_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#object_ref_field_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject_ref_field_declaration(PSSParser.Object_ref_field_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#input_or_output}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInput_or_output(PSSParser.Input_or_outputContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#flow_ref_field_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlow_ref_field_declaration(PSSParser.Flow_ref_field_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#lock_or_share}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLock_or_share(PSSParser.Lock_or_shareContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#resource_ref_field_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResource_ref_field_declaration(PSSParser.Resource_ref_field_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#flow_object_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlow_object_type(PSSParser.Flow_object_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#resource_object_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResource_object_type(PSSParser.Resource_object_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#object_ref_field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject_ref_field(PSSParser.Object_ref_fieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#action_handle_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_handle_declaration(PSSParser.Action_handle_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#action_instantiation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_instantiation(PSSParser.Action_instantiationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_data_field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_data_field(PSSParser.Activity_data_fieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#parallel_or_sequence}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParallel_or_sequence(PSSParser.Parallel_or_sequenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_scheduling_constraint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_scheduling_constraint(PSSParser.Activity_scheduling_constraintContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#struct_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct_declaration(PSSParser.Struct_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#struct_kind}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct_kind(PSSParser.Struct_kindContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#object_kind}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject_kind(PSSParser.Object_kindContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#struct_super_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct_super_spec(PSSParser.Struct_super_specContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#struct_body_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct_body_item(PSSParser.Struct_body_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#exec_block_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExec_block_stmt(PSSParser.Exec_block_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#exec_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExec_block(PSSParser.Exec_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#exec_kind}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExec_kind(PSSParser.Exec_kindContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#exec_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExec_stmt(PSSParser.Exec_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#exec_super_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExec_super_stmt(PSSParser.Exec_super_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#target_code_exec_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTarget_code_exec_block(PSSParser.Target_code_exec_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#target_file_exec_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTarget_file_exec_block(PSSParser.Target_file_exec_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_function(PSSParser.Procedural_functionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#function_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_decl(PSSParser.Function_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#function_prototype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_prototype(PSSParser.Function_prototypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#function_return_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_return_type(PSSParser.Function_return_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#function_parameter_list_prototype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_parameter_list_prototype(PSSParser.Function_parameter_list_prototypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#function_parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_parameter(PSSParser.Function_parameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#function_parameter_dir}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_parameter_dir(PSSParser.Function_parameter_dirContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#varargs_parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarargs_parameter(PSSParser.Varargs_parameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#import_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImport_function(PSSParser.Import_functionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#platform_qualifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlatform_qualifier(PSSParser.Platform_qualifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#target_template_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTarget_template_function(PSSParser.Target_template_functionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#import_class_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImport_class_decl(PSSParser.Import_class_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#import_class_extends}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImport_class_extends(PSSParser.Import_class_extendsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#import_class_function_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImport_class_function_decl(PSSParser.Import_class_function_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#export_action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExport_action(PSSParser.Export_actionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_stmt(PSSParser.Procedural_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_sequence_block_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_sequence_block_stmt(PSSParser.Procedural_sequence_block_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_data_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_data_declaration(PSSParser.Procedural_data_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_data_instantiation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_data_instantiation(PSSParser.Procedural_data_instantiationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_assignment_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_assignment_stmt(PSSParser.Procedural_assignment_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_void_function_call_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_void_function_call_stmt(PSSParser.Procedural_void_function_call_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_return_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_return_stmt(PSSParser.Procedural_return_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_repeat_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_repeat_stmt(PSSParser.Procedural_repeat_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_repeat_while_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_repeat_while_stmt(PSSParser.Procedural_repeat_while_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_while_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_while_stmt(PSSParser.Procedural_while_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_foreach_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_foreach_stmt(PSSParser.Procedural_foreach_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_if_else_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_if_else_stmt(PSSParser.Procedural_if_else_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_match_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_match_stmt(PSSParser.Procedural_match_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_match_choice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_match_choice(PSSParser.Procedural_match_choiceContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_break_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_break_stmt(PSSParser.Procedural_break_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#procedural_continue_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedural_continue_stmt(PSSParser.Procedural_continue_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#component_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComponent_declaration(PSSParser.Component_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#component_super_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComponent_super_spec(PSSParser.Component_super_specContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#component_body_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComponent_body_item(PSSParser.Component_body_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#component_data_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComponent_data_declaration(PSSParser.Component_data_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#component_pool_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComponent_pool_declaration(PSSParser.Component_pool_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#object_bind_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject_bind_stmt(PSSParser.Object_bind_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#object_bind_item_or_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject_bind_item_or_list(PSSParser.Object_bind_item_or_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#object_bind_item_path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject_bind_item_path(PSSParser.Object_bind_item_pathContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#component_path_elem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComponent_path_elem(PSSParser.Component_path_elemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#object_bind_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject_bind_item(PSSParser.Object_bind_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_stmt(PSSParser.Activity_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#labeled_activity_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabeled_activity_stmt(PSSParser.Labeled_activity_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_action_traversal_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_action_traversal_stmt(PSSParser.Activity_action_traversal_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#inline_constraints_or_empty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInline_constraints_or_empty(PSSParser.Inline_constraints_or_emptyContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_sequence_block_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_sequence_block_stmt(PSSParser.Activity_sequence_block_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_parallel_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_parallel_stmt(PSSParser.Activity_parallel_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_schedule_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_schedule_stmt(PSSParser.Activity_schedule_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_join_spec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_join_spec(PSSParser.Activity_join_specContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_join_branch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_join_branch(PSSParser.Activity_join_branchContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_join_select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_join_select(PSSParser.Activity_join_selectContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_join_none}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_join_none(PSSParser.Activity_join_noneContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_join_first}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_join_first(PSSParser.Activity_join_firstContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_repeat_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_repeat_stmt(PSSParser.Activity_repeat_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_foreach_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_foreach_stmt(PSSParser.Activity_foreach_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_select_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_select_stmt(PSSParser.Activity_select_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#select_branch}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_branch(PSSParser.Select_branchContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#select_branch_cond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_branch_cond(PSSParser.Select_branch_condContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#select_branch_weight}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect_branch_weight(PSSParser.Select_branch_weightContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_if_else_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_if_else_stmt(PSSParser.Activity_if_else_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_match_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_match_stmt(PSSParser.Activity_match_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#match_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatch_expression(PSSParser.Match_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#match_choice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatch_choice(PSSParser.Match_choiceContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_replicate_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_replicate_stmt(PSSParser.Activity_replicate_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_super_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_super_stmt(PSSParser.Activity_super_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_bind_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_bind_stmt(PSSParser.Activity_bind_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_bind_item_or_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_bind_item_or_list(PSSParser.Activity_bind_item_or_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#activity_constraint_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivity_constraint_stmt(PSSParser.Activity_constraint_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#symbol_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbol_declaration(PSSParser.Symbol_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#symbol_paramlist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbol_paramlist(PSSParser.Symbol_paramlistContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#symbol_param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbol_param(PSSParser.Symbol_paramContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#override_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOverride_declaration(PSSParser.Override_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#override_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOverride_stmt(PSSParser.Override_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#type_override}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_override(PSSParser.Type_overrideContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#instance_override}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstance_override(PSSParser.Instance_overrideContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#data_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_declaration(PSSParser.Data_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#data_instantiation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_instantiation(PSSParser.Data_instantiationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#array_dim}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_dim(PSSParser.Array_dimContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#rand_or_static_const}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRand_or_static_const(PSSParser.Rand_or_static_constContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#attr_field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttr_field(PSSParser.Attr_fieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#access_modifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAccess_modifier(PSSParser.Access_modifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#attr_group}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttr_group(PSSParser.Attr_groupContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#template_param_decl_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplate_param_decl_list(PSSParser.Template_param_decl_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#template_param_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplate_param_decl(PSSParser.Template_param_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#type_param_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_param_decl(PSSParser.Type_param_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#generic_type_param_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGeneric_type_param_decl(PSSParser.Generic_type_param_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#category_type_param_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCategory_type_param_decl(PSSParser.Category_type_param_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#type_restriction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_restriction(PSSParser.Type_restrictionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#type_category}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_category(PSSParser.Type_categoryContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#value_param_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue_param_decl(PSSParser.Value_param_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#template_param_value_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplate_param_value_list(PSSParser.Template_param_value_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#template_param_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTemplate_param_value(PSSParser.Template_param_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#data_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_type(PSSParser.Data_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#scalar_data_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScalar_data_type(PSSParser.Scalar_data_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#casting_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCasting_type(PSSParser.Casting_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#chandle_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChandle_type(PSSParser.Chandle_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#integer_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInteger_type(PSSParser.Integer_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#integer_atom_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInteger_atom_type(PSSParser.Integer_atom_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#domain_open_range_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDomain_open_range_list(PSSParser.Domain_open_range_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#domain_open_range_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDomain_open_range_value(PSSParser.Domain_open_range_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#string_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_type(PSSParser.String_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#bool_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool_type(PSSParser.Bool_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#enum_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnum_declaration(PSSParser.Enum_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#enum_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnum_item(PSSParser.Enum_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#enum_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnum_type(PSSParser.Enum_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#collection_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCollection_type(PSSParser.Collection_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#array_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_type(PSSParser.Array_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#list_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList_type(PSSParser.List_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#map_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMap_type(PSSParser.Map_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#set_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSet_type(PSSParser.Set_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#array_size_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray_size_expression(PSSParser.Array_size_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#reference_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReference_type(PSSParser.Reference_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#typedef_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypedef_declaration(PSSParser.Typedef_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#constraint_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraint_declaration(PSSParser.Constraint_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#constraint_set}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraint_set(PSSParser.Constraint_setContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#constraint_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraint_block(PSSParser.Constraint_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#constraint_body_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraint_body_item(PSSParser.Constraint_body_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#expression_constraint_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression_constraint_item(PSSParser.Expression_constraint_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#foreach_constraint_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForeach_constraint_item(PSSParser.Foreach_constraint_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#forall_constraint_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForall_constraint_item(PSSParser.Forall_constraint_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#if_constraint_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_constraint_item(PSSParser.If_constraint_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#implication_constraint_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImplication_constraint_item(PSSParser.Implication_constraint_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#unique_constraint_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnique_constraint_item(PSSParser.Unique_constraint_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_declaration(PSSParser.Covergroup_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_port}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_port(PSSParser.Covergroup_portContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_body_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_body_item(PSSParser.Covergroup_body_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_option}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_option(PSSParser.Covergroup_optionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_instantiation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_instantiation(PSSParser.Covergroup_instantiationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#inline_covergroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInline_covergroup(PSSParser.Inline_covergroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_type_instantiation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_type_instantiation(PSSParser.Covergroup_type_instantiationContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_portmap_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_portmap_list(PSSParser.Covergroup_portmap_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_portmap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_portmap(PSSParser.Covergroup_portmapContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_options_or_empty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_options_or_empty(PSSParser.Covergroup_options_or_emptyContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_coverpoint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_coverpoint(PSSParser.Covergroup_coverpointContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#bins_or_empty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBins_or_empty(PSSParser.Bins_or_emptyContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_coverpoint_body_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_coverpoint_body_item(PSSParser.Covergroup_coverpoint_body_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_coverpoint_binspec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_coverpoint_binspec(PSSParser.Covergroup_coverpoint_binspecContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#coverpoint_bins}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCoverpoint_bins(PSSParser.Coverpoint_binsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_range_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_range_list(PSSParser.Covergroup_range_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_value_range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_value_range(PSSParser.Covergroup_value_rangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#bins_keyword}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBins_keyword(PSSParser.Bins_keywordContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_expression(PSSParser.Covergroup_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_cross}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_cross(PSSParser.Covergroup_crossContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#cross_item_or_null}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCross_item_or_null(PSSParser.Cross_item_or_nullContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_cross_body_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_cross_body_item(PSSParser.Covergroup_cross_body_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_cross_binspec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_cross_binspec(PSSParser.Covergroup_cross_binspecContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#package_body_compile_if}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackage_body_compile_if(PSSParser.Package_body_compile_ifContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#package_body_compile_if_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackage_body_compile_if_item(PSSParser.Package_body_compile_if_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#action_body_compile_if}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_body_compile_if(PSSParser.Action_body_compile_ifContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#action_body_compile_if_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_body_compile_if_item(PSSParser.Action_body_compile_if_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#component_body_compile_if}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComponent_body_compile_if(PSSParser.Component_body_compile_ifContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#component_body_compile_if_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComponent_body_compile_if_item(PSSParser.Component_body_compile_if_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#struct_body_compile_if}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct_body_compile_if(PSSParser.Struct_body_compile_ifContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#struct_body_compile_if_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct_body_compile_if_item(PSSParser.Struct_body_compile_if_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#compile_has_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompile_has_expr(PSSParser.Compile_has_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#compile_assert_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompile_assert_stmt(PSSParser.Compile_assert_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#constant_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_expression(PSSParser.Constant_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(PSSParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#unary_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary_operator(PSSParser.Unary_operatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#binary_operator_precedence3}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_precedence3(PSSParser.Binary_operator_precedence3Context ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#binary_operator_precedence4}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_precedence4(PSSParser.Binary_operator_precedence4Context ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#binary_operator_precedence5}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_precedence5(PSSParser.Binary_operator_precedence5Context ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#binary_operator_precedence6}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_precedence6(PSSParser.Binary_operator_precedence6Context ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#binary_operator_precedence7}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_precedence7(PSSParser.Binary_operator_precedence7Context ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#binary_operator_precedence8}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_precedence8(PSSParser.Binary_operator_precedence8Context ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#binary_operator_precedence9}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_precedence9(PSSParser.Binary_operator_precedence9Context ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#binary_operator_precedence10}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_precedence10(PSSParser.Binary_operator_precedence10Context ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#binary_operator_precedence11}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_precedence11(PSSParser.Binary_operator_precedence11Context ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#binary_operator_precedence12}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_precedence12(PSSParser.Binary_operator_precedence12Context ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#binary_operator_precedence13}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinary_operator_precedence13(PSSParser.Binary_operator_precedence13Context ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#assign_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign_op(PSSParser.Assign_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#open_range_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpen_range_list(PSSParser.Open_range_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#open_range_value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpen_range_value(PSSParser.Open_range_valueContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#collection_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCollection_expression(PSSParser.Collection_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(PSSParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#paren_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParen_expr(PSSParser.Paren_exprContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#cast_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCast_expression(PSSParser.Cast_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#ref_path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRef_path(PSSParser.Ref_pathContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#static_ref_path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatic_ref_path(PSSParser.Static_ref_pathContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#bit_slice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBit_slice(PSSParser.Bit_sliceContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#function_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_call(PSSParser.Function_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#function_ref_path}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_ref_path(PSSParser.Function_ref_pathContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#symbol_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbol_call(PSSParser.Symbol_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#function_parameter_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_parameter_list(PSSParser.Function_parameter_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(PSSParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#hierarchical_id_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHierarchical_id_list(PSSParser.Hierarchical_id_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#hierarchical_id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHierarchical_id(PSSParser.Hierarchical_idContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#member_path_elem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMember_path_elem(PSSParser.Member_path_elemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#action_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_identifier(PSSParser.Action_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#component_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComponent_identifier(PSSParser.Component_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covercross_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovercross_identifier(PSSParser.Covercross_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_identifier(PSSParser.Covergroup_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#coverpoint_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCoverpoint_identifier(PSSParser.Coverpoint_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#enum_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnum_identifier(PSSParser.Enum_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#function_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_identifier(PSSParser.Function_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#import_class_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImport_class_identifier(PSSParser.Import_class_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#index_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndex_identifier(PSSParser.Index_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#iterator_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIterator_identifier(PSSParser.Iterator_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#label_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel_identifier(PSSParser.Label_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#language_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLanguage_identifier(PSSParser.Language_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#package_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackage_identifier(PSSParser.Package_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#struct_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct_identifier(PSSParser.Struct_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#symbol_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbol_identifier(PSSParser.Symbol_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#type_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_identifier(PSSParser.Type_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#type_identifier_elem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_identifier_elem(PSSParser.Type_identifier_elemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#action_type_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction_type_identifier(PSSParser.Action_type_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#buffer_type_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBuffer_type_identifier(PSSParser.Buffer_type_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#component_type_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComponent_type_identifier(PSSParser.Component_type_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#covergroup_type_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCovergroup_type_identifier(PSSParser.Covergroup_type_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#enum_type_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnum_type_identifier(PSSParser.Enum_type_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#resource_type_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResource_type_identifier(PSSParser.Resource_type_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#state_type_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitState_type_identifier(PSSParser.State_type_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#stream_type_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStream_type_identifier(PSSParser.Stream_type_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#entity_type_identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntity_type_identifier(PSSParser.Entity_type_identifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(PSSParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#based_bin_number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBased_bin_number(PSSParser.Based_bin_numberContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#based_oct_number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBased_oct_number(PSSParser.Based_oct_numberContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#based_dec_number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBased_dec_number(PSSParser.Based_dec_numberContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#based_hex_number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBased_hex_number(PSSParser.Based_hex_numberContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#aggregate_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregate_literal(PSSParser.Aggregate_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#empty_aggregate_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmpty_aggregate_literal(PSSParser.Empty_aggregate_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#value_list_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue_list_literal(PSSParser.Value_list_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#map_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMap_literal(PSSParser.Map_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#map_literal_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMap_literal_item(PSSParser.Map_literal_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#struct_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct_literal(PSSParser.Struct_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#struct_literal_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStruct_literal_item(PSSParser.Struct_literal_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#bool_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool_literal(PSSParser.Bool_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#null_ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNull_ref(PSSParser.Null_refContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#string_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_literal(PSSParser.String_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link PSSParser#filename_string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilename_string(PSSParser.Filename_stringContext ctx);
}
