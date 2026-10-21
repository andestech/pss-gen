# Thin wrapper over the Gradle build in the repository root (../build.gradle):
#   make        builds $(PSS_HOME)/src/pssgen_<version>.jar
#   make bin    additionally builds the native image $(PSS_HOME)/src/pssgen_<version>
#   make unit-test  runs the unit tests (src/test/java); reports in $(PSS_HOME)/build/reports/tests
# The first build downloads Gradle and ANTLR. GRADLE_USER_HOME is honoured; the JDK
# is chosen in ../make/jdk.mk (GraalVM CE when installed, else java on PATH or JAVA_HOME).
SHELL := /bin/bash

# PSS_HOME, PSSGEN_NAME, the jar, native image and ANTLR jar paths, and JAVA.
include $(dir $(lastword $(MAKEFILE_LIST)))../make/common.mk

SRC_PATH = ${PSS_HOME}/src

NATIVE_IMAGE = $(GRAALVM_PATH)/native-image
GRUN = $(JAVA) -classpath $(PSSGEN_JAR_PATH):$(ANTLR4_JAR_PATH) org.antlr.v4.gui.TestRig pssgen.parser.PSS model -gui

# Java ignores http_proxy/https_proxy, so a "scheme://[user@]host:port" proxy
# from the environment is handed to Gradle as system properties. Credentials
# and no_proxy are not forwarded; they belong in $GRADLE_USER_HOME/gradle.properties
# (systemProp.https.proxyUser, systemProp.http.nonProxyHosts, ...).
proxy_hostport = $(subst :, ,$(patsubst %/,%,$(lastword $(subst @, ,$(subst ://, ,$(1))))))
proxy_flags = $(if $(filter 2,$(words $(call proxy_hostport,$(2)))),-D$(1).proxyHost=$(word 1,$(call proxy_hostport,$(2))) -D$(1).proxyPort=$(word 2,$(call proxy_hostport,$(2))))
GRADLE_PROXY_FLAGS := $(strip $(call proxy_flags,https,$(or $(https_proxy),$(HTTPS_PROXY))) $(call proxy_flags,http,$(or $(http_proxy),$(HTTP_PROXY))))

GRADLE_FLAGS ?= --no-daemon --console=plain
# gradlew itself downloads Gradle and reads -D options only up to the first
# non-option argument, so the proxy flags come first and -p <dir> last.
GRADLE = $(if $(PSS_JDK),JAVA_HOME=$(PSS_JDK)) $(PSS_HOME)/gradlew $(GRADLE_PROXY_FLAGS) $(GRADLE_FLAGS) -p $(PSS_HOME)

PSSGEN_JAR = $(JAVA) -classpath $(PSSGEN_JAR_PATH):$(ANTLR4_JAR_PATH) PSSGenMain
PSSGEN_BIN = $(PSSGEN_BIN_PATH)
PSSGEN = $(PSSGEN_JAR)

.PHONY: jar bin unit-test clean hello_world test test2 test3 grun

jar:
	$(GRADLE) jar installAntlrJar
	cp -f $(PSS_HOME)/build/libs/$(PSSGEN_NAME).jar $(PSSGEN_JAR_PATH)

unit-test:
	$(GRADLE) test

bin: jar
	if [ -x "$(NATIVE_IMAGE)" ]; then \
		$(NATIVE_IMAGE) -jar ${PSSGEN_JAR_PATH} -classpath $(ANTLR4_JAR_PATH); \
	else \
		echo "Skipping native-image; not executable: $(NATIVE_IMAGE)"; \
	fi

clean:
	rm -rf $(PSS_HOME)/build
	rm $(SRC_PATH)/*.class -f

hello_world: jar
	$(PSSGEN) hello_world.pss -o test.s -root pss_top::hello_world_a

test: jar
	$(PSSGEN) test.pss -o test.s -root pss_top::root_a -seed $$RANDOM

test2: jar
	$(PSSGEN) -f pss.f

test3: jar
	$(PSSGEN) test2.pss softconfig.pss -o test.s -root pss_top::root_a -seed $$RANDOM

grun:
	$(GRUN) test.pss
