# Settings shared by src/Makefile and tests/Makefile: the checkout (PSS_HOME), the
# build name (make/build-name.sh), the installed jar, native executable and ANTLR jar,
# and the JDK (jdk.mk).

# PSS_HOME defaults to the checkout that contains this file.
ifndef PSS_HOME
PSS_HOME := $(abspath $(dir $(lastword $(MAKEFILE_LIST)))..)
endif

BUILD_NAME := $(strip $(shell bash $(PSS_HOME)/make/build-name.sh $(PSS_HOME)))
ifeq ($(BUILD_NAME),)
$(error Cannot get the build name from $(PSS_HOME)/VERSION; check PSS_HOME)
endif
PSSGEN_NAME = pssgen_$(BUILD_NAME)

ANTLR4_JAR_PATH = $(PSS_HOME)/antlr4/antlr-4.9.3-complete.jar
PSSGEN_JAR_PATH = $(PSS_HOME)/src/$(PSSGEN_NAME).jar
PSSGEN_BIN_PATH = $(PSS_HOME)/src/$(PSSGEN_NAME)

include $(PSS_HOME)/make/jdk.mk
