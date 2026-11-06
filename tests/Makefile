# Regression tests; run-tests.sh describes a test and how it is checked.
#   make                               every suite, with the jar
#   make bin                           every suite, with the native executable
#   make SUITES=issues                 the named suites only
#   make TESTS=samples/test_hello_world   the named tests or suites only
#   make clean                         removes the outputs of the tests
#   make compare BASE=<jar|vX.Y.Z> [NEW=<jar>]   runs the tests with two versions and reports
#                                      the differences (compare-versions.sh); a tag BASE is the
#                                      jar of that release, downloaded once; NEW defaults to the
#                                      jar of this checkout; SUITES and TESTS select the tests
# Build the jar (and for "make bin" the native executable) with "make -C src" first.
SHELL := /bin/bash

TESTS_DIR := $(abspath $(dir $(lastword $(MAKEFILE_LIST))))

include $(TESTS_DIR)/../make/common.mk

SUITES ?= samples issues cli errors
TESTS  ?= $(SUITES)

RUN_TESTS = $(TESTS_DIR)/run-tests.sh
COMPARE = $(TESTS_DIR)/compare-versions.sh

NEW ?= $(PSSGEN_JAR_PATH)
COMPARE_DIR = $(PSS_HOME)/build/compare
TAG_RE = ^v(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)$$

PSSGEN_JAR = $(JAVA) -classpath $(PSSGEN_JAR_PATH):$(ANTLR4_JAR_PATH) PSSGenMain

.PHONY: all bin clean compare

all:
	@test -f $(PSSGEN_JAR_PATH) || { echo "No $(PSSGEN_JAR_PATH); run 'make -C src' first." >&2; exit 2; }
	@cd $(TESTS_DIR) && PSSGEN='$(PSSGEN_JAR)' $(RUN_TESTS) $(TESTS)

bin:
	@test -x $(PSSGEN_BIN_PATH) || { echo "No $(PSSGEN_BIN_PATH); run 'make -C src bin' first." >&2; exit 2; }
	@cd $(TESTS_DIR) && PSSGEN='$(PSSGEN_BIN_PATH)' $(RUN_TESTS) $(TESTS)

clean:
	@cd $(TESTS_DIR) && $(RUN_TESTS) --clean $(TESTS)

# The repository slug (owner/name) is GITHUB_REPOSITORY, else the URL of origin, https or ssh form.
compare:
	@test -n '$(BASE)' || { echo "usage: make compare BASE=<jar|vX.Y.Z> [NEW=<jar>] [SUITES=...|TESTS=...]" >&2; exit 2; }
	@test -f '$(NEW)' || { echo "No $(NEW); run 'make -C src' first or set NEW=<jar>." >&2; exit 2; }
	@set -e; base='$(BASE)'; golden=""; \
	if [[ "$$base" =~ $(TAG_RE) ]]; then \
	    tag=$$base; jar=$(COMPARE_DIR)/jars/pssgen_$$tag.jar; golden="--golden-rev $$tag"; \
	    if [[ ! -f "$$jar" ]]; then \
	        slug="$$GITHUB_REPOSITORY"; \
	        [[ -n "$$slug" ]] || slug=$$(git -C $(PSS_HOME) remote get-url origin | sed -E 's#^(https?://[^/]+/|ssh://[^/]+/|[^@/]+@[^:/]+:)##; s#\.git$$##'); \
	        mkdir -p $(COMPARE_DIR)/jars; \
	        curl -fsSL -o "$$jar.part" "https://github.com/$$slug/releases/download/$$tag/pssgen_$$tag.jar"; \
	        mv "$$jar.part" "$$jar"; \
	    fi; \
	    base_name=$$tag; \
	else \
	    jar=$$base; test -f "$$jar" || { echo "No such jar: $$jar" >&2; exit 2; }; \
	    base_name=$$(basename "$$jar" .jar); \
	fi; \
	new_name=$$(basename '$(NEW)' .jar); \
	cd $(TESTS_DIR) && $(COMPARE) \
	    --base-cmd "$(JAVA) -classpath $$jar:$(ANTLR4_JAR_PATH) PSSGenMain" \
	    --new-cmd "$(JAVA) -classpath $(NEW):$(ANTLR4_JAR_PATH) PSSGenMain" \
	    --base-name "$$base_name" --new-name "$$new_name" \
	    --out "$(COMPARE_DIR)/$${base_name}_vs_$${new_name}" $$golden $(TESTS)
