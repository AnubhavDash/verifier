@echo off

rem *****************************************************************************
rem  2014 by Swiss Post, Information Technology Services
rem
rem  Version 5.0.0
rem
rem  CAUTION! Still just maven and ant are executable
rem *****************************************************************************

rem *****************************************************************************
rem   Environment Variables
rem
rem   Set desired set and versions of tools in this section
rem
rem   Examples:
rem
rem   Skip installation for unnecessary tools:
rem   set <tool>.optional=true
rem
rem   Set tool version:
rem   set <tool>.version=<tool-version>
rem *****************************************************************************

SETLOCAL

set project.shortname=mytime
set maven.version=3.3.1
set ant.version=1.7.0
set java.version=jdk1.8.0_92
set eclipse.version=4.2.2-2.0.0
set eclipse-workspace.version=4.2.2-2.0.0
set tomcat.version=8.0.18-x64
set subversionConfig.version=1.0
set subversion.version=1.7.16
set node.version=v8.3.0-x64

set maven.optional=false
set eclipse.optional=true
set eclipse-workspace.optional=true
set java.optional=false
set tomcat.optional=true
set subversionConfig.optional=true
set subversion.optional=true
set node.optional=false

rem *****************************************************************************
rem   Do not alter script beyond this point for project usage
rem *****************************************************************************

rem call help
:CHECK_PARAMETERS
if "%1" == "-h"  goto USAGE
if "%1" == "help"  goto USAGE
if "%1" == "?"  goto USAGE

rem *****************************************************************************
rem   Tool installation (via apache ant)
rem *****************************************************************************

:CHECK_JREPO_HOME
if not '%JREPO_HOME%'=='' goto CHECK_JREPO_LOCAL
set JREPO_HOME=https://repository.pnet.ch/svn/reposdev-tools/04_Implementation/toolrepository
set JREPO_RUN_HOME=https://repository.pnet.ch/svn/reposdev-tools/04_Implementation/runTool/trunk

:CHECK_JREPO_LOCAL
if not '%JREPO_LOCAL%'=='' goto CHECK_ANT_HOME
set JREPO_LOCAL=C:\work\jrepo-local
set JREPO_LOCAL_TOOLS=%JREPO_LOCAL%\tools

rem determine Java
set JAVA_HOME=%JREPO_LOCAL_TOOLS%\java\java-%java.version%

if not '%1'=='install' if not '%1'=='' goto RUN_TOOL

rem downloads of programs end in a temp folder for installation, this folder will be deleted at the end
FOR /F "usebackq delims=" %%v IN (`powershell -noprofile "& {$user = Read-Host 'Username' ; $user}"`) DO set "user=%%v";
FOR /F "usebackq delims=" %%v IN (`powershell -noprofile "& {$pass = Read-Host 'Password' -AsSecureString; [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($pass))}"`) DO set "pwd=%%v";
mkdir %JREPO_LOCAL%\temp
set JREPO_TEMP=%JREPO_LOCAL%\temp
cd %JREPO_TEMP%

:CHECK_ANT_HOME
if not '%ANT_HOME%'=='' goto CHECK_ANT
set ANT_HOME=%JREPO_LOCAL%\tools\ant\ant-%ant.version%

:CHECK_ANT
if exist %ANT_HOME%\lib\ant.jar goto RUN_ANT
goto FETCH_ANT

:FETCH_ANT
if not exist %ANT_HOME% mkdir %ANT_HOME%
mkdir %JREPO_TEMP%\sources\ant
powershell -Command "& {$webClient=New-Object System.Net.WebClient; $webClient.Credentials=new-object System.Net.NetworkCredential('%user%','%pwd%'); $webclient.DownloadFile('%JREPO_HOME%/ant/ant-%ant.version%.zip','%JREPO_TEMP%\sources\ant\ant-%ant.version%.zip')}"
unzip %JREPO_TEMP%\sources\ant\ant-%ant.version%.zip -d %ANT_HOME%
goto RUN_ANT


rem ****************************************************************************
rem install and or run ant
rem ****************************************************************************

:RUN_ANT
powershell -Command "& {$webClient=New-Object System.Net.WebClient; $webClient.Credentials=new-object System.Net.NetworkCredential('%user%','%pwd%'); $webclient.DownloadFile('%JREPO_RUN_HOME%/build.xml','%JREPO_TEMP%\build.xml')}"
if '%1'=='' goto ANT_INSTALL_ALL
if '%1'=='install' (if '%2' == '' (goto ANT_INSTALL_ALL) else (goto ANT_INSTALL))
goto usage

:ANT_INSTALL
if '%2'=='' (
  goto ANT_INSTALL_ALL
) else (
    goto LOOP
)

:LOOP
shift
if '%1'=='' goto CONTINUE
set params=%params%,%1
goto LOOP

:CONTINUE
call %ANT_HOME%\bin\ant.bat -quiet -f %JREPO_TEMP%\build.xml install -Dtools="%params%" -DdirectInstall=true
goto deltemp

:ANT_INSTALL_ALL
if '%1' == '' set params=-DnoArgs=true
call %ANT_HOME%\bin\ant.bat -quiet -f %JREPO_TEMP%\build.xml install %params%
goto deltemp


rem *****************************************************************************
rem   Calls to external tool (e.g. mvn jetty:run)
rem *****************************************************************************

:RUN_TOOL
IF /i "%2"=="jetty:run" set MAVEN_OPTS=%MAVEN_OPTS% -Xdebug -Xrunjdwp:transport=dt_socket,address=4000,server=y,suspend=n
if '%MVN_HOME%'=='' set MVN_HOME=%JREPO_LOCAL_TOOLS%\maven\maven-%maven.version%
if '%ANT_HOME%'=='' set ANT_HOME=%JREPO_LOCAL_TOOLS%\ant\ant-%ant.version%
if '%SVN_HOME%'=='' set SVN_HOME=%JREPO_LOCAL_TOOLS%\subversion\subversion-%subversion.version%
if '%NODE_HOME%'=='' set NODE_HOME=%JREPO_LOCAL_TOOLS%\node\node-%node.version%
if '%node.optional%'=='false' set PATH=%NODE_HOME%;%PATH%

:CALL_TOOL
if /i "%1" == "mvn" goto RUN_MVN
if /i "%1" == "ant" goto RUN_ANT_EXT
if /i "%1" == "java" goto RUN_JAVA
if /i "%1" == "svn" goto RUN_SVN
if /i "%1" == "npm" goto RUN_NPM
if /i "%1" == "node" goto RUN_NODE
if /i "%1" == "grunt" goto RUN_GRUNT
if /i "%1" == "yo" goto RUN_YO
if /i "%1" == "ng" goto RUN_NG
goto TOOL_ERROR

:RUN_MVN
if not exist %MVN_HOME%\bin\mvn goto TOOL_NOT_INSTALLED
if not exist %JAVA_HOME%\lib\tools.jar goto JDK_NOT_INSTALLED
rem because of site:generate (findbugs memory consumption)
rem see http://mojo.codehaus.org/findbugs-maven-plugin/usage.html
set MAVEN_OPTS=-Xmx512M %MAVEN_OPTS%
%MVN_HOME%\bin\%*
goto end

:RUN_ANT_EXT
if not exist %ANT_HOME%\bin\ant goto TOOL_NOT_INSTALLED
set ANT_OPTS=-Xmx512M %ANT_OPTS%
%ANT_HOME%\bin\%*
goto end

:RUN_JAVA
if not exist %JAVA_HOME%\bin\java.exe goto TOOL_NOT_INSTALLED
%JAVA_HOME%\bin\%*
goto end

:RUN_SVN
if not exist %SVN_HOME%\bin\svn.exe goto TOOL_NOT_INSTALLED
%SVN_HOME%\bin\%*
goto end

:RUN_NPM
if not exist %NODE_HOME%\node.exe goto TOOL_NOT_INSTALLED
%NODE_HOME%\%*
goto end

:RUN_NODE
if not exist %NODE_HOME%\node.exe goto TOOL_NOT_INSTALLED
%NODE_HOME%\%*
goto end

:RUN_GRUNT
if not exist %NODE_HOME%\grunt.cmd goto JSTOOLS_NOT_INSTALLED
call %NODE_HOME%\%*
goto end

:RUN_YO
if not exist %NODE_HOME%\yo.cmd goto JSTOOLS_NOT_INSTALLED
%NODE_HOME%\%*
goto end

:RUN_NG
if not exist %NODE_HOME%\node.exe goto JSTOOLS_NOT_INSTALLED
%NODE_HOME%\npm run-script %*
goto end

rem *****************************************************************************
rem   Errors
rem *****************************************************************************

:TOOL_ERROR
echo *** ERROR: Unsupported tool:  %1
goto end

:TOOL_NOT_INSTALLED
echo *** ERROR: Tool not installed, install by running runTool.cmd install or runTool.cmd install %1
goto end

:JDK_NOT_INSTALLED
echo *** ERROR: JDK not installed, install by running runTool.cmd install java
goto end

:JSTOOLS_NOT_INSTALLED
echo *** ERROR: jstools not installed, install by running runTool.cmd install node
goto end

rem *****************************************************************************
rem   Help
rem *****************************************************************************


:USAGE
echo.
echo usage: runTool install [tool1 [tool2] ...]
echo or runTool tool [arguments]
echo.
echo for example: runTool mvn clean install
echo.
echo without any arguments, you will be prompted if you want to install all tools (same as 'install')
echo.
echo 'install' will install all tools which are not marked as 'optional' (see top of this script)
echo 'install ^<^<tool^>^>' will install the tool specified without considering the 'optional' setting
echo available tools:
echo        - ant
echo        - eclipse
echo        - eclipse-workspace
echo        - java
echo        - jrebel
echo        - maven
echo        - subversion
echo        - tomcat
echo        - node
echo.
echo.
echo The following environment variables are supported
echo.
echo     - JREPO_LOCAL ((default: C:\work\userapp\jrepo-local)
echo.
echo     - JREPO_HOME (default: https://repository.pnet.ch/svn/reposdev-tools/04_Implementation/)
echo.
echo
goto end

:deltemp
cd ..
rmdir temp /s /q
goto end


:end
endlocal
