.PHONY: test clean

IGNORE_LIST = "4_6_Numbers\|4_7_String_literals\|test_enum"

SUBDIRS =
TEST_SUBDIRS = $(shell find ./ -name "test.pss" -type f | grep -wv $(IGNORE_LIST) | sed 's/test\.pss//g' | sort)
ALL_SUBDIRS = $(shell find ./ -name "test.pss" -type f | sed 's/test\.pss//g' | sort)
ARG =

test:
	for i in $(TEST_SUBDIRS); do \
		$(MAKE) -C $$i $@; \
	done
	make result

result:
	@$(eval total := $(shell find ./ -name "test.pss" -type f | grep -wv $(IGNORE_LIST) | wc -l))
	@$(eval pass  := $(shell find ./ -name "result.log" -type f | grep -wv $(IGNORE_LIST) | xargs grep PASS | wc -l))
	@$(eval fail  := $(shell find ./ -name "result.log" -type f | grep -wv $(IGNORE_LIST) | xargs grep FAIL | wc -l))
	@$(eval loss  := $(shell let "loss = $(total) - $(pass) - $(fail)" ; echo $$loss))

	@echo --------------------------------------------------------
	@echo TOTAL=$(total), PASS=$(pass), FAIL=$(fail), LOSS=$(loss)
	@echo --------------------------------------------------------

	@for i in $(TEST_SUBDIRS); do \
		RESULT_LOG=$$i"result.log"; \
		if [ -f $$RESULT_LOG ]; then \
			grep -H "[PASS|FAIL]" $$RESULT_LOG; \
		else \
			echo $$RESULT_LOG":LOSS"; \
		fi \
	done

clean:
	for i in $(ALL_SUBDIRS); do $(MAKE) -C $$i $@; done
