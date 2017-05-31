# Java binaries must be on the user's path
JAVAC=javac
JAVA=java

TERM=xterm

PROJ_BIN=./bin/
PROJ_SRC=./src/

all: build-jputils build-jpclient build-jpserver

build-jputils:
	$(JAVAC) -d $(PROJ_BIN) $(PROJ_SRC)jputils/*.java

build-jpclient: build-jputils
	$(JAVAC) -d $(PROJ_BIN) -cp $(PROJ_BIN) $(PROJ_SRC)jpclient/*.java

build-jpserver: build-jputils
	$(JAVAC) -d $(PROJ_BIN) -cp $(PROJ_BIN) $(PROJ_SRC)jpserver/*.java

run: all
	$(TERM) -e ./runServer.sh &
	$(TERM) -e ./runClientOrchestrator.sh &

demo: all
	$(TERM) -e ./runServer.sh &
	$(TERM) -e ./runClientDemo.sh &
