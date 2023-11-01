//
// B.20 Additional lexical conventions
// 

grammar PSS_additional_lexical_conventions;

SL_COMMENT : '//' .*? '\r'? '\n' -> skip ;

ML_COMMENT : '/*' .*? '*/' -> skip ;

string_literal
	: double_quoted_string
	| TRIPLE_QUOTED_STRING
;

double_quoted_string
	: '"' (escaped_character | UNESCAPED_CHARACTER)* '"'
;

TRIPLE_QUOTED_STRING
	: '"""' .*? '"""'
;

UNESCAPED_CHARACTER
	: '!' | '#' | '$' | '%' | '&' | '(' | ')' | '*' | '+' | ',' | '-' | '.' | '/'
	| '0'..'9'
	| ':' | ';' | '<' | '=' | '>' | '@'
	| 'A'..'Z'
	| '[' | ']' | '^' | '_' | '`'
	| 'a'..'z'
	| '{' | '|' | '}' | '~'
;

escaped_character
	: '\\' ('\'' | '"' | '?' | '\\' | 'a' | 'b' | 'f' | 'n' | 'r' | 't' | 'v' | OCT_DIGIT OCT_DIGIT OCT_DIGIT)
;

filename_string
	: double_quoted_string
;

ID
	: [a-z]
	| [A-Z]
	| '_' [a-zA-Z0-9_]*
;

ESCAPED_ID
	: '\\' .*? WS
;

WS : [ \t\n\r]+ -> channel (HIDDEN) ;
