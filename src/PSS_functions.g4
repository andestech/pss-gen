//
// B.5 Functions
//


grammar PSS_functions;


procedural_function
	: platform_qualifier? pure='pure'? 'static'? 'function'
          function_prototype '{' procedural_stmt* '}'
;

function_decl
	: pure='pure'? 'static'? 'function' function_prototype ';'
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
	: function_parameter_dir? data_type identifier ('=' constant_expression)?
	| type='type' identifier
	| ref='ref' type_category identifier
	| struct='struct' identifier
;

function_parameter_dir
	: 'input'
	| 'output'
	| 'inout'
;

varargs_parameter
	: data_type '...' identifier
	| type='type' '...' identifier
	| ref='ref' type_category '...' identifier
	| struct='struct' '...' identifier
;

