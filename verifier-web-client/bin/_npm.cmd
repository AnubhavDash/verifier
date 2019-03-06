@echo off
rem *****************************************************************************
rem  2014 by Swiss Post, Information Technology Services
rem
rem  Version 4
rem
rem  NPM command wrapper
rem *****************************************************************************

SETLOCAL

set node_version=v10.15.3-x64
set JREPO_LOCAL=C:\work\jrepo-local
set JREPO_LOCAL_TOOLS=%JREPO_LOCAL%\tools

set NODE_HOME=%JREPO_LOCAL_TOOLS%\node\node-%node_version%
set PATH=%NODE_HOME%;%PATH%

%NODE_HOME%\npm --userconfig .npmrc %*
