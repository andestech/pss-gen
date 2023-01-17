PSS_HOME=/home/users1/alexchen/bin/pss-gen
ANTLR4_JAR_PATH = $(PSS_HOME)/antlr4/antlr-4.9.3-complete.jar
PSSGEN_JAR_PATH = $(PSS_HOME)/src/pssgen.jar
PSSGEN = java -classpath $(PSSGEN_JAR_PATH):$(ANTLR4_JAR_PATH) PSSGenMain

gentarget:
	$(PSSGEN) test.pss -o test.s -root pss_top::root_a || true

test: 	gentarget
	diff test.s test.s.golden; \
	diff=$$?; \
	if [ $$diff -eq 0 ]; then \
		echo PASS >| result.log; \
	else \
		echo FAIL >| result.log; \
	fi 

clean:
	rm -f test.s result.log
