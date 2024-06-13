include ${PSS_HOME}/issue/Make.vars

gentarget:
	$(PSSGEN) test.pss -o test.s -root pss_top::root_a || true

clean:
	rm -f test.s result.log
