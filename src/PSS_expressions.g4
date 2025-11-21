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
	| <assoc=left> expression binary_operator_precedence3 expression
	| <assoc=left> expression binary_operator_precedence4 expression
	| <assoc=left> expression binary_operator_precedence5 expression
	| <assoc=left> expression binary_operator_precedence6 expression
	| <assoc=left> expression binary_operator_precedence7 expression
	| <assoc=left> expression 'in' '[' open_range_list ']'	// in_expression
	| <assoc=left> expression 'in' collection_expression 	// in_expression
	| <assoc=left> expression binary_operator_precedence8 expression
	| <assoc=left> expression binary_operator_precedence9 expression
	| <assoc=left> expression binary_operator_precedence10 expression
	| <assoc=left> expression binary_operator_precedence11 expression
	| <assoc=left> expression binary_operator_precedence12 expression
	| <assoc=left> expression binary_operator_precedence13 expression
	| <assoc=right> expression '?' expression ':' expression // conditional_expression
;

unary_operator
	: '-'
	| '!'
	| '~'
	| '&'
	| '|'
	| '^'
;

binary_operator_precedence3
    : '**'
;

binary_operator_precedence4
    : '*'
	| '/'
	| '%'
;

binary_operator_precedence5
    : '+'
	| '-'
;

binary_operator_precedence6
    : '<<'
	| '>>'
;

binary_operator_precedence7
    : '<'
	| '<='
	| '>'
	| '>='
;

binary_operator_precedence8
    : '=='
	| '!='
;

binary_operator_precedence9
    : '&'
;

binary_operator_precedence10
    : '^'
;

binary_operator_precedence11
    : '|'
;

binary_operator_precedence12
    : '&&'
;

binary_operator_precedence13
    : '||'
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
	: expression (RANGE expression)?
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


