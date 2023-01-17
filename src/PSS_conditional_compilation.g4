//
// B.16 Conditional compilation
//


grammar PSS_conditional_compilation;


package_body_compile_if
	: 'compile' 'if' '(' constant_expression ')'
           package_body_compile_if_item ('else' package_body_compile_if_item)?
;

package_body_compile_if_item
	: package_body_item
	| '{' package_body_item* '}'
;

action_body_compile_if
	: 'compile' 'if' '(' constant_expression ')'
	  action_body_compile_if_item ('else' action_body_compile_if_item)
;

action_body_compile_if_item
	: action_body_item
	| '{' action_body_item* '}'
;

component_body_compile_if
	: 'compile' 'if' '(' constant_expression ')'
	  component_body_compile_if_item ('else' component_body_compile_if_item)?
;

component_body_compile_if_item
	: component_body_item
	| '{' component_body_item* '}'
;

struct_body_compile_if
	: 'compile' 'if' '(' constant_expression ')'
	   struct_body_compile_if_item ('else' struct_body_compile_if_item)?
;

struct_body_compile_if_item
	: struct_body_item
	| '{' struct_body_item* '}'
;

compile_has_expr
	: 'compile' 'has' '(' static_ref_path ')'
;

compile_assert_stmt
	: 'compile' 'assert' '(' constant_expression (',' string_literal)? ')' ';'
;




	



