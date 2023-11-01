//
// B.14 Constraints
//


grammar PSS_constraints;

constraint_declaration
	: 'constraint' constraint_set
	| 'dynamic'? 'constraint' identifier constraint_block
;

constraint_set
	: constraint_body_item
	| constraint_block
;

constraint_block
	: '{' constraint_body_item* '}'
;

constraint_body_item
	: expression_constraint_item
	| foreach_constraint_item
	| forall_constraint_item
	| if_constraint_item
	| implication_constraint_item
	| unique_constraint_item
	| 'default' hierarchical_id '==' constant_expression ';'
	| 'default' 'disable' hierarchical_id ';'
	| dist_directive
	| constraint_body_compile_if
	| stmt_terminator
;

expression_constraint_item
	: expression ';'
;

foreach_constraint_item
	: 'foreach' '(' (iterator_identifier ':')? expression ('[' index_identifier ']')? ')'
	   constraint_set
;

forall_constraint_item
	: 'forall' '(' iterator_identifier ':' type_identifier ('in' ref_path)? ')' constraint_set
;

if_constraint_item
	: 'if' '(' expression ')' constraint_set ('else' constraint_set)?
;

implication_constraint_item
	: expression '->' constraint_set
;

unique_constraint_item
	: 'unique' '{' hierarchical_id_list '}' ';'
;

dist_directive
	: 'dist' expression 'in' '[' dist_list ']' ';'
;

dist_list
	: dist_item (',' dist_item)*
;

dist_item
	: open_range_value dist_weight?
;

dist_weight
	: ':=' expression
	| ':/' expression
;

