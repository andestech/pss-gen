//
// B.10 Overrides
//


grammar PSS_overrides;

override_declaration
	: 'override' '{' override_stmt* '}' 
;

override_stmt
	: type_override
	| instance_override
	| override_compile_if
	| stmt_terminator
;

type_override
	: 'type' type_identifier 'with' type_identifier ';'
;

instance_override
	: 'instance' hierarchical_id 'with' type_identifier ';'
;


