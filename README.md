# PSSGen

PSSGen is a test generator for the Accellera Portable Test and Stimulus Standard (PSS).
It reads PSS source files, builds the action and component model, solves the random
constraints of the root action and its activity, and expands the `exec` templates of
the selected actions into one output file, typically an assembly test (`test.s`).

PSSGen is written in Java; the PSS parser is generated with ANTLR 4 from the grammar in
`src/main/antlr`. It is distributed as a jar and, on x86-64 Linux, as a native
executable built with GraalVM.

- [Quick start](#quick-start)
- [Requirements](#requirements)
- [Building](#building)
- [Usage](#usage)
- [Testing](#testing)
- [Continuous integration](#continuous-integration)
- [Project layout](#project-layout)
- [Releasing](#releasing)
- [Known limitations](#known-limitations)
- [License](#license)

## Quick start

```
$ git clone https://github.com/andestech/pss-gen.git
$ cd pss-gen
$ make -C src                    # builds src/pssgen_<version>.jar
$ make -C samples                # runs the sample regression
```

Generate a test from a PSS file:

```
$ java -classpath src/pssgen_v2.4.1.jar:antlr4/antlr-4.9.3-complete.jar \
       PSSGenMain samples/test_hello_world/test.pss -o test.s -root pss_top::root_a
```

## Requirements

| Purpose | Requirement |
|---|---|
| Build | JDK 17 to 27 (the range Gradle 9.8.0 supports), GNU Make 3.82 or later, bash |
| Run the jar | Java 13 or later (the classes target Java 13) |
| Native executable (optional) | GraalVM Community Edition 17.0.9 with `native-image` |
| First build | Network access to services.gradle.org (Gradle 9.8.0) and Maven Central (ANTLR 4.9.3) |

Gradle is not installed separately: the wrapper `./gradlew` downloads the pinned version,
verifies its checksum and keeps it in the Gradle user home (`~/.gradle` unless
`GRADLE_USER_HOME` is set), together with the ANTLR jar.

## Building

### Jar

```
$ make -C src
```

`make` runs `./gradlew jar installAntlrJar` (see `build.gradle`). It generates the parser
from `src/main/antlr/PSS.g4`, compiles `src/main/java` into `build/`, and installs

- `src/pssgen_<version>.jar`: PSSGen, without its dependencies;
- `antlr4/antlr-4.9.3-complete.jar`: the ANTLR runtime that the jar needs on the classpath.

`<version>` is the content of the file `VERSION`. `make -C src clean` removes `build/`.

### Native executable

```
$ make -C src bin
```

builds the jar and then `src/pssgen_<version>` with GraalVM `native-image`. When GraalVM is
not found, `make bin` prints `Skipping native-image` and builds the jar only.

### Selecting the JDK

The Makefiles in `src/`, `samples/` and `issue/` choose the JDK in `make/jdk.mk`:

1. `PSS_JDK=<jdk directory>` on the command line or in the environment, if given;
2. otherwise GraalVM, when it is installed at `tools/graalvm-community-openjdk-17.0.9+9.1`
   (or at `GRAALVM_PATH=<its bin directory>`): the jar, the regression runs and the
   native executable then all use the same JDK;
3. otherwise `java` on the `PATH` (Gradle uses `JAVA_HOME` first).

`make PSS_JDK=` forces case 3 even when GraalVM is installed.

To install GraalVM, download GraalVM Community Edition 17.0.9 from
https://github.com/graalvm/graalvm-ce-builds/releases/tag/jdk-17.0.9 and unpack it into
`tools/`. The directory is ignored by git.

### Proxy and offline builds

Java ignores the `http_proxy` and `https_proxy` variables. `make -C src` passes a proxy
given as `http://host:port` in these variables to Gradle. Proxy credentials and
`no_proxy` are not passed on; set them in `$GRADLE_USER_HOME/gradle.properties`
(`systemProp.https.proxyUser`, `systemProp.http.nonProxyHosts`, ...).

On a host without network access, either reuse a Gradle user home that has already run
a build, or build on another host and copy `src/pssgen_<version>.jar` and
`antlr4/antlr-4.9.3-complete.jar`.

### PSS_HOME

The Makefiles find the checkout they belong to, so `PSS_HOME` does not need to be set.
If it is set, it must point at a checkout that contains `VERSION`.

## Usage

```
java -classpath pssgen_<version>.jar:antlr-4.9.3-complete.jar PSSGenMain [options] file...
pssgen_<version> [options] file...
```

| Option | Meaning |
|---|---|
| `-o <file>` | Output file. Required. |
| `-f <file>` | Read options and input files from a file list (see below). |
| `-y <directory>` | Add every `*.pss` file in the directory as an input file. |
| `-root <action>` | Root action, as a qualified name. Default: `pss_top::root_a`. |
| `-seed <integer>` | Seed of the random generator (64-bit). Default: `1234`. |
| `-set_random_limit <integer>` | Maximum number of attempts per randomization. Default: `2000`. |
| `-info` | Print `PSS-INFO` messages. |
| `-debug` | Print `PSS-DEBUG` messages. |
| `-debug_random` | Print the randomization sequence; implies `-debug`. |
| `-help` | Print the usage text and exit. |

Every other argument is an input file. For a given set of input files, options and seed,
the output file is the same on every run.

### File list

A file list holds one entry per line. Empty lines and lines that start with `#` are
skipped, and `$NAME` is replaced by the value of the environment variable `NAME`. A line
with one word is an input file; a line with two words sets an option: `-o <file>`,
`-y <directory>` or `-root <action>`.

```
# pss.f
$PROJECT/pss/common.pss
test.pss
-o test.s
-root pss_top::root_a
```

### Messages and exit status

Messages are printed on standard output with the time since start:

```
[0.152s] PSS-INFO: parse file 'test.pss'
[0.731s] PSS-WARNING: ...
[0.006s] PSS-ERROR: [OPT] invalid value 'abc' for option '-seed': expected an integer
```

Some failures, such as a randomization that finds no solution within the limit of
`-set_random_limit`, are reported as `<Class>.<method>(), Ln<line>: <message>`.

| Exit status | Meaning |
|---|---|
| 0 | The output file was written, or `-help` printed the usage text. |
| 1 | An error was reported, or PSSGen was started without arguments. |

After an error, the output file may be missing or incomplete.

### Using PSSGen as a library

`PSSGenMain` exposes the stages `parseFiles`, `createModel`, `traverseRootAction` and
`realizeTest`. A failure inside them throws `PSSException` (a `RuntimeException`) instead
of ending the JVM: `getID()` is the error ID, `getMessage()` the message, `getReport()` the
text the command line would print and `getStatus()` its exit status.

## Testing

The regression tests live in `samples/` (language features) and `issue/` (reproductions
of reported issues). Each test is a directory with

- a `Makefile` that runs PSSGen on `test.pss` (or on the file list `pss.f`);
- `test.s.golden`, the expected output. A test without a golden file passes when PSSGen
  writes no output file.

Build the jar first, then run a suite from a clean state:

```
$ make -C src
$ make -C samples clean && make -C samples
$ make -C issue clean && make -C issue
```

`make bin` instead of `make` runs the native executable. Each suite prints one line per
test and writes them to `summary.log`, followed by a total:

```
./test_hello_world: PASS
...
TOTAL=<n>, PASS=<n>, FAIL=<n>, SKIP=<n>
```

Tests listed in `IGNORE_LIST` in `samples/Makefile` or `issue/Makefile` are skipped. A
suite's `make` exits 0 even when a test fails; use the check script to get a status:

```
$ bash .github/scripts/check-summary.sh samples issue
```

It exits 1 if a test is not PASS or a `summary.log` is missing. Run `make clean` before
running a suite again: an existing `test.s` is not regenerated.

## Continuous integration

`.github/workflows/ci.yml` runs on every push, on pull requests and on demand. Its job
runs on `ubuntu-24.04` with Temurin JDK 17: it builds the jar, runs both suites, fails
when `check-summary.sh` reports a test that is not PASS, and uploads `summary.log` and the
per-test `run.log`, `result.log` and `diff.log` as the artifact `regression-logs`, also
after a failure.

## Project layout

| Path | Content |
|---|---|
| `src/main/java/` | PSSGen sources (default package; entry point `PSSGenMain`) |
| `src/main/antlr/` | Grammar: `PSS.g4` and the `PSS_*.g4` files it imports |
| `src/Makefile` | `make` and `make bin`; the jar and the native executable are installed next to it |
| `make/jdk.mk` | JDK selection shared by `src/`, `samples/` and `issue/` |
| `samples/`, `issue/` | Regression tests |
| `antlr4/` | License of ANTLR; `make -C src` installs the ANTLR jar here |
| `docs/Doxyfile` | API documentation: `mkdir -p build && doxygen docs/Doxyfile` writes `build/doxygen/` |
| `build.gradle`, `settings.gradle`, `gradlew`, `gradle/` | Gradle build and wrapper |
| `.github/` | CI workflow, its check script and the issue templates |
| `tools/` | Local toolchains such as GraalVM; not stored in git |
| `build/` | Build output; not stored in git |

## Releasing

1. Set the new version, for example `v2.4.2`, in `VERSION`.
2. Build and test: `make -C src bin`, then both suites with `make` and `make bin`.
3. Commit, tag the commit with the same version, and publish
   `src/pssgen_<version>.jar` and `src/pssgen_<version>`.

## Known limitations

### `repeat (index : count)` and `this`

Inside `repeat`, `this` does not refer to the index variable. A constraint such as
`foo_val == this.foo_val` refers to `foo_val` of the enclosing action instead, and the
traversal fails:

```pss
action main {
    activity {
        repeat (foo_val : 5) {
            do foo with { foo_val == this.foo_val; };
        }
    }
}
```

Give the index a name that the called action does not use:

```pss
action main {
    activity {
        repeat (i : 5) {
            do foo with { foo_val == i; };
        }
    }
}
```

## License

PSSGen is released under the MIT License (see `LICENSE`). ANTLR is distributed under the
BSD 3-Clause License (see `antlr4/LICENSE.txt`); the native executable contains the ANTLR
runtime.
