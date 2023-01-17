.PHONY: test clean

SUBDIRS =
TEST_SUBDIRS = $(wildcard test_*)
ALL_SUBDIRS = $(SUBDIRS) $(TEST_SUBDIRS)
ARG =

test:
	for i in $(TEST_SUBDIRS); do \
		$(MAKE) -C $$i $@; \
	done
	make result

result:
	$(eval total := $(shell ls |grep test_ |wc -l))
	$(eval pass  := $(shell grep PASS test_*/result.log |wc -l))
	$(eval fail  := $(shell let "fail = $(total) - $(pass)" ; echo $$fail))

	@echo -----------------------------------------
	@echo TOTAL=$(total), PASS=$(pass), FAIL=$(fail)
	@echo -----------------------------------------

	grep "[PASS|FAIL]" test_*/result.log

clean:
	for i in $(ALL_SUBDIRS); do $(MAKE) -C $$i $@; done



