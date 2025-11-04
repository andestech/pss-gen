
# Prerequisites

* bash
* GNU Make 3.82
* OpenJDK Runtime Environment (build 13.0.2+8)
* javac 13.0.2

# Setup 
```
$ export PSS_HOME=$PWD

```

# Build pssgen.jar
```
$ cd $PSS_HOME/src
$ make
```
The **$PSS_HOME/src/pssgen_vX.X.X.jar** should generated.


# Run Example
```
$ cd $PSS_HOME/samples
$ make
```

You should see the following logs.

```
--------------------------------------------------------
TOTAL=108, PASS=102, FAIL=0, SKIP=6
--------------------------------------------------------
./9_6_Primary_expressions/test_bit_selects: PASS
./9_6_Primary_expressions/test_indexing_array: PASS
./9_6_Primary_expressions/test_indexing_list: PASS
./9_6_Primary_expressions/test_indexing_map: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_declaration_square: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_declaration_template: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_initialization_assignment: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_method_size: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_method_sum: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_method_sum_float: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_method_to_list: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_method_to_set: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_operator_assignment: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_operator_equality: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_operator_foreach: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_operator_in: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_operator_index: PASS
./PSSv2.1_7.9_Collections/arrays/test_array_operator_inequality: PASS
./PSSv2.1_7.9_Collections/lists/test_list_declaration: PASS
./PSSv2.1_7.9_Collections/lists/test_list_initialization_assignment: PASS
./PSSv2.1_7.9_Collections/lists/test_list_method_clear: PASS
./PSSv2.1_7.9_Collections/lists/test_list_method_delete: PASS
./PSSv2.1_7.9_Collections/lists/test_list_method_insert: PASS
./PSSv2.1_7.9_Collections/lists/test_list_method_pop_back: PASS
./PSSv2.1_7.9_Collections/lists/test_list_method_pop_front: PASS
./PSSv2.1_7.9_Collections/lists/test_list_method_push_back: PASS
./PSSv2.1_7.9_Collections/lists/test_list_method_push_front: PASS
./PSSv2.1_7.9_Collections/lists/test_list_method_shuffle: PASS
./PSSv2.1_7.9_Collections/lists/test_list_method_size: PASS
./PSSv2.1_7.9_Collections/lists/test_list_method_to_set: PASS
./PSSv2.1_7.9_Collections/lists/test_list_operator_assignment: PASS
./PSSv2.1_7.9_Collections/lists/test_list_operator_equality: PASS
./PSSv2.1_7.9_Collections/lists/test_list_operator_foreach: PASS
./PSSv2.1_7.9_Collections/lists/test_list_operator_in: PASS
./PSSv2.1_7.9_Collections/lists/test_list_operator_index: PASS
./PSSv2.1_7.9_Collections/lists/test_list_operator_inequality: PASS
./PSSv2.1_7.9_Collections/maps/test_map_declaration: PASS
./PSSv2.1_7.9_Collections/maps/test_map_initialization_assignment: PASS
./PSSv2.1_7.9_Collections/maps/test_map_method_clear: PASS
./PSSv2.1_7.9_Collections/maps/test_map_method_delete: PASS
./PSSv2.1_7.9_Collections/maps/test_map_method_insert: PASS
./PSSv2.1_7.9_Collections/maps/test_map_method_keys: PASS
./PSSv2.1_7.9_Collections/maps/test_map_method_size: PASS
./PSSv2.1_7.9_Collections/maps/test_map_method_values: PASS
./PSSv2.1_7.9_Collections/maps/test_map_operator_assignment: PASS
./PSSv2.1_7.9_Collections/maps/test_map_operator_equality: PASS
./PSSv2.1_7.9_Collections/maps/test_map_operator_foreach: PASS
./PSSv2.1_7.9_Collections/maps/test_map_operator_index: PASS
./PSSv2.1_7.9_Collections/maps/test_map_operator_inequality: PASS
./PSSv2.1_7.9_Collections/sets/test_set_declaration: PASS
./PSSv2.1_7.9_Collections/sets/test_set_initialization_assignment: PASS
./PSSv2.1_7.9_Collections/sets/test_set_method_clear: PASS
./PSSv2.1_7.9_Collections/sets/test_set_method_delete: PASS
./PSSv2.1_7.9_Collections/sets/test_set_method_insert: PASS
./PSSv2.1_7.9_Collections/sets/test_set_method_size: PASS
./PSSv2.1_7.9_Collections/sets/test_set_method_to_list: PASS
./PSSv2.1_7.9_Collections/sets/test_set_operator_assignment: PASS
./PSSv2.1_7.9_Collections/sets/test_set_operator_equality: PASS
./PSSv2.1_7.9_Collections/sets/test_set_operator_foreach: PASS
./PSSv2.1_7.9_Collections/sets/test_set_operator_in: PASS
./PSSv2.1_7.9_Collections/sets/test_set_operator_inequality: PASS
./test_action_inference: PASS
./test_aggregate_function_parameters: PASS
./test_break: PASS
./test_buffer_constraint: PASS
./test_collection_array: PASS
./test_collection_list: PASS
./test_collection_lists: PASS
./test_collection_map: PASS
./test_collection_set: PASS
./test_collections: PASS
./test_comp_init: PASS
./test_constraint_by_cfg: PASS
./test_continue: PASS
./test_demo: PASS
./test_domain_open_range_list: PASS
./test_enum_declaration: PASS
./test_filelist: PASS
./test_function_chaining: PASS
./test_hello_world: PASS
./test_if_constraint_item: PASS
./test_if_in_constraint: PASS
./test_init_up: PASS
./test_match: PASS
./test_multi_constraint: PASS
./test_native_functions: PASS
./test_proc_stmt_scope: PASS
./test_pss2.0_example169_name_resolution_inside_with_constraint_block: PASS
./test_pss2.0_example23_map_operators_and_methods: PASS
./test_pss2.0_example315_parameter_passing_semantics: PASS
./test_ref_function_parameters: PASS
./test_reference_path_resolution: PASS
./test_repeat_action_inst: PASS
./test_repeat_count: PASS
./test_repeat_with_array: PASS
./test_square_array: PASS
./test_target_code: PASS
./test_this: PASS
./test_unique: PASS
./test_unique_with_array: PASS
./test_with_constraint_ops: PASS
./test_with_multi_constraint: PASS
```

---
# Limitation
#### 13.4.1 repeat(count)
##### Limit 1: Can't use **this** to target *index*_identifier of **repeat**.
**Example:**
```pss
action main {
    activity {
        repeat (foo_val:5) {
            do foo with { foo_val == this.foo_val; };
        }
    }
}

action foo {
    rand int foo_val;
    ...
}
```
In the example, we wish the built-in variable **this** will refer *index*_identifier of **repeat**.
However, it actually try to refer *foo_val* under *main* scope, therefore travesal fails.
We must avoid using same name between *index*_identifier of **repeat** and *variable*_identifier of callee action.

**Solution:**
```pss
action main {
    activity {
        repeat (i:5) {
            do foo with { foo_val == i; };
        }
    }
}

action foo {
    rand int foo_val;
    ...
}
```
