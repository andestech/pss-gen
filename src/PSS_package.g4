//
// B.1 Package declarations
//


grammar PSS_package;

package_declaration
	: 'package' package_id_path '{' package_body_item* '}'
;

package_id_path
	: package_identifier ('::' package_identifier)*
;

package_body_item
	: abstract_action_declaration
	| struct_declaration
	| enum_declaration
	| covergroup_declaration
	| function_decl
	| import_class_decl
	| procedural_function
	| import_function
	| target_template_function
	| export_action
	| typedef_declaration
	| import_stmt
	| extend_stmt
	| const_field_declaration
	| component_declaration
	| package_declaration
	| compile_assert_stmt
	| package_body_compile_if
	| stmt_terminator
;

import_stmt
	: 'import' package_import_pattern ';'
;

package_import_pattern
	: type_identifier package_import_qualifier?
;

package_import_qualifier
	: package_import_wildcard
	| package_import_alias
;

package_import_wildcard
	: '::' '*'
;

package_import_alias
	: 'as' package_identifier
;

extend_stmt
	: 'extend' 'action' type_identifier '{' action_body_item* '}'
	| 'extend' 'component' type_identifier '{' component_body_item* '}'
	| 'extend' struct_kind type_identifier '{' struct_body_item* '}'
	| 'extend' 'enum' type_identifier '{' (enum_item (',' enum_item)*)? '}'
;

const_field_declaration
	: 'static'? 'const' data_declaration
;

stmt_terminator
	: ';'
;




       



