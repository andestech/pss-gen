//
// B.9 Activity statements
//


grammar PSS_activity_statements;

activity_stmt
	: ( label_identifier ':')? labeled_activity_stmt
	| activity_action_traversal_stmt
	| activity_data_field
	| activity_bind_stmt
	| action_handle_declaration
	| activity_constraint_stmt
	| activity_scheduling_constraint
	| stmt_terminator
;

labeled_activity_stmt
	: activity_sequence_block_stmt
	| activity_parallel_stmt
	| activity_schedule_stmt
	| activity_repeat_stmt
	| activity_foreach_stmt
	| activity_select_stmt
	| activity_if_else_stmt
	| activity_match_stmt
	| activity_replicate_stmt
	| activity_super_stmt
	| activity_atomic_block_stmt
	| symbol_call
;

activity_action_traversal_stmt
	: identifier ('[' expression ']')? inline_constraints_or_empty
	| 'do' type_identifier inline_constraints_or_empty
;

inline_constraints_or_empty
	: 'with' constraint_set ';'
	| ';'
;

activity_sequence_block_stmt
	: 'sequence'? '{' activity_stmt* '}'
;

activity_parallel_stmt
	: 'parallel' activity_join_spec? '{' activity_stmt* '}'
;

activity_schedule_stmt
	: 'schedule' activity_join_spec? '{' activity_stmt* '}'
;

activity_join_spec
	: activity_join_branch
	| activity_join_select
	| activity_join_none
	| activity_join_first
;

activity_join_branch
	: 'join_branch' '(' label_identifier (',' label_identifier)* ')'
;

activity_join_select
	: 'join_select' '(' expression ')'
;

activity_join_none
	: 'join_none'
;

activity_join_first
	: 'join_first' '(' expression ')'
;

activity_repeat_stmt
	: 'repeat' '(' (index_identifier ':')? expression ')' activity_stmt
	| 'repeat' activity_stmt 'while' '(' expression ')' ';'
;

activity_foreach_stmt
	: 'foreach' '(' (iterator_identifier ':')? expression ('[' index_identifier ']')? ')' activity_stmt
;

activity_select_stmt
	: 'select' '{' select_branch select_branch select_branch* '}'
;

select_branch
	:  (( select_branch_cond )? ( select_branch_weight )? ':' )? activity_stmt
;

select_branch_cond:
	'(' expression ')'
;

select_branch_weight:
	'[' expression ']'
;
	

activity_if_else_stmt
	: 'if' '(' expression ')' activity_stmt ('else' activity_stmt)?
;

activity_match_stmt
	: 'match' '(' match_expression ')' '{' match_choice match_choice* '}'
;

match_expression
	: expression
;

match_choice
	: '[' open_range_list ']' ':' activity_stmt
	| 'default' ':' activity_stmt
;

activity_replicate_stmt
	: 'replicate' '(' (index_identifier ':')? expression ')'
	  (label_identifier '[' ']' ':')? labeled_activity_stmt
;

activity_super_stmt
	: 'super' ';'
;

activity_atomic_block_stmt
	: 'atomic' '{' activity_stmt* '}'
;

activity_bind_stmt
	: 'bind' hierarchical_id activity_bind_item_or_list ';'
;

activity_bind_item_or_list
	: hierarchical_id
	| '{' hierarchical_id_list '}'
;

activity_constraint_stmt
	: 'constraint' constraint_set
;

symbol_declaration
	: 'symbol' symbol_identifier ('(' symbol_paramlist ')')? '{' activity_stmt* '}'
;

symbol_paramlist
	: symbol_param (',' symbol_param)*
;

symbol_param
	: data_type identifier
;

	

