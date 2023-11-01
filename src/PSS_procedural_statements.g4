//
// B.7 Procedural statements
//


grammar PSS_procedural_statements;


procedural_stmt
	: procedural_sequence_block_stmt
	| procedural_data_declaration
	| procedural_assignment_stmt
	| procedural_void_function_call_stmt
	| procedural_return_stmt
	| procedural_repeat_stmt
	| procedural_while_stmt
	| procedural_repeat_while_stmt
	| procedural_foreach_stmt
	| procedural_if_else_stmt
	| procedural_match_stmt
	| procedural_break_stmt
	| procedural_continue_stmt
	| procedural_randomization_stmt
	| procedural_compile_if
	| stmt_terminator
;

procedural_sequence_block_stmt
	: 'sequence'? '{' procedural_stmt* '}'
;

procedural_data_declaration
	: data_type procedural_data_instantiation (',' procedural_data_instantiation)* ';'
;

procedural_data_instantiation
	: identifier array_dim? ('=' expression)?
;

procedural_assignment_stmt
	: ref_path assign_op expression ';'
;

procedural_void_function_call_stmt
	: ('(' 'void' ')')? function_call ';'
;

procedural_return_stmt
	: 'return' expression? ';'
;

procedural_repeat_stmt
	: 'repeat' '(' (index_identifier ':')? expression ')' procedural_stmt
;

procedural_repeat_while_stmt
	: 'repeat' procedural_stmt 'while' '(' expression ')' ';'
;

procedural_while_stmt
	: 'while' '(' expression ')' procedural_stmt
;

procedural_foreach_stmt
	: 'foreach' '(' (iterator_identifier ':')? expression ('[' index_identifier ']')? ')'
          procedural_stmt
;

procedural_if_else_stmt
	: 'if' '(' expression ')' procedural_stmt ('else' procedural_stmt)?
;

procedural_match_stmt
	: 'match' '(' match_expression ')'
	  '{' procedural_match_choice+ '}'
;

procedural_match_choice
	: '[' open_range_list ']' ':' procedural_stmt
	| 'default' ':' procedural_stmt
;

procedural_break_stmt
	: 'break' ';'
;

procedural_continue_stmt
	: 'continue' ';'
;

procedural_randomization_stmt
	: 'randomize' procedural_randomization_target procedural_randomization_term
;
	  
procedural_randomization_target
	: hierarchical_id (',' hierarchical_id)*
;

procedural_randomization_term
	: 'with' constraint_set
	| ';'
;

