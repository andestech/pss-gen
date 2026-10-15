//
// B.2 Action declarations
//


grammar PSS_action_declarations;


action_declaration
	: 'action' action_identifier
	   template_param_decl_list? action_super_spec? '{' action_body_item* '}'
;

abstract_action_declaration
	: 'abstract' action_declaration
;

action_super_spec
	: ':' type_identifier
;

action_body_item
	: activity_declaration
	| override_declaration
	| constraint_declaration
	| action_field_declaration
	| symbol_declaration
	| covergroup_declaration
	| exec_block_stmt
	| activity_scheduling_constraint
	| attr_group
	| compile_assert_stmt
	| covergroup_instantiation
	| action_body_compile_if
	| stmt_terminator
;

activity_declaration
	: 'activity' '{' activity_declaration_body_stmt* '}'
;

activity_declaration_body_stmt
	: (label_identifier ':')? activity_stmt
;
	
action_field_declaration
	: attr_field
	| activity_data_field
	| action_handle_declaration
	| object_ref_field_declaration
;

object_ref_field_declaration
	: flow_ref_field_declaration
	| resource_ref_field_declaration
;

input_or_output
	: 'input'
	| 'output'
;

flow_ref_field_declaration
	: input_or_output flow_object_type object_ref_field (',' object_ref_field)* ';'
;

lock_or_share
	: 'lock'
	| 'share'
;

resource_ref_field_declaration
	: lock_or_share resource_object_type object_ref_field (',' object_ref_field)* ';'
;

flow_object_type
	: buffer_type_identifier
	| state_type_identifier
	| stream_type_identifier
;

resource_object_type
	: resource_type_identifier
;

object_ref_field
	: identifier array_dim?
;

action_handle_declaration
	: action_type_identifier action_instantiation ';'
;

action_instantiation
	: action_identifier array_dim? (',' action_identifier array_dim?)*
;

activity_data_field
	: 'action' data_declaration
;

parallel_or_sequence
	: 'parallel'
	| 'sequence'
;

activity_scheduling_constraint
	: 'constraint' parallel_or_sequence
	  '{' hierarchical_id ',' hierarchical_id (',' hierarchical_id)* '}'
;

