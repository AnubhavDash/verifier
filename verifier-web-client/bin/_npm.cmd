@echo off
rem *****************************************************************************
rem  NPM command wrapper
rem *****************************************************************************

SETLOCAL

set NODE_HOME=C:\work\jrepo-local\tools\node\node-v14.15.1-win-x64
set PATH=%NODE_HOME%;%PATH%

%NODE_HOME%\npm --userconfig .npmrc %*
