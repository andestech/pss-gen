
# Prerequisites

* bash
* GNU Make 3.82
* JDK 17 to 27 to build (the range Gradle 9.8.0 supports; tested with 17 and 26; `./gradlew` uses `JAVA_HOME`, otherwise `java` on the `PATH`)
* Java 13 or later to run (the classes target Java 13)
* Network access for the first build: it downloads Gradle 9.8.0 from services.gradle.org and ANTLR 4.9.3 from Maven Central. On a host without internet, build once on a connected host and copy `src/pssgen_vX.X.X.jar` and `antlr4/antlr-4.9.3-complete.jar`, or reuse a warm `GRADLE_USER_HOME`.
* GraalVM CE 17.0.9 (optional; see [Install GraalVM](#install-graalvm-optional))

# Setup
```
$ export PSS_HOME=$PWD
```
`PSS_HOME` is optional: the Makefiles default it to the checkout that contains them; if it is set, it must point at a checkout that contains the file `VERSION`.

Behind a proxy, export `https_proxy` (and `http_proxy`) as `http://host:port`; `make` in `src/` hands host and port to Gradle, because Java ignores these variables.
Proxy credentials and `no_proxy` are not forwarded; put them in `$GRADLE_USER_HOME/gradle.properties` (`systemProp.https.proxyUser`, `systemProp.http.nonProxyHosts`, ...).

Gradle keeps its distribution and the downloaded ANTLR jar in `GRADLE_USER_HOME` (default `~/.gradle`); export it to use another directory.

# Install GraalVM (optional)
If you want `make bin` to build a native image, install GraalVM CE 17.0.9 with `native-image`.

* Download GraalVM Community Edition from https://github.com/graalvm/graalvm-ce-builds/releases/tag/jdk-17.0.9
* Install GraalVM elsewhere and update `GRAALVM_PATH` in `src/Makefile` to the GraalVM `bin` directory.
* Make sure `native-image` is executable at the configured path.

# Build pssgen.jar
```
$ cd $PSS_HOME/src
$ make
```
The **$PSS_HOME/src/pssgen_vX.X.X.jar** should generated.

`make` wraps `./gradlew jar installAntlrJar` (see `build.gradle`): it generates the parser from `PSS.g4`, compiles `src/*.java` into `$PSS_HOME/build`, copies the jar to `src/` and installs the ANTLR jar as **$PSS_HOME/antlr4/antlr-4.9.3-complete.jar**.
`make clean` removes `$PSS_HOME/build` and needs no Gradle.

* The ANTLR jar and the generated parser are build outputs, not stored in git: run `make` in `src/` before running a regression.
* Run `make clean` in `samples/` or `issue/` before re-running a regression with a new build: an existing `test.s` is not regenerated.
* The release name `vX.X.X` comes from the file `VERSION`; bump it at each release, together with the release tag.

# Build native image (optional)
```
$ cd $PSS_HOME/src
$ make bin
```
The executable file **$PSS_HOME/src/pssgen_vX.X.X** should generated.

# Run Example
```
$ cd $PSS_HOME/samples
$ make
```

# Run Example with native image (optional)
```
$ cd $PSS_HOME/samples
$ make bin
```

You should see the following logs.

```
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
--------------------------------------------------------
TOTAL=108, PASS=102, FAIL=0, SKIP=6
--------------------------------------------------------
```

# Continuous integration
The GitHub Actions workflow `.github/workflows/ci.yml` runs on every push, when a pull request is opened, reopened or updated, and on demand (`workflow_dispatch`).
Its single job runs on `ubuntu-24.04` with Temurin JDK 17.
It builds the jar with `make -C src`, runs the regressions with `make -C samples` and `make -C issue`, and checks both `summary.log` files with `.github/scripts/check-summary.sh`.
Its last step runs even after a failed step, unless the run is cancelled, and uploads the `summary.log` files and the per-test `run.log`, `result.log` and `diff.log` that exist as the artifact `regression-logs`.

`make` in `samples/` and `issue/` exits 0 even when tests fail; it records the result of each test it runs (PASS, FAIL, NO RESULT or NO RESULT_LOG) in `summary.log`.
`.github/scripts/check-summary.sh` turns the two `summary.log` files into the job status: it prints how many tests passed, writes an `::error::` line for every test that is not PASS, and exits 1 if there is such a test or if a `summary.log` is missing or empty.
The job therefore fails as long as any test in `samples/summary.log` or `issue/summary.log` is not PASS.

In a checkout that has already run the regressions, first run `make clean` in `samples/` and `issue/`.
To run the build, the regressions and the check locally, from the top of the checkout:
```
$ make -C src
$ make -C samples
$ make -C issue
$ bash .github/scripts/check-summary.sh samples issue
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
