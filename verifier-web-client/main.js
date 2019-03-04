/*
 * This file is part of Verifier Swiss Post.
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
 */
const {app, BrowserWindow, session, Menu, dialog} = require('electron')
const { createLogger, format, transports } = require('winston');
const fs = require('fs');
const path = require('path');
const dateFormat = require('dateformat');

const logDir = 'logs';
let now = new Date();
let suffix = dateFormat(now, "yyyy-mm-dd-HHMMss");
if (!fs.existsSync(logDir)) {
  fs.mkdirSync(logDir);
}
const filename = path.join(logDir, 'verifier_'+ suffix + '.log');
const logger = createLogger({
  format: format.combine(
    format.timestamp({
      format: 'YYYY-MM-DD HH:mm:ss'
    }),
    format.json()
  ),
  transports: [ new transports.File({ filename }) ]
});


let win;
let serverProcess;
let platform = process.platform;

let appUrl = 'https://127.0.0.1:8443';

if (platform === 'win32') {
  console.log(app.getAppPath());
  logger.log('info', app.getAppPath());

  serverProcess = require('child_process')
    .spawn('cmd.exe', ['/c', 'run-backend.bat'],
      {
        cwd: app.getAppPath() + '/../'
      });
} else {
  // serverProcess = require('child_process').spawn(app.getAppPath() + '/run-backend');
}

if (!serverProcess) {
  console.error('Unable to start server from ' + app.getAppPath());
  logger.log('error', 'Unable to start server from ' + app.getAppPath());
  app.quit();
  return;
}

app.on('certificate-error', (event, webContents, url, error, certificate, callback) => {
  // console.log("certificate :"+certificate.fingerprint );
  if (certificate.fingerprint === 'sha256/ERUEk1Mx1zexjJ7FROwfICOsTc6ueShhtmCxi9o3p8I=') {
    // Logique de vérification.
    event.preventDefault();
    callback(true);
  } else {
    callback(false);
  }
})

serverProcess.stdout.on('data', function (data) {
  console.log('Server: ' + data);
  logger.log('info', 'Server: ' + data);
});

console.log('Server PID: ' + serverProcess.pid);
logger.log('info', 'Server PID: ' + serverProcess.pid);


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
  const menu = Menu.buildFromTemplate([
    {
      label: 'File',
      submenu: [
        {
          label: 'Toggle developer tools', click() {
            win.webContents.toggleDevTools();
          }
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
  // win.setMenu(null);


  //// uncomment below to open the DevTools.
  // win.webContents.openDevTools()

  // Event when the window is closed.
  win.on('closed', function () {
    win = null
    app.quit();
  });

  win.on('close', function (e) {
    if (serverProcess) {
      e.preventDefault();

      const kill = require('tree-kill');
      kill(serverProcess.pid, 'SIGTERM', function () {
        console.log('Server process killed');
        logger.log('info', 'Server process killed');

        serverProcess = null;

        win.close();
      });
    }
  });
};

const startUp = function (counter) {
  const requestPromise = require('minimal-request-promise');

  process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
  app.on('ready', function() {
    prepareWindow();
  });

  requestPromise.get(appUrl + "/api/ping").then(function (response) {
    console.log('Server started!');
    logger.log('info', 'Server started!');
    win.loadURL(`file://${__dirname}/dist/index.html`);
    win.maximize();
    win.show();
  }, function (response) {
    console.log('Waiting for the server start... ('+counter+'/20)');
    logger.log('info', 'Waiting for the server start...');
    if (counter < 20) {
      setTimeout(function () {
        startUp(counter+1);
      }, 200);
    } else {
      dialog.showMessageBox(win, {type: "error", message: "Unable to connect to server. Application will stop"}, function (response) {app.quit()})
    }
  });
};

startUp(1);
