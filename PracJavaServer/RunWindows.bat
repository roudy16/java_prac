@echo off
set startDir=%~dp0
set binDir=%startDir%bin\

pushd %binDir%

:: run server and client orchestrator
start cmd /C java jpserver.Server
start cmd /C java jpclient.ClientOrchestrator

popd
