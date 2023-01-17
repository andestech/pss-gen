//
// B.15 Coverage specification
//


grammar PSS_coverage;

covergroup_declaration
	: 'covergroup' covergroup_identifier
	  '(' covergroup_port (',' covergroup_port)? ')' '{' covergroup_body_item* '}'
	;

covergroup_port
	: data_type identifier
	;

covergroup_body_item
	: covergroup_option
	| covergroup_coverpoint
	| covergroup_cross
	| stmt_terminator
;

covergroup_option
	: 'option' '.' identifier '=' constant_expression ';'
	| 'type_option' '.' identifier '=' constant_expression ';'
;

covergroup_instantiation
	: covergroup_type_instantiation
	| inline_covergroup
;

inline_covergroup
	: 'covergroup' '{' covergroup_body_item* '}' identifier ';'
;

covergroup_type_instantiation
	: covergroup_type_identifier covergroup_identifier
	  '(' covergroup_portmap_list ')' covergroup_options_or_empty
;

covergroup_portmap_list
	: covergroup_portmap (',' covergroup_portmap)*
	| hierarchical_id_list
;

covergroup_portmap
	: '.' identifier '(' hierarchical_id ')'
;

covergroup_options_or_empty
	: 'with' '{' covergroup_option* '}'
	| ';'
;


covergroup_coverpoint
	:  (data_type? coverpoint_identifier ':')? 'coverpoint'
	   expression ('iff' '(' expression ')')? bins_or_empty
	| ';'
;

bins_or_empty
	: '{' covergroup_coverpoint_body_item* '}'
	| ';'
;

covergroup_coverpoint_body_item
	: covergroup_option
	| covergroup_coverpoint_binspec
;

covergroup_coverpoint_binspec
	: bins_keyword identifier ('[' constant_expression? ']')? '=' coverpoint_bins
;

coverpoint_bins
	: '[' covergroup_range_list ']' ('with' '(' covergroup_expression ')')? ';'
	| coverpoint_identifier 'with' '(' covergroup_expression ')' ';'
	| 'default' ';'
;

covergroup_range_list
	: covergroup_value_range (',' covergroup_value_range)*
;

covergroup_value_range
	: expression
	| expression '..' expression?
	| expression? '..' expression
;

bins_keyword
	: 'bins'
	| 'illegal_bins'
	| 'ignore_bins'
;

covergroup_expression
	: expression
;

covergroup_cross
	: covercross_identifier ':' 'cross' coverpoint_identifier
 	  (',' coverpoint_identifier)* ('iff' '(' expression ')')? cross_item_or_null
;

cross_item_or_null
	: '{' covergroup_cross_body_item* '}'
	| ';'
;

covergroup_cross_body_item
	: covergroup_option
	| covergroup_cross_binspec
;

covergroup_cross_binspec
	: bins_keyword identifier '=' covercross_identifier
	  'with' '(' covergroup_expression ')' ';'
;


