# JDK that src/Makefile, samples/ and issue/ use to build, test and run PSSGen.
# Included after PSS_HOME is set.
#
# When GraalVM CE is installed at GRAALVM_PATH, its JDK is the default, so the
# jar, the regression runs and the native image of "make bin" all come from one
# JDK. Without it, java on PATH is used (Gradle takes JAVA_HOME first), so any
# JDK 17 to 27 builds and runs PSSGen; GraalVM is only required for "make bin".
#
# Overrides (make command line or environment):
#   PSS_JDK=<jdk dir>    use that JDK
#   PSS_JDK=             use java on PATH even when GraalVM is installed
#   GRAALVM_PATH=<dir>   bin directory of GraalVM (command line)

GRAALVM_PATH = $(PSS_HOME)/graalvm-community-openjdk-17.0.9+9.1/bin

PSS_JDK ?= $(if $(wildcard $(GRAALVM_PATH)/java),$(abspath $(GRAALVM_PATH)/..))

JAVA = $(if $(PSS_JDK),$(PSS_JDK)/bin/java,java)
