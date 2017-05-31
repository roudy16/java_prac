#!/bin/bash

PROJDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd)"
BINDIR="${PROJDIR}/bin/"
cd $BINDIR
java jpclient.ClientOrchestrator < "${PROJDIR}/demo_input.txt"
