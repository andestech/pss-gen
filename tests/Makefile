# Regression tests; run-tests.sh describes a test and how it is checked.
#   make                               every suite, with the jar
#   make bin                           every suite, with the native executable
#   make SUITES=issues                 the named suites only
#   make TESTS=samples/test_hello_world   the named tests or suites only
#   make clean                         removes the outputs of the tests
# Build the jar (and for "make bin" the native executable) with "make -C src" first.
SHELL := /bin/bash

TESTS_DIR := $(abspath $(dir $(lastword $(MAKEFILE_LIST))))

include $(TESTS_DIR)/../make/common.mk

SUITES ?= samples issues
TESTS  ?= $(SUITES)

RUN_TESTS = $(TESTS_DIR)/run-tests.sh

PSSGEN_JAR = $(JAVA) -classpath $(PSSGEN_JAR_PATH):$(ANTLR4_JAR_PATH) PSSGenMain

.PHONY: all bin clean

all:
	@test -f $(PSSGEN_JAR_PATH) || { echo "No $(PSSGEN_JAR_PATH); run 'make -C src' first." >&2; exit 2; }
	@cd $(TESTS_DIR) && PSSGEN='$(PSSGEN_JAR)' $(RUN_TESTS) $(TESTS)

bin:
	@test -x $(PSSGEN_BIN_PATH) || { echo "No $(PSSGEN_BIN_PATH); run 'make -C src bin' first." >&2; exit 2; }
	@cd $(TESTS_DIR) && PSSGEN='$(PSSGEN_BIN_PATH)' $(RUN_TESTS) $(TESTS)

clean:
	@cd $(TESTS_DIR) && $(RUN_TESTS) --clean $(TESTS)
