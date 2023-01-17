//
// B.8 Component declarations
//


grammar PSS_identifier;


identifier
	: ID
;

hierarchical_id_list
	: hierarchical_id (',' hierarchical_id)*
;

hierarchical_id
	: member_path_elem ('.' member_path_elem)*
;

member_path_elem
	: identifier function_parameter_list? ('[' expression ']')?
;

action_identifier
	: identifier
;

component_identifier
	: identifier
;

covercross_identifier
	: identifier
;

covergroup_identifier
	: identifier
;

coverpoint_identifier
	: identifier
;

enum_identifier
	: identifier
;

function_identifier
	: identifier
;

import_class_identifier
	: identifier
;

index_identifier
	: identifier
;

iterator_identifier
	: identifier
;

label_identifier
	: identifier
;

language_identifier
	: identifier
;

package_identifier
	: identifier
;

struct_identifier
	: identifier
;

symbol_identifier
	: identifier
;
	
type_identifier
	: '::'? type_identifier_elem ('::' type_identifier_elem)*
;

type_identifier_elem
	: identifier template_param_value_list*
;

action_type_identifier
	: type_identifier
;

buffer_type_identifier
	: type_identifier
;

component_type_identifier
	: type_identifier
;

covergroup_type_identifier
	: type_identifier
;

enum_type_identifier
	: type_identifier
;

resource_type_identifier
	: type_identifier
;

state_type_identifier
	: type_identifier
;

stream_type_identifier
	: type_identifier
;

entity_type_identifier
	: action_type_identifier
	| component_type_identifier
	| flow_object_type
	| resource_object_type
;

