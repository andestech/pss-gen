

# Setup 
```
$ export PSS_HOME=$PWD

```

# Build pssgen.jar
```
$ cd $PSS_HOME/src
$ make
```
The **$PSS_HOME/src/pssgen.jar** should generated.


# Run Example
```
$ cd $PSS_HOME/samples
$ source run.bash
```

You should see the following logs.

```
-----------------------------------------
TOTAL=25, PASS=25, FAIL=0
-----------------------------------------
grep "[PASS|FAIL]" test_*/result.log
test_action_inference/result.log:PASS
test_buffer_constraint/result.log:PASS
test_collection_array/result.log:PASS
test_collections/result.log:PASS
test_comp_init/result.log:PASS
test_constraint_by_cfg/result.log:PASS
test_demo/result.log:PASS
test_domain_open_range_list/result.log:PASS
test_enum/result.log:PASS
test_filelist/result.log:PASS
test_hello_world/result.log:PASS
test_if_constraint_item/result.log:PASS
test_if_in_constraint/result.log:PASS
test_init_up/result.log:PASS
test_multi_constraint/result.log:PASS
test_repeat_action_inst/result.log:PASS
test_repeat_count/result.log:PASS
test_repeat_with_array/result.log:PASS
test_square_array/result.log:PASS
test_target_code/result.log:PASS
test_this/result.log:PASS
test_unique/result.log:PASS
test_unique_with_array/result.log:PASS
test_with_constraint_ops/result.log:PASS
test_with_multi_constraint/result.log:PASS
```
