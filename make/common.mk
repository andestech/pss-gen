# Settings shared by src/Makefile and tests/Makefile: the checkout (PSS_HOME), the
# release name (file VERSION), the installed jar, native executable and ANTLR jar,
# and the JDK (jdk.mk).

# PSS_HOME defaults to the checkout that contains this file.
ifndef PSS_HOME
PSS_HOME := $(abspath $(dir $(lastword $(MAKEFILE_LIST)))..)
endif

TAGS := $(strip $(shell cat $(PSS_HOME)/VERSION 2>/dev/null))
ifeq ($(TAGS),)
$(error Cannot read $(PSS_HOME)/VERSION; check PSS_HOME)
endif
PSSGEN_NAME = pssgen_$(TAGS)

ANTLR4_JAR_PATH = $(PSS_HOME)/antlr4/antlr-4.9.3-complete.jar
PSSGEN_JAR_PATH = $(PSS_HOME)/src/$(PSSGEN_NAME).jar
PSSGEN_BIN_PATH = $(PSS_HOME)/src/$(PSSGEN_NAME)

include $(PSS_HOME)/make/jdk.mk
