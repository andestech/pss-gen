//
// Annex B.8 Component declarations
//

grammar PSS;

import
	PSS_package,				// B.1
	PSS_action_declarations,		// B.2
	PSS_struct_declarations,		// B.3
	PSS_exec,				// B.4
	PSS_functions,				// B.5
	PSS_foreign_procedural_interface,	// B.6
	PSS_procedural_statements,		// B.7
	PSS_component_declaration,		// B.8
	PSS_activity_statements,		// B.9
	PSS_overrides,				// B.10
	PSS_data_declarations,			// B.11
	PSS_template,				// B.12
	PSS_data_types,				// B.13
	PSS_constraints,			// B.14
	PSS_coverage,				// B.15
	PSS_conditional_compilation,		// B.16
	PSS_expressions,			// B.17
	PSS_identifier,				// B.18
	PSS_numbers_and_literals,		// B.19
	PSS_additional_lexical_conventions	// B.20
;

model
	: portable_stimulus_description*
;

portable_stimulus_description
	: package_body_item
	| package_declaration
	| component_declaration
;
	

