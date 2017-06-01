@echo off
set startDir=%~dp0
set scriptsDir=%startDir%scripts\
set binDir=%startDir%bin\
set srcDir=%startDir%src\

pushd %binDir%
:: clean
del /S *.class

:: build
javac -d %binDir% -cp %binDir% %srcDir%\jputils\*.java
javac -d %binDir% -cp %binDir% %srcDir%\jpclient\*.java
javac -d %binDir% -cp %binDir% %srcDir%\jpserver\*.java

popd
