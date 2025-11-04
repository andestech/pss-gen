SHELL := /bin/bash
include ../Make.vars

FLIST      ?= pss.f 
SRC        ?= test.pss
OUT        ?= test.s
GOLDEN     ?= test.s.golden
LIB        += sub_test.pss
ARG        ?= 
RUN_LOG    ?= run.log
RESULT_LOG ?= result.log
DIFF_LOG   ?= diff.log

# Dynamically determine OUT if FLIST exists
HAS_FLIST := $(shell if [ -f "$(FLIST)" ]; then echo $(FLIST); else echo -1; fi)
ifeq ($(HAS_FLIST),$(FLIST))
OUT := $(shell grep -Po '\-o[ ]+.+' $(FLIST) | sed 's/^-o *//')
endif

# Shell functions for PASS/FAIL
PASS_FUNC = printf "$(GREEN)Test Result: PASS$(RESET)\n"; printf "$(GREEN)PASS$(RESET)\n" > $(RESULT_LOG)
FAIL_FUNC = printf "$(RED)Test Result: FAIL$(RESET)\n"; printf "$(RED)FAIL$(RESET)\n" > $(RESULT_LOG)

.PHONY: all clean check

all: $(OUT) check

# Generic rule for .s from .pss
%.s: %.pss
ifeq ($(HAS_FLIST), $(FLIST))
	@$(PSSGEN) -f $(FLIST) -root pss_top::root_a $(ARG) 2>&1 | tee $(RUN_LOG);
else
	@$(PSSGEN) $< -o $@ $(LIB) -root pss_top::root_a $(ARG) 2>&1 | tee $(RUN_LOG);
endif

check:
	@if [ -f "$(OUT)" ] && [ -f "$(GOLDEN)" ]; then \
		$(MAKE) $(RESULT_LOG); \
	elif [ ! -f "$(OUT)" ] && [ ! -f "$(GOLDEN)" ]; then \
		$(PASS_FUNC); \
	else \
		$(FAIL_FUNC); \
	fi

$(RESULT_LOG): $(GOLDEN) $(OUT)
	@diff --color=always --side-by-side --width=$${COLUMNS:-160} $(GOLDEN) $(OUT) > $(DIFF_LOG); \
	STATUS=$$?; \
	if [ $$STATUS -eq 0 ]; then \
		$(PASS_FUNC); \
	else \
		$(FAIL_FUNC); \
		printf "$(BLUE)%-$(shell expr $${COLUMNS:-160} / 2 - 4)s <-> %s$(RESET)\n" "$(GOLDEN)" "$(OUT)"; \
		cat $(DIFF_LOG); \
	fi

clean:
	-rm -f $(OUT) $(RUN_LOG) $(RESULT_LOG) $(DIFF_LOG)
