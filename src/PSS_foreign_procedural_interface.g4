//
// B.6 Foreign procedural interface
//


grammar PSS_foreign_procedural_interface;

import_function
	: 'import' platform_qualifier? language_identifier? 'function' type_identifier ';'
	| 'import' platform_qualifier? language_identifier? 'function' function_prototype ';'	
;

platform_qualifier
	: 'target'
	| 'solv'
;

target_template_function
	: 'target' language_identifier 'function' function_prototype '=' string_literal ';'
;

import_class_decl
	: 'import' 'class' import_class_identifier
           import_class_extends? '{' import_class_function_decl* '}'
;

import_class_extends
	: ':' type_identifier (',' type_identifier)*
;

import_class_function_decl
	: function_prototype ';'
;

export_action
	: 'export' platform_qualifier? action_type_identifier function_parameter_list_prototype ';'
;





