TAGS = $(shell git describe --abbrev=0 --tags)
PSSGEN_NAME = pssgen_${TAGS}

GRAALVM_PATH = ${PSS_HOME}/graalvm-community-openjdk-17.0.9+9.1/bin
ANTLR4_JAR_PATH = ${PSS_HOME}/antlr4/antlr-4.9.3-complete.jar
PSSGEN_JAR_PATH = ${PSS_HOME}/src/${PSSGEN_NAME}.jar
PSSGEN_BIN_PATH = ${PSS_HOME}/src/${PSSGEN_NAME}

SRC_PATH = ${PSS_HOME}/src
#CLASSPATH="$ANTLR4_JAR_PATH:$SRC_PATH:$CLASSPATH"

NATIVE_IMAGE = $(GRAALVM_PATH)/native-image
ANTLR4 = java -jar $(ANTLR4_JAR_PATH)
GRUN = java -classpath $(PSSGEN_JAR_PATH):$(ANTLR4_JAR_PATH) org.antlr.v4.gui.TestRig PSS model -gui -f
JAVAC = javac --release 13 -classpath $(SRC_PATH):$(ANTLR4_JAR_PATH)

PSSGEN_JAR = java -classpath $(PSSGEN_JAR_PATH):$(ANTLR4_JAR_PATH) PSSGenMain
PSSGEN_BIN = $(PSSGEN_BIN_PATH)
PSSGEN = $(PSSGEN_JAR)

jar: guard-PSS_HOME
	$(ANTLR4) -no-listener -visitor PSS.g4;
	$(JAVAC) PSS*.java
	jar --create --file ${PSSGEN_JAR_PATH} --main-class PSSGenMain -c .

bin: jar
	if [ -x "$(NATIVE_IMAGE)" ]; then \
		$(NATIVE_IMAGE) -jar ${PSSGEN_JAR_PATH} -classpath $(ANTLR4_JAR_PATH); \
	else \
		echo "Skipping native-image; not executable: $(NATIVE_IMAGE)"; \
	fi

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
