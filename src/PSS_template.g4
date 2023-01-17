//
// B.12 Template types
//


grammar PSS_template;

template_param_decl_list
	: '<' template_param_decl (',' template_param_decl )* '>'
;

template_param_decl
	: type_param_decl
	| value_param_decl
;

type_param_decl
	: generic_type_param_decl
	| category_type_param_decl
;

generic_type_param_decl
	: 'type' identifier ('=' type_identifier)?
;

category_type_param_decl
	: type_category identifier type_restriction? ('=' type_identifier)?
;

type_restriction
	: ':' type_identifier
;

type_category
	: 'action'
	| 'component'
	| struct_kind
;

value_param_decl
	: data_type identifier ('=' constant_expression)?
;

template_param_value_list
	: '<' (template_param_value (',' template_param_value)*)? '>'
;

template_param_value
	: constant_expression
	| data_type
;


	



