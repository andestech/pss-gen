//
// B.13 Data types
//


grammar PSS_data_types;


data_type
	: scalar_data_type
	| collection_type
	| reference_type
	| type_identifier
;

scalar_data_type
	: chandle_type
	| integer_type
	| string_type
	| bool_type
	| enum_type
	| float_type
;

casting_type
	: integer_type
	| bool_type
	| enum_type
	| float_type
	| reference_type
	| type_identifier
;

chandle_type
	: 'chandle'
;


integer_type
	: integer_atom_type
          ('[' constant_expression (':' '0')? ']')?
          ('in' '[' domain_open_range_list ']')?
;

integer_atom_type
	: 'int'
	| 'bit'
;

domain_open_range_list
	: domain_open_range_value (',' domain_open_range_value)*
;

domain_open_range_value
	: constant_expression ('..' constant_expression)*
	| constant_expression '..'
	| '..' constant_expression
;

string_type
	: 'string' ('in' '[' string_literal (',' string_literal)* ']')?
;

bool_type
	: 'bool'
;

enum_declaration
	: 'enum' enum_identifier (':' data_type)? '{' (enum_item (',' enum_item)*)? '}'
;

enum_item
	: identifier ('=' constant_expression)?
;

enum_type
	: enum_type_identifier ('in' '[' domain_open_range_list ']')?
;

float_type
	: 'float32'
	| 'float64'
;

collection_type
	: array_type
	| map_type
	| list_type
	| set_type
;

array_type
	: 'array' '<' data_type ',' array_size_expression '>'
;

list_type
	: 'list' '<' data_type '>'
;

map_type
	: 'map' '<' data_type ',' data_type '>'
;

set_type
	: 'set' '<' data_type '>'
;



array_size_expression
	: constant_expression
;

reference_type
	: 'ref' entity_type_identifier
;

typedef_declaration
	: 'typedef' data_type identifier ';'
;




