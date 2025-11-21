//
// B.20 Additional lexical conventions
// 

grammar PSS_additional_lexical_conventions;

LINE_COMMENT : '//' .*? '\r'? '\n' -> skip ;

COMMENT : '/*' .*? '*/' -> skip ;

ID : [a-zA-Z_] [a-zA-Z0-9_]* ;

RANGE : '..' ;

string_literal
	: QUOTED_STRING
	| TRIPLE_QUOTED_STRING
;

QUOTED_STRING : '"' (~ [\n\r"])* '"' ;


filename_string : QUOTED_STRING ;

ESC : '\\"' | '\\\\' ;
TRIPLE_QUOTED_STRING : '"""' .*? '"""' ;

WS : [ \t\n\r]+ -> channel (HIDDEN) ;
