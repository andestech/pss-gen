//
// B.4 Exec blocks
//


grammar PSS_exec;

exec_block_stmt
	: exec_block
	| target_code_exec_block
	| target_file_exec_block
	| stmt_terminator
;

exec_block
	: 'exec' exec_kind '{' exec_stmt* '}'
;

exec_kind
	: 'pre_solve'
	| 'post_solve'
	| 'body'
	| 'header'
	| 'declaration'
	| 'run_start'
	| 'run_end'
	| 'init_down'
	| 'init_up'
	| 'init'
;

exec_stmt
	: procedural_stmt
	| exec_super_stmt
;

exec_super_stmt
	: 'super' ';'
;

target_code_exec_block
	: 'exec' exec_kind language_identifier '=' string_literal ';'
;

target_file_exec_block
	: 'exec' 'file' filename_string '=' string_literal ';'
;





