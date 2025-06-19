/*
 * (c) Copyright 2025 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
const {app, screen, BrowserWindow, Menu, dialog} = require('electron');
const {createLogger, format, transports} = require('winston');
const fs = require('fs');
const {dirname, join} = require('path');
const config = require('./config');
const {spawn} = require("child_process");
const requestPromise = require("minimal-request-promise");

// Logger
const logger = createFrontendLogger();
// Electron processes
let frontendWindow = null;
let serverProcess = null;

/**
 * Bootstraps the application.
 */
const gotTheLock = app.requestSingleInstanceLock();
if (!gotTheLock) {
  app.quit();
} else {
  app.on('second-instance', (event, commandLine, workingDirectory) => {
    // Someone tried to run a second instance, we should focus our window.
    if (frontendWindow) {
      if (frontendWindow.isMinimized()) {
        frontendWindow.restore();
      }
      frontendWindow.focus();
    }
  });
  app.on('ready', function () {
    configureFrontendWindow();
  });

  startUpServerProcess();
}

/**
 * Frontend window configuration.
 */
function configureFrontendWindow() {
  // Window size & configuration
  const workAreaSize = screen.getPrimaryDisplay().workAreaSize;
  const width = Math.min(1280, workAreaSize.width || 1280);
  const height = Math.min(900, workAreaSize.height || 900);
  frontendWindow = new BrowserWindow({
    show: false,
    width: width,
    height: height,
    webPreferences: {
      plugins: true
    }
  });
  frontendWindow.webContents.on('did-finish-load', () => frontendWindow.setTitle(`Swiss Post Verifier (${app.getVersion()})`));

  // Menu configuration
  const menu = Menu.buildFromTemplate([
    {
      label: 'File',
      submenu: [
        {
          label: 'Toggle developer tools', click() {
            frontendWindow.webContents.toggleDevTools();
          },
          accelerator: 'F12'
        },
        {
          label: 'Exit', click() {
            app.quit();
          }
        }
      ]
    }
  ]);
  frontendWindow.setMenu(menu);
  frontendWindow.setIcon(`${__dirname}/favicon.ico`);

  frontendWindow.on('close', function (e) {
    shutdownServerProcess(e);
  });

  // Event when the window is closed.
  frontendWindow.on('closed', function () {
    frontendWindow = null;
    app.quit();
  });
}

/**
 * Handles the server process startup.
 */
function startUpServerProcess() {
  serverProcess = spawn(
    'cmd.exe',
    ['/c', 'run-backend.bat startup'],
    {cwd: app.getAppPath() + '/../'}
  );

  if (!serverProcess) {
    console.error('Unable to start server from ' + app.getAppPath());
    logger.log('error', 'Unable to start server from ' + app.getAppPath());
    app.quit();
    return;
  }
  console.log('Server PID: ' + serverProcess.pid);
  logger.log('info', 'Server PID: ' + serverProcess.pid);

  checkBackendStartUp(1);
}

/**
 * Handles the server process readiness.
 */
function checkBackendStartUp(counter) {
  process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';

  requestPromise.get(config.serverConnectionCheckUrl()).then(
    function () {
      console.log('Server started!');
      logger.log('info', 'Server started!');
      frontendWindow.loadURL(`file://${__dirname}/index.html`);
      frontendWindow.show();
    }, function () {
      console.log(`Waiting for the server start... (${counter}/20)`);
      logger.log('info', 'Waiting for the server start...');
      if (counter < 20) {
        setTimeout(function () {
          checkBackendStartUp(counter + 1);
        }, 800);
      } else {
        dialog.showMessageBox(frontendWindow, {
          type: 'error',
          message: 'Unable to connect to server. Application will stop'
        }).then(() => {
          app.exit(0);
        }).catch(() => {
          app.exit(-1);
        });
      }
    });
}

/**
 * Handles the server process shutdown.
 */
function shutdownServerProcess(e) {
  if (serverProcess) {
    e.preventDefault();

    logger.log('info', 'Killing backend server PID: ' + serverProcess.pid);
    const shutdown = spawn(
      'cmd.exe',
      ['/c', 'run-backend.bat shutdown'],
      {cwd: app.getAppPath() + '/../'}
    );
    shutdown.on('exit', function () {
      logger.log('info', "Backend server process is killed.");
      serverProcess = null;
      frontendWindow.close(); // recall window close method.
    })
  }
}

/**
 * Creates a frontend logger.
 */
function createFrontendLogger() {
  const logDir = dirname(process.execPath) + '/logs';
  const suffix = Date.now();
  if (!fs.existsSync(logDir)) {
    fs.mkdirSync(logDir);
  }
  const filename = join(logDir, `verifier-frontend-app_${suffix}.log`);
  return createLogger({
    format: format.combine(
      format.timestamp({
        format: 'YYYY-MM-DD HH:mm:ss'
      }),
      format.json()
    ),
    transports: [new transports.File({filename})]
  });
}
