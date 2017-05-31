# Java binaries must be on the user's path
JAVAC=javac
JAVA=java

TERM=xterm

PROJ_BIN=./bin/
PROJ_SRC=./src/
PROJ_SCRIPTS=./scripts/

all: build-jputils build-jpclient build-jpserver

build-jputils:
	$(JAVAC) -d $(PROJ_BIN) $(PROJ_SRC)jputils/*.java

build-jpclient: build-jputils
	$(JAVAC) -d $(PROJ_BIN) -cp $(PROJ_BIN) $(PROJ_SRC)jpclient/*.java

build-jpserver: build-jputils
	$(JAVAC) -d $(PROJ_BIN) -cp $(PROJ_BIN) $(PROJ_SRC)jpserver/*.java

run: all
	$(TERM) -e $(PROJ_SCRIPTS)runServer.sh &
	$(TERM) -e $(PROJ_SCRIPTS)runClientOrchestrator.sh &

demo: all
	$(TERM) -e $(PROJ_SCRIPTS)runServer.sh &
	$(TERM) -e $(PROJ_SCRIPTS)runClientDemo.sh &

clean:
	rm -r bin/*
