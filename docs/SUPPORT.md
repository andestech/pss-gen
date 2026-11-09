# PSSGen Language Support

PSSGen reads a PSS description, randomly solves the constraints of the root
action and of the actions it traverses, and writes the expanded `exec`
target templates to a test file. This document lists, per chapter of the
Accellera Portable Test and Stimulus Standard (LRM), which constructs PSSGen
handles and how.

LRM references are given as `2.0 / 2.1`: the PSS 2.0 section first (the
edition the PSSGen sources cite), then the PSS 2.1 section, which is numbered
one chapter lower from the data types on.

## How To Read The Entries

Each entry names a construct in bold, followed by its status, and lists below it
the LRM sections and, where they apply, a Message, a Test and Notes.

Status:

* 🟢 **supported** - works as the LRM describes, as far as the listed test shows.
* 🟡 **partial** - accepted, but the behavior deviates from the LRM in the way the
  Notes describe. These entries are set apart in a warning box.
* 🔴 **unsupported** - PSSGen stops with the message under Message.

A construct that has no entry has no test. PSSGen may accept it, but
nothing checks what it does.

Message:

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

Test: a directory under `tests/` whose `test.pss` uses the construct.
"(skipped)" marks a test whose `test.cfg` has a `skip =` line; the harness does
not run it, and the Notes give the reason. #nn names a GitHub issue.

## Lexical Conventions

- **Comments `//` and `/* */`**: 🟢 supported
  - LRM: 4.1 / 4.1
  - Test: tests/samples/test_demo

- **Sized and based numbers (`8'b01101010`, `2'b10`, `'h8045`)**: 🟢 supported
  - LRM: 4.6 / 4.6
  - Test: tests/issues/35
  - Notes: `'h` numbers are in tests/samples/test_demo (softconfig.pss).

- **A digit string that is no legal number, e.g. `01234567890`**: 🟢 supported
  - LRM: 4.6 / 4.6
  - Test: tests/samples/4_6_Numbers/test_octal (skipped)
  - Notes: Rejected with `[SYNTAX] '...'`, as the LRM requires. The test is skipped because its own input contains this literal; its golden also has wrong values.

- **Quoted `"..."` and triple-quoted `"""..."""` strings**: 🟢 supported
  - LRM: 4.7 / 4.7
  - Test: tests/samples/test_multi_constraint

- **Escape sequences in quoted strings, in expressions and in target templates**: 🟢 supported
  - LRM: 4.7 / 4.7
  - Message: `[PSS 2.0 Section 4.7] illegal escape sequence '...' in string literal ...`
  - Test: tests/samples/4_7_String_literals
  - Notes: Each escape of Table 3 becomes one character; an escape not in the table is the error under Message. A quoted target template is inserted without its quotes. Triple-quoted strings have no escapes.

- **Empty `{}`, value list `{1, 2}` and map `{1: 2}` literals**: 🟢 supported
  - LRM: 4.8.1-4.8.3 / 4.8.1-4.8.3
  - Test: tests/samples/test_function_chaining

- **Structure literal `{.a = 1}`**: 🔴 unsupported
  - LRM: 4.8.4 / 4.8.4
  - Message: `PSSGenVisitor.visitAggregate_literal()`: `Syntax is not yet supported: '...'`

## Data Types

- **`bit` and `int` with a width `[N]` or `[msb:lsb]`, rand and non-rand**: 🟢 supported
  - LRM: 8.2 / 7.2
  - Test: tests/samples/test_repeat_count
  - Notes: The width must be a number literal.

- **Value domain of ranges on the type: `int in [1..5]`, `[100..]`, `[..10]`, `[10..11, 30..31]`**: 🟢 supported
  - LRM: 8.2 / 7.2
  - Test: tests/issues/14

- **Value domain on the element type of an array or list, `array<bit[8] in [1..3], 4> a`, `list<int in [0..7]> l`, and on an array field `bit[8] in [1..3] a[4]`**: 🟢 supported
  - LRM: 8.2, 8.8 / 7.2, 7.9
  - Test: tests/samples/test_domain_elements
  - Notes: Every element is kept in the domain.

- **Value domain on the element, key or value type of a `set` or `map`, e.g. `set<bit[8] in [1..3]> s`, `map<bit[8] in [10..12], int> m`**: 🟢 supported
  - LRM: 8.2, 8.8 / 7.2, 7.9
  - Test: tests/samples/test_domain_set_map, tests/errors/set-element-domain
  - Notes: Every element, key and value is kept in the domain. As for a list, the collection is not random, so the check warns that the constraint is meaningless, and a value outside the domain ends in `randomize fail`.

- **Value domain item that is a single value: `bit in [1]`, `int in [0, 1]`**: 🔴 unsupported
  - LRM: 8.2 / 7.2
  - Message: `PSSGenVisitor.visitDomain_open_range_value()`: `Syntax is not yet supported: '...'`
  - Test: tests/samples/7_2_Integer_types/test_int, test_multi_bit, test_single_bit (skipped)
  - Notes: Only in a type declaration; `x in [0, 1]` in a constraint is supported. This message is why the three tests are skipped.

> [!WARNING]
> **`int` without a width; a value stored into a signed field**: 🟡 partial
> - LRM: 8.2 / 7.2
> - Test: tests/samples/4_6_Numbers/test_binary, test_decimal, test_hexadecimal (skipped)
> - Notes: PSSGen makes `int` 65 bits signed instead of 32, so it holds every 64-bit value, signed or unsigned: `int c = (1 << 63) - 1` is 9223372036854775807. A value stored into an `int` or `int[N]` field, or cast to a signed type, is not wrapped to its width: `int[8] a = 255` holds 255, not -1. This is why the three tests are skipped; their goldens also contain values the LRM does not give. See README, Known limitations.

- **`bool`, rand and non-rand; printed as `true`/`false` in templates**: 🟢 supported
  - LRM: 8.3 / 7.4
  - Test: tests/samples/7_4_Booleans/test_bool_type

- **Non-rand `int`, `bit`, `bool` or `string` field without initializer, referenced in a template or an expression**: 🟢 supported
  - LRM: 8.2, 8.3, 8.5 / 7.2, 7.4, 7.6
  - Test: tests/samples/7_4_Booleans/test_bool
  - Notes: The LRM default value: 0, `false`, `""`.

- **`enum` with and without item values, at top level and in a component; rand enum fields; `e::ITEM`**: 🟢 supported
  - LRM: 8.4 / 7.5
  - Test: tests/samples/test_enum_declaration
  - Notes: An item also initializes a component data field (tests/samples/test_comp_field_enum_init). An item has the value it is written with at any size, so `0xFFFFFFFE` is 4294967294; the fields of an enum type with an item outside the 32-bit signed range are as wide as `int` (tests/samples/test_enum_item_wide).

- **Non-rand enum field without initializer, referenced in a template**: 🟢 supported
  - LRM: 8.4 / 7.5
  - Test: tests/samples/test_enum (skipped)
  - Notes: The first enum item, the LRM default. The test is skipped because its template names `outVal_2`, which the test does not declare.

- **Enum type with a domain `e in [A, B]`**: 🔴 unsupported
  - LRM: 8.4 / 7.5
  - Message: `PSSGenVisitor.visitScalar_data_type()`: `Syntax is not yet supported: '...'`

- **`string`; rand strings constrained with `==` and `in [...]`**: 🟢 supported
  - LRM: 8.5 / 7.6
  - Test: tests/samples/test_with_multi_constraint

- **String type with a domain `string in ["a", "b"]`**: 🔴 unsupported
  - LRM: 8.5 / 7.6
  - Message: `PSSGenVisitor.visitString_type()`: `Syntax is not yet supported: '...'`

- **`chandle`**: 🔴 unsupported
  - LRM: 8.6 / 7.7
  - Message: `PSSGenVisitor.visitScalar_data_type()`: `Syntax is not yet supported: '...'`

- **`struct`: fields, nested field access, struct as function parameter and return value**: 🟢 supported
  - LRM: 8.7 / 7.8
  - Test: tests/issues/13
  - Notes: Comparison of struct values: see Operators.

- **Arrays `T a[N]` and `array<T,N>`: initialization, assignment, `==`, `!=`, `in`, `[]`, `foreach`, `size()`, `sum()`, `to_list()`, `to_set()`**: 🟢 supported
  - LRM: 8.8.2 / 7.9.2
  - Test: tests/samples/PSSv2.1_7.9_Collections/arrays

- **`list<T>`: initialization, assignment, `==`, `!=`, `in`, `[]`, `foreach`, `size`, `clear`, `delete`, `insert`, `pop_back`, `pop_front`, `push_back`, `push_front`, `shuffle`, `to_set`**: 🟢 supported
  - LRM: 8.8.3 / 7.9.3
  - Test: tests/samples/PSSv2.1_7.9_Collections/lists

- **`map<K,V>`: initialization, assignment, `==`, `!=`, `[]`, `foreach`, `clear`, `delete`, `insert`, `keys`, `size`, `values`**: 🟢 supported
  - LRM: 8.8.4 / 7.9.4
  - Test: tests/samples/PSSv2.1_7.9_Collections/maps
  - Notes: Nested type parameter lists may end in `>>`, as in `map<int, list<int>>` (tests/samples/test_nested_type_close). In a map literal, `{}` and a value list literal `{1, 2}` are values of a list, set or map value type (tests/samples/test_map_nested_empty, test_map_set_literal).

- **`set<T>`: initialization, assignment, `==`, `!=`, `in`, `foreach`, `clear`, `delete`, `insert`, `size`, `to_list`**: 🟢 supported
  - LRM: 8.8.5 / 7.9.5
  - Test: tests/samples/PSSv2.1_7.9_Collections/sets

- **Reference type `ref T` as a field or variable type**: 🔴 unsupported
  - LRM: 8.9 / 7.10
  - Message: `PSSGenVisitor.visitData_type()`: `Syntax is not yet supported: '...'`
  - Notes: `ref action`, `ref component` function parameters are supported (see Functions).

- **`null`**: 🔴 unsupported
  - LRM: 8.9 / 7.10
  - Message: `PSSGenVisitor.visitPrimary()`: `Syntax is not yet supported: '...'`

- **`typedef`**: 🔴 unsupported
  - LRM: 8.10 / 7.11
  - Message: `PSSGenVisitor.visitTypedef_declaration()`: `Syntax is not yet supported: '...'`

- **Cast to an integer type `(bit[4]) x`**: 🟢 supported
  - LRM: 8.11 / 7.12
  - Test: tests/issues/35

- **Cast to `bool`**: 🔴 unsupported
  - LRM: 8.11 / 7.12
  - Message: `PSSGenVisitor.visitCasting_type()`: `Cast to bool_type is not yet supported: '...'`

- **Cast to an enum or other named type `(e) x`**: 🔴 unsupported
  - LRM: 8.11 / 7.12
  - Message: `PSSGenVisitor.visitCasting_type()`: `Cast to enum_type is not yet supported: '...'`
  - Notes: Every `(<name>) x` cast takes this path.

- **Integer cast of a non-numeric field, e.g. a string**: 🔴 unsupported
  - LRM: 8.11 / 7.12
  - Message: `PSSCastExpression.eval()`: `Casting < PSSStringInst > is not yet support.`
  - Notes: The class names the kind of the operand.

- **Integer cast of an expression that refers to no field, e.g. `(bit[8]) (1 + 2)`**: 🔴 unsupported
  - LRM: 8.11 / 7.12
  - Message: `PSSCastExpression.eval()`: `Casting < PSSAddExpression > ( ... ) is not yet support.`
  - Notes: The class names the kind of the operand.

## Operators And Expressions

- **Assignment `=`, `+=`, `-=`, `|=`**: 🟢 supported
  - LRM: 9.3 / 8.3
  - Test: tests/samples/test_repeat_count

- **`+ - * / %`**: 🟢 supported
  - LRM: 9.5.1 / 8.5.1
  - Message: `[PSS 2.0 Section 9.5.1] division by zero: ...`, `[PSS 2.0 Section 9.5.1] modulus by zero: ...`
  - Test: tests/samples/test_native_functions
  - Notes: `/` is in tests/samples/test_aggregate_function_parameters, `%` in tests/samples/test_continue. A zero right operand is the error under Message, as the LRM requires (tests/errors/division-by-zero, modulus-by-zero). `/` truncates toward zero and `%` takes the sign of the first operand (tests/samples/test_mod_sign).

- **Power `**`**: 🔴 unsupported
  - LRM: 9.5.1 / 8.5.1
  - Message: `PSSExpression.eval()`: `PSSBinaryExpression.eval() is not implemented`
  - Notes: In a constraint the text is `PSSBinaryExpression.deduceDomain() is not implemented`.

- **`< <= > >=`, `&&`, `||`**: 🟢 supported
  - LRM: 9.5.2, 9.5.4 / 8.5.2, 8.5.4
  - Test: tests/samples/test_with_constraint_ops
  - Notes: `<=` and `>=` are in tests/samples/test_aggregate_function_parameters.

- **`==`, `!=` on numbers, strings, enums and collections of scalars**: 🟢 supported
  - LRM: 9.5.3 / 8.5.3
  - Test: tests/samples/PSSv2.1_7.9_Collections/lists/test_list_operator_equality

- **`==`, `!=` and `in` on struct values, also through arrays and lists of structs**: 🟢 supported
  - LRM: 9.5.3, 9.5.9 / 8.5.3, 8.5.9
  - Test: tests/issues/46
  - Notes: Compared field by field.

- **`~`, and `|` through `|=`**: 🟢 supported
  - LRM: 9.5.5 / 8.5.5
  - Test: tests/issues/22
  - Notes: `|=` is in tests/samples/9_6_Primary_expressions/test_bit_selects.

- **Reduction `&x`, `|x`, `^x`**: 🔴 unsupported
  - LRM: 9.5.6 / 8.5.6
  - Message: `PSSGenVisitor.visitExpression()`: `unary operator '...'is not yet supported`
  - Notes: No space before `is` in the message.

- **`<<`**: 🟢 supported
  - LRM: 9.5.7 / 8.5.7
  - Test: tests/issues/35

> [!WARNING]
> **Shift whose result is assigned to a wider target, e.g. `ans = src << 8` with `bit[8] src` and `bit[32] ans`**: 🟡 partial
> - LRM: 9.7.1 / 8.7.1
> - Test: tests/issues/52
> - Notes: `<<` keeps the bits shifted past its operand's width, and an unsigned target truncates the result to its own width, so the example gives 27136. As for every operator, intermediate results are not truncated to the width of the assignment: `(src << 4) >> 4` assigned to a `bit[8]` gives 106 where the LRM gives 10. A signed target `int[N]` keeps a value outside its N bits.

- **Conditional `c ? a : b`**: 🔴 unsupported
  - LRM: 9.5.8 / 8.5.8
  - Message: `[VISITOR-0001] visitExpression is not fully implemented yet`

- **`x in [a, b..c]` and `x in collection`**: 🟢 supported
  - LRM: 9.5.9 / 8.5.9
  - Test: tests/issues/20

> [!WARNING]
> **Unqualified enum item on the left of `in`**: 🟡 partial
> - LRM: 9.5.9 / 8.5.9
> - Message: `[PSS 2.0 Section 9.5.9] The left-hand side of 'in' shall not be an unqualified enum item: '...'`
> - Test: tests/issues/47
> - Notes: Rejected, as the LRM requires, also in parentheses `(ITEM) in ...` (tests/errors/in-lhs-paren); `e::ITEM in ...` is accepted. The check runs when the expression is evaluated, so an `in` in a branch or body that never runs is not reported.

- **Operators other than `==`, `!=`, `in` on strings**: 🔴 unsupported
  - LRM: 9.5 / 8.5
  - Message: `PSSVal.Add()`: `PSSStringVal::Add is not implemented`
  - Notes: The LRM defines no other string operator, so the input is illegal. Each operator names its own method (`Sub`, `Mul`, `GreaterEqual`, ...); as a constraint, e.g. `s > "a"`, the message is `PSSDomain.reduceDomainGreaterThan()`: `PSSStringDomain::reduceDomainGreaterThan is not implemented`.

- **Bit-select and part-select, read and assigned**: 🟢 supported
  - LRM: 9.6.1 / 8.6.1
  - Test: tests/issues/32
  - Notes: Part-select assignment is in tests/issues/33, bit-selects in tests/samples/9_6_Primary_expressions/test_bit_selects.

- **Indexing arrays, lists and maps, also on call results `m.values()[0]`**: 🟢 supported
  - LRM: 9.6.2 / 8.6.2
  - Test: tests/samples/test_function_chaining

## Components And Actions

- **Components, component instances, `C::name` type references**: 🟢 supported
  - LRM: 10.4, 10.5 / 9.3, 9.4
  - Test: tests/samples/test_pss2.0_example169_name_resolution_inside_with_constraint_block

> [!WARNING]
> **`comp` of an action, `this`**: 🟡 partial
> - LRM: 10.6 / 9.5
> - Test: tests/samples/test_this
> - Notes: `comp` of an action declared in another component is the first instance of that component type under the traversing action's component (tests/samples/test_comp_context). When the tree has no such instance, `comp` stays the traversing action's component; the model is not rejected.

- **Integer field with a value domain in a component, e.g. `bit[4] in [1..2] x;`**: 🔴 unsupported
  - LRM: 10 / 9
  - Message: `PSSModel.addConstraint()`: `PSSComponentModel::addConstraint is not implemented`
  - Notes: A package-level `const` field with a domain stops the same way, naming its scope's class.

- **Atomic actions with `exec body`; compound actions with `activity`; rand fields; root action from `-root`**: 🟢 supported
  - LRM: 11.3.1, 11.3.2 / 10.2.1, 10.2.2
  - Test: tests/samples/test_demo

> [!WARNING]
> **Compound action that also has an `exec body`**: 🟡 partial
> - LRM: 11.1 / 10.1
> - Test: tests/issues/17 (skipped)
> - Notes: Accepted and not reported, although the LRM forbids it: the body's output comes before the output of the activity. See README, Known limitations. The test's model is also illegal by name resolution. It is skipped because the expected output needs an activity `foreach` to see a list element that an earlier sub-action's `post_solve` appends, and PSSGen expands the `foreach` first.

- **`abstract action`**: 🔴 unsupported
  - LRM: 11.3.3 / 10.2.3
  - Message: `PSSGenVisitor.visitAbstract_action_declaration()`: `Syntax is not yet supported: '...'`
  - Test: tests/errors/inheritance
  - Notes: An abstract action is only usable as a base type, and inheritance is not supported.

> [!WARNING]
> **Two fields with the same name in one type, flow-object and resource-object references included**: 🟡 partial
> - LRM: 4.2, 21.3 / 4.2, 20.3
> - Message: `[PSS 2.0 Section 4.2] '...' is declared more than once in '...'`
> - Test: tests/issues/48, tests/errors/duplicate-flow-ref
> - Notes: Rejected, as the LRM requires. Both branches of a `compile if` are read, so a field declared in a `compile if` branch of its type is not reported.

## Activities

> [!WARNING]
> **`do T`, handle traversal `h;`, `with { ... }`**: 🟡 partial
> - LRM: 13.3.1 / 12.3.1
> - Test: tests/samples/test_demo
> - Notes: Each traversal of a handle selects new values (tests/samples/test_handle_retraverse). A handle traversed more than once in one sequential, parallel or schedule scope (13.3.1 c)) is not reported.

> [!WARNING]
> **A field of a traversed action handle read by a later activity expression, e.g. `h; repeat (h.n) ...`**: 🟡 partial
> - LRM: 17.4.6 / 16.4.8
> - Test: tests/samples/test_handle_field_in_activity
> - Notes: Reads the handle's solved values, post_solve updates included. A handle traversed again, in a loop or a later block, is randomized anew with the in-line constraints of that traversal only, and a later read gets the most recent values (tests/samples/test_handle_retraverse). A read before the first traversal, or while a sub-action up to the handle waits for a flow object that a later `bind` or inference supplies, is reported as `[PSSMemberPathElemExpression] ... is not defined.`

- **`parallel`**: 🔴 unsupported
  - LRM: 13.3.4 / 12.3.4
  - Message: `PSSGenVisitor.visitActivity_parallel_stmt()`: `Syntax is not yet supported: '...'`

- **`schedule`**: 🟢 supported
  - LRM: 13.3.5 / 12.3.5
  - Test: tests/samples/test_demo

- **`repeat (n)`, `repeat (i : n)`**: 🟢 supported
  - LRM: 13.4.1 / 12.4.1
  - Test: tests/samples/test_repeat_count

- **`foreach` over a collection, with or without an index**: 🟢 supported
  - LRM: 13.4.3 / 12.4.3
  - Test: tests/samples/test_reference_path_resolution

- **`select` with weights and guards**: 🟢 supported
  - LRM: 13.4.4 / 12.4.4
  - Test: tests/samples/test_demo

- **`if` / `else`**: 🟢 supported
  - LRM: 13.4.5 / 12.4.5
  - Message: `[PSS 2.0 Section 13.4.5] the condition '...' of an activity if shall be of type bool`
  - Test: tests/samples/test_demo
  - Notes: A condition that is not bool, e.g. `if (1)`, is the error under Message when the `if` is reached.

- **`match`**: 🟢 supported
  - LRM: 13.4.6 / 12.4.6
  - Test: tests/samples/test_match

- **`replicate`**: 🔴 unsupported
  - LRM: 13.5.1 / 12.5.1
  - Message: `PSSGenVisitor.visitActivity_replicate_stmt()`: `Syntax is not yet supported: '...'`

- **`super;` in an activity**: 🔴 unsupported
  - LRM: 13.6 / 12.6
  - Message: `PSSGenVisitor.visitActivity_super_stmt()`: `Syntax is not yet supported: '...'`

- **`symbol` declaration**: 🔴 unsupported
  - LRM: 13.7 / 12.7
  - Message: `PSSGenVisitor.visitSymbol_declaration()`: `Syntax is not yet supported: '...'`

- **Symbol call**: 🔴 unsupported
  - LRM: 13.7 / 12.7
  - Message: `PSSGenVisitor.visitSymbol_call()`: `Syntax is not yet supported: '...'`

- **Action handle or data field declared in an activity; `constraint` statement or scheduling constraint in an activity**: 🔴 unsupported
  - LRM: 13.2, 17.2 / 12.2, 16.2
  - Message: `PSSGenVisitor.visitActivity_stmt()`: `Syntax is not yet supported: '...'`
  - Test: tests/errors/activity-handle-declaration

> [!WARNING]
> **Empty statement `;` in an activity (e.g. `do A with {...};;`, `if (...) {...};`)**: 🟡 partial
> - LRM: 13.2 / 12.2
> - Message: `[SYNTAX] '...'`
> - Test: tests/issues/18
> - Notes: #51: the LRM allows it. tests/issues/23 also uses `if (1)`, which the LRM rejects; PSSGen reports the `;` first.

- **`bind` of buffer objects**: 🟢 supported
  - LRM: 13.9 / 12.9
  - Test: tests/samples/test_hello_world

## Flow Objects, Resources And Pools

- **`buffer` objects, `input` / `output`, constraints on buffer fields**: 🟢 supported
  - LRM: 14.1, 14.4 / 13.1, 13.4
  - Test: tests/samples/test_buffer_constraint

- **Inferred producer action for an unbound input**: 🟢 supported
  - LRM: 18.1 / 17.1
  - Test: tests/samples/test_action_inference

- **`pool` declaration**: 🔴 unsupported
  - LRM: 16 / 15
  - Message: `PSSGenVisitor.visitComponent_pool_declaration()`: `Syntax is not yet supported: '...'`

## Randomization And Constraints

- **Member constraints, named and unnamed, with `==`, relational, `&&`, `||` and `in` expressions**: 🟢 supported
  - LRM: 17.1.1, 17.1.4 / 16.1.1, 16.1.4
  - Test: tests/samples/test_with_constraint_ops

- **Constraint whose expression, or an operand of its `&&` / `||`, is not a comparison or `in`, e.g. `x + 1;`, `!(x == 1);`**: 🔴 unsupported
  - LRM: 17.1.4 / 16.1.4
  - Message: `PSSExpression.deduceDomain()`: `PSSAddExpression.deduceDomain() is not implemented`
  - Notes: The class names the kind of expression.

- **In-line `with` constraints, including names of the containing action and `comp`**: 🟢 supported
  - LRM: 17.1.3 / 16.1.3
  - Test: tests/samples/test_pss2.0_example169_name_resolution_inside_with_constraint_block

> [!WARNING]
> **A name in an action that is a field of an action traversing it**: 🟡 partial
> - LRM: 21.3 / 20.3
> - Test: tests/issues/27
> - Notes: #49: accepted, although the LRM makes it an error. The name resolves to the field of the nearest traversing action that has one, up to the root action, in member constraints, activities, exec blocks, templates and field initializers, and an in-line `with` constraint also reaches past the containing action. A member constraint `in` on such a field warns `PSS-WARNING: Use 'in' expression to non-random variable in constraint block is meaningless: ...`. See README, Known limitations.

- **Unqualified enum item where an enum is expected, while a field or local has the same name**: 🟢 supported
  - LRM: 21.3 / 20.3
  - Test: tests/samples/test_enum_expected_type, test_enum_expected_in_match_args, test_enum_expected_return
  - Notes: The item wins, as 21.3 a) requires, on the right of `=` to an enum field, in an enum field's initializer, opposite an enum field in `==` and `!=`, in the `[...]` of `in` and of a `match` choice on an enum field, in a function argument for an enum parameter, and in the `return` statement of a function returning an enum (9.4.3). A cast to an enum type is unsupported (see the 8.11 entries).

- **Implication `a -> b`**: 🔴 unsupported
  - LRM: 17.1.5 / 16.1.5
  - Message: `PSSGenVisitor.visitImplication_constraint_item()`: `Syntax is not yet supported: '...'`

- **`if` / `else if` / `else` constraints**: 🟢 supported
  - LRM: 17.1.6 / 16.1.6
  - Test: tests/samples/test_if_constraint_item

- **`foreach` constraints**: 🟢 supported
  - LRM: 17.1.7 / 16.1.7
  - Test: tests/issues/42

- **`forall`**: 🔴 unsupported
  - LRM: 17.1.8 / 16.1.8
  - Message: `PSSGenVisitor.visitForall_constraint_item()`: `Syntax is not yet supported: '...'`

- **`unique`**: 🟢 supported
  - LRM: 17.1.9 / 16.1.9
  - Test: tests/samples/test_unique

- **`default x == c;` that no other constraint contradicts**: 🟢 supported
  - LRM: 17.1.10 / 16.1.10
  - Test: tests/samples/16_1_10_Default_value_constraints/test_default_basic

- **`default x == c;` that another constraint contradicts**: 🟢 supported
  - LRM: 17.1.10 / 16.1.10
  - Message: `[OVERCONSTRAINT] '...' is over-constrained`
  - Test: tests/issues/15
  - Notes: Reported as a contradiction, as the LRM requires; also tests/samples/16_1_10_Default_value_constraints/test_override.

> [!WARNING]
> **Several default constraints on one field**: 🟡 partial
> - LRM: 17.1.10 / 16.1.10
> - Message: `[OVERCONSTRAINT] '...' is over-constrained`
> - Notes: #50: the LRM precedence rules (outer context, derived type, later declaration) are not applied; two different defaults on one field are reported as a contradiction.

> [!WARNING]
> **Default constraint under `if` / `else` with a constant condition**: 🟡 partial
> - LRM: 17.1.10 / 16.1.10
> - Test: tests/issues/31
> - Notes: Only the defaults of the branch the condition selects apply. A condition on a random field, which the LRM forbids, is not reported; it is evaluated against the field's value before and after the draw.

- **`default disable x;`**: 🔴 unsupported
  - LRM: 17.1.10 / 16.1.10
  - Message: `PSSGenVisitor.visitConstraint_body_item()`: `Syntax is not yet supported: '...'`
  - Notes: #50

- **Constraining `comp` of a traversed action**: 🟢 supported
  - LRM: 17.4.4 / 16.4.5
  - Test: tests/samples/test_pss2.0_example169_name_resolution_inside_with_constraint_block

- **`exec pre_solve` and `exec post_solve` in actions**: 🟢 supported
  - LRM: 17.4.10 / 16.4.12
  - Test: tests/samples/test_if_constraint_item

## Coverage

- **`covergroup` declaration**: 🔴 unsupported
  - LRM: 19.1 / 18.1
  - Message: `PSSGenVisitor.visitCovergroup_declaration()`: `Syntax is not yet supported: '...'`

## Type Inheritance, Extension And Overrides

- **Base type `: base` of a component, action, struct, flow object or resource**: 🔴 unsupported
  - LRM: 20.1 / 19.1
  - Message: `PSSGenVisitor.visitComponent_super_spec()`, `PSSGenVisitor.visitAction_super_spec()`, `PSSGenVisitor.visitStruct_super_spec()`: `Syntax is not yet supported: ':...'`
  - Test: tests/errors/inheritance, tests/errors/inheritance-action
  - Notes: Flow objects and resources use the struct message.

- **`super.x` in an expression or in a template `{{super.x}}`**: 🔴 unsupported
  - LRM: 20.1 / 19.1
  - Message: `PSSGenVisitor.visitRef_path()`: `super in ref_path is not implemented`
  - Notes: In a template the message comes while the output file is written.

- **`extend` of any type, enum extension included**: 🔴 unsupported
  - LRM: 20.2 / 19.2
  - Message: `PSSGenVisitor.visitExtend_stmt()`: `Syntax is not yet supported: '...'`

- **`override` block**: 🔴 unsupported
  - LRM: 20.5 / 19.5
  - Message: `PSSGenVisitor.visitOverride_declaration()`: `Syntax is not yet supported: '...'`

## Source Organization

- **Several input files; file list `-f`; library directory `-y`**: 🟢 supported
  - LRM: 21 / 20
  - Test: tests/samples/test_filelist

- **Enums, structs and functions declared outside any component**: 🟢 supported
  - LRM: 21 / 20
  - Test: tests/samples/test_pss2.0_example315_parameter_passing_semantics

- **`static const` field of a component, action or struct; `const` field of a package; read by name inside the type or a type nested in it, or as `T::name`, `pkg::name`**: 🟢 supported
  - LRM: 8.1.1, 21.1.1, 21.3 / 7.1.1, 20.1.1, 20.3
  - Message: `[] ... is read-only`
  - Test: tests/samples/test_static_const
  - Notes: The value is the initializer's, which may name enum items and other constants. Assigning to it is the error under Message. In a component or action it is also readable through an instance, e.g. `comp.name`.

- **Field whose type name resolves to a package**: 🔴 unsupported
  - LRM: 21.1 / 20.1
  - Message: `PSSModel.declInst()`: `PSSPackageModel::declInst is not implemented`
  - Notes: Not legal PSS.

- **`compile has`**: 🔴 unsupported
  - LRM: 23.3 / 22.3
  - Message: `PSSGenVisitor.visitPrimary()`: `Syntax is not yet supported: '...'`

## Exec Blocks, Functions And Procedural Statements

- **`exec init_down` and `exec init_up` in components**: 🟢 supported
  - LRM: 22.1.3 / 21.1.2
  - Test: tests/samples/test_comp_init

- **`exec run_start`, `exec run_end` in an action**: 🔴 unsupported
  - LRM: 22.1.3 / 21.1.2
  - Message: `[ACTION] run_start is not yet supported`
  - Notes: `run_end` prints `run_end`.

- **Any `exec` block in a `struct`, `buffer`, `stream`, `state` or `resource`**: 🔴 unsupported
  - LRM: 22.1 / 21.1
  - Message: `PSSModel.addExecBlock()`: `PSSStructModel::addExecBlock is not implemented`
  - Notes: A target-template exec (`exec body L = "...";`) there ends in a Java ClassCastException instead.

- **`super;` in an exec block**: 🔴 unsupported
  - LRM: 22.1.5 / 21.1.4
  - Message: `PSSGenVisitor.visitExec_super_stmt()`: `Syntax is not yet supported: '...'`

- **Native functions: prototype then definition, recursion, default parameter values, `void` calls, `return`**: 🟢 supported
  - LRM: 22.2, 22.3 / 21.2, 21.3
  - Test: tests/samples/test_native_functions
  - Notes: An unqualified call in an action names a function of the action's own component and runs on its `comp`, then a function of the enclosing scopes and imported packages; an action or other member of the same name is passed over (21.3 b), 22.2.1 b); tests/samples/test_comp_unqualified_call).

- **`ref action`, `ref component`, `struct` parameters; aggregates passed by handle; struct and collection return values**: 🟢 supported
  - LRM: 22.2.2, 22.3.3 / 21.2.2, 21.3.2
  - Test: tests/samples/test_ref_function_parameters
  - Notes: Also tests/samples/test_aggregate_function_parameters.

- **Calling a function that has a prototype only (implemented outside PSS)**: 🔴 unsupported
  - LRM: 22.4 / 21.4
  - Message: `PSSFunctionInst.eval()`: `Invocation of an external function is not implemented yet`

- **Scoped blocks and local variables**: 🟢 supported
  - LRM: 22.7.1, 22.7.2 / 21.7.1, 21.7.2
  - Test: tests/samples/test_proc_stmt_scope

- **`repeat`, `repeat (i : n)`, `repeat ... while`, `while`**: 🟢 supported
  - LRM: 22.7.6, 22.7.7 / 21.7.6, 21.7.7
  - Test: tests/samples/test_break

- **`foreach`**: 🟢 supported
  - LRM: 22.7.8 / 21.7.8
  - Test: tests/samples/test_continue

> [!WARNING]
> **Names in a `foreach` body that never runs**: 🟡 partial
> - LRM: 22.7.8 / 21.7.8
> - Test: tests/samples/PSSv2.1_7.9_Collections/sets/test_set_operator_foreach
> - Notes: Undeclared names are not reported while the collection is empty.

- **`if` / `else`, `match`**: 🟢 supported
  - LRM: 22.7.9, 22.7.10 / 21.7.9, 21.7.10
  - Test: tests/samples/test_match

- **`break`, `continue`; either one outside a loop is reported**: 🟢 supported
  - LRM: 22.7.11 / 21.7.11
  - Test: tests/errors/break-outside-loop

## Target Templates

- **`exec header`, `exec declaration`, `exec body` templates; several blocks of one kind**: 🟢 supported
  - LRM: 22.5 / 21.5
  - Test: tests/samples/test_target_code

- **`{{field}}`, `{{a.b.c}}`, `{{comp.x}}`, `{{a[0]}}` for int, bit, bool and string fields, and a call with no arguments, `{{l.size()}}`, `{{f()}}`**: 🟢 supported
  - LRM: 22.5.3 / 21.5.3
  - Test: tests/samples/test_template_language, test_template_method
  - Notes: In every target language: `int` signed decimal, `bit` unsigned decimal at any width, `bool` `true` / `false`, `string` as is (22.5.3.2).

- **`{{e}}` for an enum field**: 🟢 supported
  - LRM: 22.5.3 / 21.5.3
  - Test: tests/samples/test_template_language
  - Notes: A target language other than `ASM` prints the item name, as in the LRM example. `ASM` prints the item's integer value, which the generated assembly uses; the language is matched exactly, so `asm` prints names. An enum item named directly, `{{A}}` or `{{e_t::A}}`, and an enum field whose value is no item print the integer value.

- **`{{x}}` for a collection or struct field**: 🔴 unsupported
  - LRM: 22.5.3 / 21.5.3
  - Message: `PSSInst.toTargetCode()`: `PSSListInst::toTargetCode is not implemented`
  - Notes: The class names the kind of the field. The LRM defines no text form for these types.

- **`{{s[0]}}` on a string field**: 🔴 unsupported
  - LRM: 22.5.3 / 21.5.3
  - Message: `PSSInst.indexOf()`: `PSSStringInst::indexOf is not implemented`

Other Fatal messages of the form `<Class>::<method> is not implemented` or
`<Class>.<method>() is not implemented`, and the `Syntax is not yet supported`
branches of `PSSGenVisitor.visitCasting_type()` and
`PSSGenVisitor.visitOpen_range_value()` that no parse reaches, are internal:
no PSS construct is known to reach them. Reaching one is a PSSGen bug; please
report it with the input that caused it.

## Inputs With Several Unsupported Constructs

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
| Base type `: base` of a component | `PSSGenVisitor.visitComponent_super_spec()` |
| Base type `: base` of an action | `PSSGenVisitor.visitAction_super_spec()` |
| Base type `: base` of a struct, flow object or resource | `PSSGenVisitor.visitStruct_super_spec()` |
| `abstract action` | `PSSGenVisitor.visitAbstract_action_declaration()` |
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
