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
const {app, BrowserWindow, Menu, dialog} = require('electron');
const {createLogger, format, transports} = require('winston');
const fs = require('fs');
const path = require('path');
const config = require('./config');
const kill = require("tree-kill");

// Logger config
const logDir = 'logs';
const suffix = Date.now();
if (!fs.existsSync(logDir)) {
  fs.mkdirSync(logDir);
}
const filename = path.join(logDir, `verifier-frontend-app_${suffix}.log`);
const logger = createLogger({
  format: format.combine(
    format.timestamp({
      format: 'YYYY-MM-DD HH:mm:ss'
    }),
    format.json()
  ),
  transports: [new transports.File({filename})]
});


// Backend server process
let serverProcess;
const platform = process.platform;

if (platform === 'win32') {
  console.log(app.getAppPath());
  logger.log('info', app.getAppPath());

  serverProcess = require('child_process')
    .spawn('cmd.exe', ['/c', 'run-backend.bat startup'],
      {
        cwd: app.getAppPath() + '/../'
      });
} else {
  console.error('Non windows OS is currently not implemented');
  logger.log('error', 'Non windows OS is currently not implemented');
}

if (!serverProcess) {
  console.error('Unable to start server from ' + app.getAppPath());
  logger.log('error', 'Unable to start server from ' + app.getAppPath());
  app.quit();
  return;
}

console.log('Server PID: ' + serverProcess.pid);
logger.log('info', 'Server PID: ' + serverProcess.pid);


let win;
const prepareWindow = function () {

  // Create the browser window.
  win = new BrowserWindow({
    show: false,
    width: 1200,
    height: 800,
    webPreferences: {
      plugins: true
    }
  });
  win.webContents.on('did-finish-load', () => win.setTitle(`Swiss Post Verifier (${app.getVersion()})`));
  const menu = Menu.buildFromTemplate([
    {
      label: 'File',
      submenu: [
        {
          label: 'Toggle developer tools', click() {
            win.webContents.toggleDevTools();
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
  Menu.setApplicationMenu(menu);

  win.on('close', function (e) {
    if (serverProcess) {
      e.preventDefault();

      logger.log('info', 'Killing backend server PID: ' + serverProcess.pid);
      const shutdown = require('child_process').spawn('cmd.exe', ['/c', 'run-backend.bat shutdown'], {cwd: app.getAppPath() + '/../'});
      shutdown.on('exit', function () {
        logger.log('info', "Backend server process is killed.");
        serverProcess = null;
        win.close(); // recall window close method.
      })
    }
  });

  // Event when the window is closed.
  win.on('closed', function () {
    win = null;
    app.quit();
  });
};

const startUp = function (counter) {
  const requestPromise = require('minimal-request-promise');

  process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';
  app.on('ready', function () {
    prepareWindow();
  });

  requestPromise.get(config.serverConnectionCheckUrl()).then(
    function () {
      console.log('Server started!');
      logger.log('info', 'Server started!');
      win.loadURL(`file://${__dirname}/index.html`);
      win.maximize();
      win.show();
    }, function () {
      console.log(`Waiting for the server start... (${counter}/20)`);
      logger.log('info', 'Waiting for the server start...');
      if (counter < 20) {
        setTimeout(function () {
          startUp(counter + 1);
        }, 800);
      } else {
        dialog.showMessageBox(win, {
          type: 'error',
          message: 'Unable to connect to server. Application will stop'
        }).then(() => {
          app.exit(0);
        }).catch(() => {
          app.exit(-1);
        });
      }
    });
};

startUp(1);
