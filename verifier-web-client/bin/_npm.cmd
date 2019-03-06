@echo off
rem *****************************************************************************
rem  NPM command wrapper
rem *****************************************************************************

SETLOCAL

set NODE_HOME=C:\work\jrepo-local\tools\node\node-v10.15.3-x64
set PATH=%NODE_HOME%;%PATH%

%NODE_HOME%\npm --userconfig .npmrc %*
