//
// B.5 Functions
//


grammar PSS_functions;


procedural_function
	: platform_qualifier? 'pure'? 'function'
          function_prototype '{' procedural_stmt* '}'
;

function_decl
	: 'pure'? 'function' function_prototype ';'
;

function_prototype
	: function_return_type function_identifier function_parameter_list_prototype
;

function_return_type
	: 'void'
	| data_type
;

function_parameter_list_prototype
	: '(' (function_parameter (',' function_parameter)* )? ')'
	| '(' (function_parameter ',')* varargs_parameter ')'
;

function_parameter
	: function_parameter_dir? data_type identifier array_dim? ('=' constant_expression)?
	| 'type' identifier
	| 'ref' type_category identifier
	| 'struct' identifier
;

function_parameter_dir
	: 'input'
	| 'output'
	| 'inout'
;

varargs_parameter
	: data_type '...' identifier
	| 'type' '...' identifier
	| 'ref' type_category '...' identifier
	| 'struct' '...' identifier
;

