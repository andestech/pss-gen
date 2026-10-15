//
// B.8 Component declarations
//


grammar PSS_component_declaration;


component_declaration
	: 'pure'? 'component' component_identifier template_param_decl_list?
	  component_super_spec? '{' component_body_item* '}'
;

component_super_spec
	: ':' type_identifier
;

component_body_item
	: override_declaration
	| component_data_declaration
	| component_pool_declaration
	| action_declaration
	| abstract_action_declaration
	| object_bind_stmt
	| exec_block
	| struct_declaration
	| enum_declaration
	| covergroup_declaration
	| function_decl
	| import_class_decl
	| procedural_function
	| import_function
	| target_template_function
	| export_action
	| typedef_declaration
	| import_stmt
	| extend_stmt
	| compile_assert_stmt
	| attr_group
	| component_body_compile_if
	| stmt_terminator
;

component_data_declaration
	: access_modifier? ('static' 'const')? data_declaration
;
	
component_pool_declaration
	: 'pool' ( '[' expression ']' )? type_identifier identifier (',' identifier)* ';'
;

object_bind_stmt
	: 'bind' hierarchical_id object_bind_item_or_list ';'
;

object_bind_item_or_list
	: object_bind_item_path
	| '{' object_bind_item_path (',' object_bind_item_path)* '}'
;

object_bind_item_path
	: (component_path_elem '.')* object_bind_item
;

component_path_elem
	: component_identifier ('[' constant_expression ']')?
;

object_bind_item
	: action_type_identifier '.' identifier ('[' constant_expression ']')?
	| '*'
;
