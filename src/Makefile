TAGS = $(shell git describe --abbrev=0 --tags)
PSSGEN_NAME = pssgen_${TAGS}.jar

ANTLR4_JAR_PATH = ${PSS_HOME}/antlr4/antlr-4.9.3-complete.jar
PSSGEN_JAR_PATH = ${PSS_HOME}/src/${PSSGEN_NAME}


SRC_PATH = ${PSS_HOME}/src
#CLASSPATH="$ANTLR4_JAR_PATH:$SRC_PATH:$CLASSPATH"

ANTLR4 = java -jar $(ANTLR4_JAR_PATH)
GRUN = java -classpath $(PSSGEN_JAR_PATH):$(ANTLR4_JAR_PATH) org.antlr.v4.gui.TestRig PSS model -gui -f

JAVAC = javac --release 13 -classpath $(SRC_PATH):$(ANTLR4_JAR_PATH)

PSSGEN = java -classpath $(PSSGEN_JAR_PATH):$(ANTLR4_JAR_PATH) PSSGenMain


jar: guard-PSS_HOME
	$(ANTLR4) -no-listener -visitor PSS.g4;
	$(JAVAC) PSS*.java
	jar --create --file ${PSSGEN_NAME} --main-class PSSGenMain -c .

clean:
	rm $(SRC_PATH)/*.class -f

hello_world: jar
	$(PSSGEN) hello_world.pss -o test.s -root pss_top::hello_world_a

test: jar
	$(PSSGEN) test.pss -o test.s -root pss_top::root_a -seed $$RANDOM

test2: jar
	$(PSSGEN) -f pss.f

test3: jar
	$(PSSGEN) test2.pss softconfig.pss -o test.s -root pss_top::root_a -seed $$RANDOM

grun:
	$(GRUN) test.pss

guard-%:
	@ if [ "${${*}}" = "" ]; then \
		echo "Environment variable $* not set"; \
		exit 1; \
	fi


