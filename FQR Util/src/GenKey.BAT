@echo off
echo This program generates a random key with length 16. Can be used to feed the AES.
REM SET FQR_UTIL_HOME=%CD%
SET FQR_UTIL_HOME=%~dp0
ECHO Running %~nx0 at %FQR_UTIL_HOME% with parameter %1
SET MY_CLASSPATH=%FQR_UTIL_HOME%;%FQR_UTIL_HOME%\libs\derby.jar;%FQR_UTIL_HOME%\FQR.Util.jar
echo %MY_CLASSPATH%
java -cp "%MY_CLASSPATH%" -Dfile.encoding=UTF-8 fqr.util.GenerateRandomKey
echo on
@PAUSE