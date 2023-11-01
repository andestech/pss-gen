//
// B.11 Data declarations
//


grammar PSS_data_declarations;



data_declaration
	: data_type data_instantiation (',' data_instantiation)* ';'
;

data_instantiation
	: identifier array_dim? ('=' constant_expression)?
;

array_dim
	: '[' constant_expression ']'
;

attr_field
	: access_modifier? rand_or_static_const? data_declaration
;

rand_or_static_const
	: 'rand'
	| 'static' 'const'
;

access_modifier
	: 'public'
	| 'protected'
	| 'private'
;

attr_group
	: access_modifier ':'
;
	



