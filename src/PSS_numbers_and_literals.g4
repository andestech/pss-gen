//
// B.19 Numbers and literals
//

grammar PSS_numbers_and_literals;


number
	: OCT_NUMBER
	| DEC_NUMBER
	| HEX_NUMBER
	| based_bin_number
	| based_oct_number
	| based_dec_number
	| based_hex_number
;

OCT_NUMBER: '0' [0-7_]* ;

DEC_NUMBER:  [1-9] [0-9_]*;

HEX_NUMBER: '0' [xX] [0-9a-fA-F] [0-9a-fA-F_]* ;

BASED_BIN_LITERAL : '\'' [s|S]? [bB] [01] [01_]* ;

BASED_OCT_LITERAL : '\'' [s|S]? [oO] [0-7] [0-7_]* ;

BASED_DEC_LITERAL : '\'' [s|S]? [dD] [0-9] [0-9_]* ;

BASED_HEX_LITERAL : '\'' [s|S]? [hH] [0-9a-fA-F] [0-9a-fA-F_]* ;

based_bin_number : DEC_NUMBER? BASED_BIN_LITERAL ;

based_oct_number : DEC_NUMBER? BASED_OCT_LITERAL ;

based_dec_number : DEC_NUMBER? BASED_DEC_LITERAL ;

based_hex_number : DEC_NUMBER? BASED_HEX_LITERAL ;

aggregate_literal
	: empty_aggregate_literal
	| value_list_literal
	| map_literal
	| struct_literal
;

empty_aggregate_literal
	: '{' '}'
;

value_list_literal
	: '{' expression (',' expression)* '}'
;

map_literal
	: '{' map_literal_item (',' map_literal_item)* '}'
;

map_literal_item
	: expression ':' expression
;

struct_literal
	: '{' struct_literal_item (',' struct_literal_item)* '}'
;

struct_literal_item
	: '.' identifier '=' expression
;

bool_literal
	: 'true'
	| 'false'
;

null_ref
	: 'null'
;


