
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
The **$PSS_HOME/src/pssgen.jar** should generated.


# Run Example
```
$ cd $PSS_HOME/samples
$ make
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
