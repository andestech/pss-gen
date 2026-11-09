//
// B.3 Struct declarations
//


grammar PSS_struct_declarations;

struct_declaration
	: struct_kind struct_identifier
          template_param_decl_list? struct_super_spec? '{' struct_body_item* '}'
;

struct_kind
	: 'struct'
	| object_kind
;

object_kind
	: 'buffer'
	| 'stream'
	| 'state'
	| 'resource'
;

struct_super_spec
	: ':' type_identifier
;

struct_body_item
	: constraint_declaration
	| attr_field
	| typedef_declaration
	| exec_block_stmt
	| attr_group
	| compile_assert_stmt
	| covergroup_declaration
	| covergroup_instantiation
	| struct_body_compile_if
	| stmt_terminator
;


