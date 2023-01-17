//
// B.17 Expressions
//


grammar PSS_expressions;

constant_expression
	: expression
;

expression
	: primary 
	| unary_operator primary
	| expression binary_operator expression
	| expression '?' expression ':' expression // conditional_expression
	| expression 'in' '[' open_range_list ']'	// in_expression
	| expression 'in' collection_expression 	// in_expression
;

unary_operator
	: '-'
	| '!'
	| '~'
	| '&'
	| '|'
	| '^'
;

binary_operator
	: '*'
	| '/'
	| '%'
	| '+'
	| '-'
	| '<<'
	| '>>'
	| '=='
	| '!='
	| '<'
	| '<='
	| '>'
	| '>='
	| '||'
	| '&&'
	| '|'
	| '^'
	| '&'
	| '**'
;

assign_op
	: '=' 
	| '+='
	| '-='
	| '<<='
	| '>>='
	| '|='
	| '&='
;

open_range_list
	: open_range_value (',' open_range_value)*
;

open_range_value
	: expression ('..' expression)?
;

collection_expression
	: expression
;

primary
	: number
	| aggregate_literal
	| bool_literal
	| string_literal
	| null_ref
	| paren_expr
	| cast_expression
	| ref_path
	| compile_has_expr
;

paren_expr
	: '(' expression ')'
;

cast_expression
	: '(' casting_type ')' expression
;

ref_path
	: static_ref_path ('.' hierarchical_id)? bit_slice?
	| ('super' '.')? hierarchical_id bit_slice?
;

static_ref_path
	: '::'? (type_identifier_elem '::')* member_path_elem
;

bit_slice
	: '[' constant_expression ':' constant_expression ']'
;

function_call
	: 'super' '.' function_ref_path
	| '::'? (type_identifier_elem '::')? function_ref_path
;

function_ref_path
	: (member_path_elem '.')* identifier function_parameter_list
;

symbol_call
	: symbol_identifier function_parameter_list ';'
;

function_parameter_list
	: '(' (expression (',' expression)*)? ')'
;


