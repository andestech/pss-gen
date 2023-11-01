//
// B.19 Numbers and literals
//

grammar PSS_numbers_and_literals;


number
	: integer_number
	| floating_point_number
;

integer_number
	: BIN_NUMBER
	| OCT_NUMBER
	| DEC_NUMBER
	| HEX_NUMBER
	| based_bin_number
	| based_oct_number
	| based_dec_number
	| based_hex_number
;

BIN_DIGIT : [01] ;
OCT_DIGIT : [0-7] ;
DEC_DIGIT : [0-9] ;
HEX_DIGIT : [0-9a-fA-F];

BIN_NUMBER : [0] [bB]? BIN_DIGIT (BIN_DIGIT|'_')* ;
OCT_NUMBER : [0] (OCT_DIGIT|'_')* ;
DEC_NUMBER : [1-9]? DEC_DIGIT (DEC_DIGIT|'_')* ;
HEX_NUMBER : [0] [xX]? HEX_DIGIT (HEX_DIGIT|'_')* ;

BASED_BIN_LITERAL : '\'' [sS]? [bB] BIN_DIGIT (BIN_DIGIT|'_')* ;
BASED_OCT_LITERAL : '\'' [sS]? [bB] OCT_DIGIT (OCT_DIGIT|'_')* ;
BASED_DEC_LITERAL : '\'' [sS]? [bB] DEC_DIGIT (DEC_DIGIT|'_')* ;
BASED_HEX_LITERAL : '\'' [sS]? [bB] HEX_DIGIT (HEX_DIGIT|'_')* ;

based_bin_number : DEC_NUMBER? BASED_BIN_LITERAL ;
based_oct_number : DEC_NUMBER? BASED_OCT_LITERAL ;
based_dec_number : DEC_NUMBER? BASED_DEC_LITERAL ;
based_hex_number : DEC_NUMBER? BASED_HEX_LITERAL ;

floating_point_number
	: floating_point_dec_number
	| floating_point_sci_number
;

unsigned_number : DEC_NUMBER;

floating_point_dec_number : unsigned_number '.' unsigned_number;
floating_point_sci_number : unsigned_number ('.' unsigned_number)? exp sign? unsigned_number;

exp : 'e' | 'E';
sign : '+' | '-';

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


