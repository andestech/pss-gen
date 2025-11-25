SHELL := /bin/bash
include ./Make.vars

IGNORE_LIST := ./4_6_Numbers/% ./4_7_String_literals ./test_enum
SUBDIRS := $(filter-out $(IGNORE_LIST), $(shell find . -mindepth 2 -type f -name 'Makefile' | xargs -I {} dirname {} | sort))
SUMMARY := summary.log

.PHONY: all run-subdirs summary clean

all: run-subdirs summary

run-subdirs:
	@for i in $(SUBDIRS); do \
		$(MAKE) -C $$i || exit 1; \
	done

$(SUMMARY):
	@rm -f $@
	@for i in $(SUBDIRS); do \
		f="$$i/$(RESULT_LOG)"; \
		if [ -f $$f ]; then \
			echo "$$i: $$(grep -E 'PASS|FAIL' $$f || echo 'NO RESULT')" >> $@; \
		else \
			echo "$$i: NO RESULT_LOG" >> $@; \
		fi; \
	done

summary: $(SUMMARY)
	@echo "--------------------------------------------------------"; \
	cat $<; \
	total=$$(find . -mindepth 2 -type f -name 'Makefile' | wc -l); \
	pass=$$(grep -Pc 'PASS' $<); \
	fail=$$(grep -Pc 'FAIL|NO.RESULT|NO.RESULT_LOG' $<); \
	skip=$$(( $$total - $$pass - $$fail )); \
	echo "--------------------------------------------------------"; \
	echo "TOTAL=$$total, PASS=$$pass, FAIL=$$fail, SKIP=$$skip"; \
	echo "--------------------------------------------------------"

clean:
	@for i in $(SUBDIRS); do \
		$(MAKE) -C $$i $@; \
	done
	-rm -f $(SUMMARY)
