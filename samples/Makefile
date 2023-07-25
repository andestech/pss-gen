.PHONY: test clean

SUBDIRS =
TEST_SUBDIRS = $(shell find ./ -name "test.pss" -type f | sed 's/test\.pss//g' | sort)
ALL_SUBDIRS = $(SUBDIRS) $(TEST_SUBDIRS)
ARG =

test:
	for i in $(TEST_SUBDIRS); do \
		$(MAKE) -C $$i $@; \
	done
	make result

result:
	@$(eval total := $(shell find ./ -name "test.pss" -type f | wc -l))
	@$(eval pass  := $(shell find ./ -name "result.log" -type f | xargs grep PASS | wc -l))
	@$(eval fail  := $(shell find ./ -name "result.log" -type f | xargs grep FAIL | wc -l))
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



