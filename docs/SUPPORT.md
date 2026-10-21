# PSSGen language support

PSSGen reads a PSS description, randomly solves the constraints of the root
action and of the actions it traverses, and writes the expanded `exec`
target templates to a test file. This document lists, per chapter of the
Accellera Portable Test and Stimulus Standard (LRM), which constructs PSSGen
handles and how.

LRM references are given as `2.0 / 2.1`: the PSS 2.0 section first (the
edition the PSSGen sources cite), then the PSS 2.1 section, which is numbered
one chapter lower from the data types on.

## How to read the tables

Status:

* **supported** - works as the LRM describes, as far as the listed test shows.
* **partial** - accepted, but the behavior deviates from the LRM in the way the
  Notes column describes.
* **unsupported** - PSSGen stops with the message in the Message column.

A construct that appears in no table has no test. PSSGen may accept it, but
nothing checks what it does.

Message column:

* A PSS-ERROR is printed as `[<seconds>s] PSS-ERROR: [<ID>] <text>` and is
  identified here by its ID in brackets plus the text, e.g.
  `[ACTION] run_start is not yet supported`.
* A Fatal message carries no ID. It is printed as
  `<Class>.<method>(), Ln<N>: <text>` and is identified here by
  `<Class>.<method>()` plus the text. The `Ln<N>` part is a source line of
  PSSGen and changes between builds, so do not match on it.
* `'...'` (or `...` where the message has no quotes) stands for text copied
  from the input, usually the construct as written with white space removed.
* A parse error is `[SYNTAX] '...'`, where the text gives file, line, column
  and the parser's reason.

PSSGen stops at the first such message, except for the constructs listed in
[Inputs with several unsupported constructs](#inputs-with-several-unsupported-constructs).

Test column: a directory under `tests/` whose `test.pss` uses the construct.
"(skipped)" marks a test whose `test.cfg` has a `skip =` line; the harness does
not run it, and the Notes give the reason. #nn names a GitHub issue.

## Lexical conventions

| LRM (2.0 / 2.1) | Construct | Status | Message | Test | Notes |
|---|---|---|---|---|---|
| 4.1 / 4.1 | Comments `//` and `/* */` | supported | | tests/samples/test_demo | |
| 4.6 / 4.6 | Sized and based numbers (`8'b01101010`, `2'b10`, `'h8045`) | supported | | tests/issues/35 | `'h` numbers are in tests/samples/test_demo (softconfig.pss). |
| 4.6 / 4.6 | A digit string that is no legal number, e.g. `01234567890` | supported | | tests/samples/4_6_Numbers/test_octal (skipped) | Rejected with `[SYNTAX] '...'`, as the LRM requires. The test is skipped because its own input contains this literal; its golden also has wrong values. |
| 4.7 / 4.7 | Quoted `"..."` and triple-quoted `"""..."""` strings | supported | | tests/samples/test_multi_constraint | |
| 4.7 / 4.7 | Escape sequences in quoted strings | partial | `[SYNTAX] '...'` | tests/samples/4_7_String_literals (skipped) | `\"` ends the literal, so the rest of the line is a syntax error; this is why the test is skipped. Other escapes (`\n`, `\t`, `\\`, `\ddd`, ...) are copied unconverted, backslash included. The test's golden also mis-renders control characters. |
| 4.8.1-4.8.3 / 4.8.1-4.8.3 | Empty `{}`, value list `{1, 2}` and map `{1: 2}` literals | supported | | tests/samples/test_function_chaining | |
| 4.8.4 / 4.8.4 | Structure literal `{.a = 1}` | unsupported | `PSSGenVisitor.visitAggregate_literal()`: `Syntax is not yet supported: '...'` | | |

## Data types

| LRM (2.0 / 2.1) | Construct | Status | Message | Test | Notes |
|---|---|---|---|---|---|
| 8.2 / 7.2 | `bit` and `int` with a width `[N]` or `[msb:lsb]`, rand and non-rand | supported | | tests/samples/test_repeat_count | The width must be a number literal. |
| 8.2 / 7.2 | Value domain of ranges on the type: `int in [1..5]`, `[100..]`, `[..10]`, `[10..11, 30..31]` | supported | | tests/issues/14 | |
| 8.2 / 7.2 | Value domain item that is a single value: `bit in [1]`, `int in [0, 1]` | unsupported | `PSSGenVisitor.visitDomain_open_range_value()`: `Syntax is not yet supported: '...'` | tests/samples/7_2_Integer_types/test_int, test_multi_bit, test_single_bit (skipped) | Only in a type declaration; `x in [0, 1]` in a constraint is supported. This message is why the three tests are skipped. |
| 8.2 / 7.2 | `int` without a width | partial | | tests/samples/4_6_Numbers/test_binary, test_decimal, test_hexadecimal (skipped) | PSSGen makes it 65 bits signed instead of 32, so values outside -2^31..2^31-1 do not wrap; this is why the three tests are skipped. Their goldens also contain values the LRM does not give. |
| 8.3 / 7.4 | `bool`, rand and non-rand; printed as `true`/`false` in templates | supported | | tests/samples/7_4_Booleans/test_bool_type | |
| 8.3 / 7.4 | Non-rand `bool` field without initializer, referenced in a template | partial | `[INTVAR-0001] variable '...' is not initialized` | tests/samples/7_4_Booleans/test_bool (skipped) | The LRM default is `false`. The test's golden also expects `0`/`1` where the LRM and PSSGen print `false`/`true`. |
| 8.4 / 7.5 | `enum` with and without item values, at top level and in a component; rand enum fields; `e::ITEM` | supported | | tests/samples/test_enum_declaration | |
| 8.4 / 7.5 | Non-rand enum field without initializer, referenced in a template | partial | `[INTVAR-0001] variable '...' is not initialized` | tests/samples/test_enum (skipped) | The LRM default is the first enum item. |
| 8.4 / 7.5 | Enum type with a domain `e in [A, B]` | unsupported | `PSSGenVisitor.visitScalar_data_type()`: `Syntax is not yet supported: '...'` | | |
| 8.5 / 7.6 | `string`; rand strings constrained with `==` and `in [...]` | supported | | tests/samples/test_with_multi_constraint | |
| 8.5 / 7.6 | String type with a domain `string in ["a", "b"]` | unsupported | `PSSGenVisitor.visitString_type()`: `Syntax is not yet supported: '...'` | | |
| 8.6 / 7.7 | `chandle` | unsupported | `PSSGenVisitor.visitScalar_data_type()`: `Syntax is not yet supported: '...'` | | |
| 8.7 / 7.8 | `struct`: fields, nested field access, struct as function parameter and return value | supported | | tests/issues/13 | Comparison of struct values: see Operators. |
| 8.8.2 / 7.9.2 | Arrays `T a[N]` and `array<T,N>`: initialization, assignment, `==`, `!=`, `in`, `[]`, `foreach`, `size()`, `sum()`, `to_list()`, `to_set()` | supported | | tests/samples/PSSv2.1_7.9_Collections/arrays | |
| 8.8.3 / 7.9.3 | `list<T>`: initialization, assignment, `==`, `!=`, `in`, `[]`, `foreach`, `size`, `clear`, `delete`, `insert`, `pop_back`, `pop_front`, `push_back`, `push_front`, `shuffle`, `to_set` | supported | | tests/samples/PSSv2.1_7.9_Collections/lists | |
| 8.8.4 / 7.9.4 | `map<K,V>`: initialization, assignment, `==`, `!=`, `[]`, `foreach`, `clear`, `delete`, `insert`, `keys`, `size`, `values` | supported | | tests/samples/PSSv2.1_7.9_Collections/maps | |
| 8.8.5 / 7.9.5 | `set<T>`: initialization, assignment, `==`, `!=`, `in`, `foreach`, `clear`, `delete`, `insert`, `size`, `to_list` | supported | | tests/samples/PSSv2.1_7.9_Collections/sets | |
| 8.9 / 7.10 | Reference type `ref T` as a field or variable type | unsupported | `PSSGenVisitor.visitData_type()`: `Syntax is not yet supported: '...'` | | `ref action`, `ref component` function parameters are supported (see Functions). |
| 8.9 / 7.10 | `null` | unsupported | `PSSGenVisitor.visitPrimary()`: `Syntax is not yet supported: '...'` | | |
| 8.10 / 7.11 | `typedef` | unsupported | `PSSGenVisitor.visitTypedef_declaration()`: `Syntax is not yet supported: '...'` | | |
| 8.11 / 7.12 | Cast to an integer type `(bit[4]) x` | supported | | tests/issues/35 | |
| 8.11 / 7.12 | Cast to `bool` | unsupported | `PSSGenVisitor.visitCasting_type()`: `Cast to bool_type is not yet supported: '...'` | | |
| 8.11 / 7.12 | Cast to an enum or other named type `(e) x` | unsupported | `PSSGenVisitor.visitCasting_type()`: `Cast to enum_type is not yet supported: '...'` | | Every `(<name>) x` cast takes this path. |
| 8.11 / 7.12 | Integer cast of a non-numeric field, e.g. a string | unsupported | `PSSCastExpression.eval()`: `Casting < PSSStringInst > is not yet support.` | | The class names the kind of the operand. |
| 8.11 / 7.12 | Integer cast of an expression that refers to no field, e.g. `(bit[8]) (1 + 2)` | unsupported | `PSSCastExpression.eval()`: `Casting < PSSAddExpression > ( ... ) is not yet support.` | | The class names the kind of the operand. |

## Operators and expressions

| LRM (2.0 / 2.1) | Construct | Status | Message | Test | Notes |
|---|---|---|---|---|---|
| 9.3 / 8.3 | Assignment `=`, `+=`, `-=`, `\|=` | supported | | tests/samples/test_repeat_count | |
| 9.5.1 / 8.5.1 | `+ - * / %` | supported | | tests/samples/test_native_functions | `/` is in tests/samples/test_aggregate_function_parameters, `%` in tests/samples/test_continue. |
| 9.5.1 / 8.5.1 | Power `**` | unsupported | `PSSExpression.eval()`: `PSSBinaryExpression.eval() is not implemented` | | In a constraint the text is `PSSBinaryExpression.deduceDomain() is not implemented`. |
| 9.5.2, 9.5.4 / 8.5.2, 8.5.4 | `< <= > >=`, `&&`, `\|\|` | supported | | tests/samples/test_with_constraint_ops | `<=` and `>=` are in tests/samples/test_aggregate_function_parameters. |
| 9.5.3 / 8.5.3 | `==`, `!=` on numbers, strings, enums and collections of scalars | supported | | tests/samples/PSSv2.1_7.9_Collections/lists/test_list_operator_equality | |
| 9.5.3, 9.5.9 / 8.5.3, 8.5.9 | `==`, `!=` and `in` on struct values | partial | | tests/samples/PSSv2.1_7.9_Collections/arrays/test_array_operator_equality | #46: compares struct instances, not contents, so equal-valued structs differ. |
| 9.5.5 / 8.5.5 | `~`, and `\|` through `\|=` | supported | | tests/issues/22 | `\|=` is in tests/samples/9_6_Primary_expressions/test_bit_selects. |
| 9.5.6 / 8.5.6 | Reduction `&x`, `\|x`, `^x` | unsupported | `PSSGenVisitor.visitExpression()`: `unary operator '...'is not yet supported` | | No space before `is` in the message. |
| 9.5.7 / 8.5.7 | `<<` | supported | | tests/issues/35 | |
| 9.7.1 / 8.7.1 | Shift whose result is assigned to a wider target | partial | | tests/issues/36 | #52: sized by the left operand, not by the assignment, so `ans = src << 8` with `bit[8] src` and `bit[32] ans` gives 0. |
| 9.5.8 / 8.5.8 | Conditional `c ? a : b` | unsupported | `[VISITOR-0001] visitExpression is not fully implemented yet` | | |
| 9.5.9 / 8.5.9 | `x in [a, b..c]` and `x in collection` | supported | | tests/issues/20 | |
| 9.5.9 / 8.5.9 | Unqualified enum item on the left of `in` | partial | | tests/samples/PSSv2.1_7.9_Collections/arrays/test_array_operator_in | #47: accepted; the LRM requires `e::ITEM in ...`. |
| 9.5 / 8.5 | Operators other than `==`, `!=`, `in` on strings | unsupported | `PSSVal.Add()`: `PSSStringVal::Add is not implemented` | | The LRM defines no other string operator, so the input is illegal. Each operator names its own method (`Sub`, `Mul`, `GreaterEqual`, ...); as a constraint, e.g. `s > "a"`, the message is `PSSDomain.reduceDomainGreaterThan()`: `PSSStringDomain::reduceDomainGreaterThan is not implemented`. |
| 9.6.1 / 8.6.1 | Bit-select and part-select, read and assigned | supported | | tests/issues/32 | Part-select assignment is in tests/issues/33, bit-selects in tests/samples/9_6_Primary_expressions/test_bit_selects. |
| 9.6.2 / 8.6.2 | Indexing arrays, lists and maps, also on call results `m.values()[0]` | supported | | tests/samples/test_function_chaining | |

## Components and actions

| LRM (2.0 / 2.1) | Construct | Status | Message | Test | Notes |
|---|---|---|---|---|---|
| 10.4, 10.5 / 9.3, 9.4 | Components, component instances, `C::name` type references | supported | | tests/samples/test_pss2.0_example169_name_resolution_inside_with_constraint_block | |
| 10.6 / 9.5 | `comp` of an action, `this` | supported | | tests/samples/test_this | |
| 10 / 9 | Integer field with a value domain in a component, e.g. `bit[4] in [1..2] x;` | unsupported | `PSSModel.addConstraint()`: `PSSComponentModel::addConstraint is not implemented` | | A package-level `const` field with a domain stops the same way, naming its scope's class. |
| 11.3.1, 11.3.2 / 10.2.1, 10.2.2 | Atomic actions with `exec body`; compound actions with `activity`; rand fields; root action from `-root` | supported | | tests/samples/test_demo | |
| 11.1 / 10.1 | Compound action that also has an `exec body` | partial | | tests/issues/17 (skipped) | Accepted; the LRM forbids it. The test's model is also illegal by name resolution. It is skipped because the expected output needs an activity `foreach` to see a list element that an earlier sub-action's `post_solve` appends, and PSSGen expands the `foreach` first. |
| 4.2, 21.3 / 4.2, 20.3 | Two fields with the same name in one type | partial | | tests/samples/PSSv2.1_7.9_Collections/maps/test_map_initialization_assignment | #48: not reported. |

## Activities

| LRM (2.0 / 2.1) | Construct | Status | Message | Test | Notes |
|---|---|---|---|---|---|
| 13.3.1 / 12.3.1 | `do T`, handle traversal `h;`, `with { ... }` | supported | | tests/samples/test_demo | |
| 13.3.4 / 12.3.4 | `parallel` | unsupported | `PSSGenVisitor.visitActivity_parallel_stmt()`: `Syntax is not yet supported: '...'` | | |
| 13.3.5 / 12.3.5 | `schedule` | supported | | tests/samples/test_demo | |
| 13.4.1 / 12.4.1 | `repeat (n)`, `repeat (i : n)` | supported | | tests/samples/test_repeat_count | |
| 13.4.3 / 12.4.3 | `foreach` over a collection, with or without an index | supported | | tests/samples/test_reference_path_resolution | |
| 13.4.4 / 12.4.4 | `select` with weights and guards | supported | | tests/samples/test_demo | |
| 13.4.5 / 12.4.5 | `if` / `else` | supported | | tests/samples/test_demo | |
| 13.4.6 / 12.4.6 | `match` | supported | | tests/samples/test_match | |
| 13.5.1 / 12.5.1 | `replicate` | unsupported | `PSSGenVisitor.visitActivity_replicate_stmt()`: `Syntax is not yet supported: '...'` | | |
| 13.6 / 12.6 | `super;` in an activity | unsupported | `PSSGenVisitor.visitActivity_super_stmt()`: `Syntax is not yet supported: '...'` | | |
| 13.7 / 12.7 | `symbol` declaration | unsupported | `PSSGenVisitor.visitSymbol_declaration()`: `Syntax is not yet supported: '...'` | | |
| 13.7 / 12.7 | Symbol call | unsupported | `PSSGenVisitor.visitSymbol_call()`: `Syntax is not yet supported: '...'` | | |
| 13.2, 17.2 / 12.2, 16.2 | Action handle or data field declared in an activity; `constraint` statement or scheduling constraint in an activity | unsupported | `PSSGenVisitor.visitActivity_stmt()`: `Syntax is not yet supported: '...'` | tests/errors/activity-handle-declaration | |
| 13.2 / 12.2 | Empty statement `;` in an activity (e.g. `do A with {...};;`, `if (...) {...};`) | partial | `[SYNTAX] '...'` | tests/issues/18 | #51: the LRM allows it. tests/issues/23 also uses `if (1)`, which the LRM rejects; PSSGen reports the `;` first. |
| 13.9 / 12.9 | `bind` of buffer objects | supported | | tests/samples/test_hello_world | |

## Flow objects, resources and pools

| LRM (2.0 / 2.1) | Construct | Status | Message | Test | Notes |
|---|---|---|---|---|---|
| 14.1, 14.4 / 13.1, 13.4 | `buffer` objects, `input` / `output`, constraints on buffer fields | supported | | tests/samples/test_buffer_constraint | |
| 18.1 / 17.1 | Inferred producer action for an unbound input | supported | | tests/samples/test_action_inference | |
| 16 / 15 | `pool` declaration | unsupported | `PSSGenVisitor.visitComponent_pool_declaration()`: `Syntax is not yet supported: '...'` | | |

## Randomization and constraints

| LRM (2.0 / 2.1) | Construct | Status | Message | Test | Notes |
|---|---|---|---|---|---|
| 17.1.1, 17.1.4 / 16.1.1, 16.1.4 | Member constraints, named and unnamed, with `==`, relational, `&&`, `\|\|` and `in` expressions | supported | | tests/samples/test_with_constraint_ops | |
| 17.1.4 / 16.1.4 | Constraint whose expression, or an operand of its `&&` / `\|\|`, is not a comparison or `in`, e.g. `x + 1;`, `!(x == 1);` | unsupported | `PSSExpression.deduceDomain()`: `PSSAddExpression.deduceDomain() is not implemented` | | The class names the kind of expression. |
| 17.1.3 / 16.1.3 | In-line `with` constraints, including names of the containing action and `comp` | supported | | tests/samples/test_pss2.0_example169_name_resolution_inside_with_constraint_block | |
| 21.3 / 20.3 | Member constraint that names a field of the traversing action | partial | | tests/issues/27 | #49: resolved to the other action's field, with `PSS-WARNING: Use 'in' expression to non-random variable in constraint block is meaningless: ...`; the LRM makes it an error. |
| 17.1.5 / 16.1.5 | Implication `a -> b` | unsupported | `PSSGenVisitor.visitImplication_constraint_item()`: `Syntax is not yet supported: '...'` | | |
| 17.1.6 / 16.1.6 | `if` / `else if` / `else` constraints | supported | | tests/samples/test_if_constraint_item | |
| 17.1.7 / 16.1.7 | `foreach` constraints | supported | | tests/issues/42 | |
| 17.1.8 / 16.1.8 | `forall` | unsupported | `PSSGenVisitor.visitForall_constraint_item()`: `Syntax is not yet supported: '...'` | | |
| 17.1.9 / 16.1.9 | `unique` | supported | | tests/samples/test_unique | |
| 17.1.10 / 16.1.10 | `default x == c;` that no other constraint contradicts | supported | | tests/samples/16_1_10_Default_value_constraints/test_default_basic | |
| 17.1.10 / 16.1.10 | `default x == c;` that another constraint contradicts | partial | | tests/issues/15 | The default is dropped; the LRM requires a contradiction to be reported. tests/samples/16_1_10_Default_value_constraints/test_override expects the same behavior. |
| 17.1.10 / 16.1.10 | Several default constraints on one field | partial | | | #50: the LRM precedence rules (outer context, derived type, later declaration) are not applied. |
| 17.1.10 / 16.1.10 | Default constraint under `if` with a constant condition | partial | `PSSRandom.limitRetry()`: `randomize fail` | tests/issues/31 (skipped) | Both branches' defaults apply, whatever the condition; this is why the test is skipped. |
| 17.1.10 / 16.1.10 | `default disable x;` | unsupported | `PSSGenVisitor.visitConstraint_body_item()`: `Syntax is not yet supported: '...'` | | #50 |
| 17.4.4 / 16.4.5 | Constraining `comp` of a traversed action | supported | | tests/samples/test_pss2.0_example169_name_resolution_inside_with_constraint_block | |
| 17.4.10 / 16.4.12 | `exec pre_solve` and `exec post_solve` in actions | supported | | tests/samples/test_if_constraint_item | |

## Coverage

| LRM (2.0 / 2.1) | Construct | Status | Message | Test | Notes |
|---|---|---|---|---|---|
| 19.1 / 18.1 | `covergroup` declaration | unsupported | `PSSGenVisitor.visitCovergroup_declaration()`: `Syntax is not yet supported: '...'` | | |

## Type inheritance, extension and overrides

| LRM (2.0 / 2.1) | Construct | Status | Message | Test | Notes |
|---|---|---|---|---|---|
| 20.1 / 19.1 | `super.x` in an expression or in a template `{{super.x}}` | unsupported | `PSSGenVisitor.visitRef_path()`: `super in ref_path is not implemented` | | In a template the message comes while the output file is written. |
| 20.2 / 19.2 | `extend` of any type, enum extension included | unsupported | `PSSGenVisitor.visitExtend_stmt()`: `Syntax is not yet supported: '...'` | | |
| 20.5 / 19.5 | `override` block | unsupported | `PSSGenVisitor.visitOverride_declaration()`: `Syntax is not yet supported: '...'` | | |

## Source organization

| LRM (2.0 / 2.1) | Construct | Status | Message | Test | Notes |
|---|---|---|---|---|---|
| 21 / 20 | Several input files; file list `-f`; library directory `-y` | supported | | tests/samples/test_filelist | |
| 21 / 20 | Enums, structs and functions declared outside any component | supported | | tests/samples/test_pss2.0_example315_parameter_passing_semantics | |
| 21.1 / 20.1 | Field whose type name resolves to a package | unsupported | `PSSModel.declInst()`: `PSSPackageModel::declInst is not implemented` | | Not legal PSS. |
| 23.3 / 22.3 | `compile has` | unsupported | `PSSGenVisitor.visitPrimary()`: `Syntax is not yet supported: '...'` | | |

## Exec blocks, functions and procedural statements

| LRM (2.0 / 2.1) | Construct | Status | Message | Test | Notes |
|---|---|---|---|---|---|
| 22.1.3 / 21.1.2 | `exec init_down` and `exec init_up` in components | supported | | tests/samples/test_comp_init | |
| 22.1.3 / 21.1.2 | `exec run_start`, `exec run_end` in an action | unsupported | `[ACTION] run_start is not yet supported` | | `run_end` prints `run_end`. |
| 22.1 / 21.1 | Any `exec` block in a `struct`, `buffer`, `stream`, `state` or `resource` | unsupported | `PSSModel.addExecBlock()`: `PSSStructModel::addExecBlock is not implemented` | | A target-template exec (`exec body L = "...";`) there ends in a Java ClassCastException instead. |
| 22.1.5 / 21.1.4 | `super;` in an exec block | unsupported | `PSSGenVisitor.visitExec_super_stmt()`: `Syntax is not yet supported: '...'` | | |
| 22.2, 22.3 / 21.2, 21.3 | Native functions: prototype then definition, recursion, default parameter values, `void` calls, `return` | supported | | tests/samples/test_native_functions | |
| 22.2.2, 22.3.3 / 21.2.2, 21.3.2 | `ref action`, `ref component`, `struct` parameters; aggregates passed by handle; struct and collection return values | supported | | tests/samples/test_ref_function_parameters | Also tests/samples/test_aggregate_function_parameters. |
| 22.4 / 21.4 | Calling a function that has a prototype only (implemented outside PSS) | unsupported | `PSSFunctionInst.eval()`: `Invocation of an external function is not implemented yet` | | |
| 22.7.1, 22.7.2 / 21.7.1, 21.7.2 | Scoped blocks and local variables | supported | | tests/samples/test_proc_stmt_scope | |
| 22.7.6, 22.7.7 / 21.7.6, 21.7.7 | `repeat`, `repeat (i : n)`, `repeat ... while`, `while` | supported | | tests/samples/test_break | |
| 22.7.8 / 21.7.8 | `foreach` | supported | | tests/samples/test_continue | |
| 22.7.8 / 21.7.8 | Names in a `foreach` body that never runs | partial | | tests/samples/PSSv2.1_7.9_Collections/sets/test_set_operator_foreach | Undeclared names are not reported while the collection is empty. |
| 22.7.9, 22.7.10 / 21.7.9, 21.7.10 | `if` / `else`, `match` | supported | | tests/samples/test_match | |
| 22.7.11 / 21.7.11 | `break`, `continue`; either one outside a loop is reported | supported | | tests/errors/break-outside-loop | |

## Target templates

| LRM (2.0 / 2.1) | Construct | Status | Message | Test | Notes |
|---|---|---|---|---|---|
| 22.5 / 21.5 | `exec header`, `exec declaration`, `exec body` templates; several blocks of one kind | supported | | tests/samples/test_target_code | |
| 22.5.3 / 21.5.3 | `{{field}}`, `{{a.b.c}}`, `{{comp.x}}`, `{{a[0]}}` for int, bit, bool and string fields | supported | | tests/samples/test_comp_init | |
| 22.5.3 / 21.5.3 | `{{e}}` for an enum field | partial | | tests/samples/PSSv2.1_7.9_Collections/maps/test_map_operator_index | Printed as the item's integer value; the LRM example prints the item name. |
| 22.5.3 / 21.5.3 | `{{x}}` for a collection or struct field | unsupported | `PSSInst.toTargetCode()`: `PSSListInst::toTargetCode is not implemented` | | The class names the kind of the field. The LRM defines no text form for these types. |
| 22.5.3 / 21.5.3 | `{{s[0]}}` on a string field | unsupported | `PSSInst.indexOf()`: `PSSStringInst::indexOf is not implemented` | | |

Other Fatal messages of the form `<Class>::<method> is not implemented` or
`<Class>.<method>() is not implemented`, and the `Syntax is not yet supported`
branches of `PSSGenVisitor.visitCasting_type()` and
`PSSGenVisitor.visitOpen_range_value()` that no parse reaches, are internal:
no PSS construct is known to reach them. Reaching one is a PSSGen bug; please
report it with the input that caused it.

## Inputs with several unsupported constructs

Before it builds the model, PSSGen looks for the following constructs in all
input files. Each is rejected wherever it appears, so their presence alone
decides that the input cannot be generated:

| Construct | Message of a single occurrence |
|---|---|
| `extend` | `PSSGenVisitor.visitExtend_stmt()` |
| `override` block | `PSSGenVisitor.visitOverride_declaration()` |
| `pool` declaration | `PSSGenVisitor.visitComponent_pool_declaration()` |
| `covergroup` declaration | `PSSGenVisitor.visitCovergroup_declaration()` |
| `typedef` | `PSSGenVisitor.visitTypedef_declaration()` |
| `super;` in an exec block | `PSSGenVisitor.visitExec_super_stmt()` |
| `symbol` declaration | `PSSGenVisitor.visitSymbol_declaration()` |
| `default disable x;` | `PSSGenVisitor.visitConstraint_body_item()` |
| `forall` | `PSSGenVisitor.visitForall_constraint_item()` |
| Implication `a -> b` | `PSSGenVisitor.visitImplication_constraint_item()` |
| `parallel` | `PSSGenVisitor.visitActivity_parallel_stmt()` |
| Handle, data field, `constraint` or scheduling constraint in an activity | `PSSGenVisitor.visitActivity_stmt()` |
| Symbol call | `PSSGenVisitor.visitSymbol_call()` |
| `replicate` | `PSSGenVisitor.visitActivity_replicate_stmt()` |
| `super;` in an activity | `PSSGenVisitor.visitActivity_super_stmt()` |

All carry the text `Syntax is not yet supported: '...'`. An input with one of
them stops with that Fatal message, as for any other unsupported construct.
An input with two or more stops once, with one PSS-ERROR that lists every
occurrence in file order, without the occurrences nested inside another one:

```
[0.123s] PSS-ERROR: [UNSUPPORTED] 2 unsupported constructs:
test.pss:2:4: PSSGenVisitor.visitTypedef_declaration(): Syntax is not yet supported: 'typedefbit[8]byte_t;'
test.pss:6:12: PSSGenVisitor.visitActivity_parallel_stmt(): Syntax is not yet supported: 'parallel{dosub_a;dosub_a;}'
```

Each line gives file, line and column (counted from 0) of the construct. The
exit status is 1. tests/errors/unsupported-several and
tests/errors/unsupported-several-files show the message.
