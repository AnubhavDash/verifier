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
/**
 * This file includes polyfills needed by Angular and is loaded before the app.
 * You can add your own extra polyfills to this file.
 *
 * This file is divided into 2 sections:
 *   1. Browser polyfills. These are applied before loading ZoneJS and are sorted by browsers.
 *   2. Application imports. Files imported after ZoneJS that should be loaded before your main
 *      file.
 *
 * The current setup is for so-called "evergreen" browsers; the last versions of browsers that
 * automatically update themselves. This includes Safari >= 10, Chrome >= 55 (including Opera),
 * Edge >= 13 on the desktop, and iOS 10 and Chrome on mobile.
 *
 * Learn more in https://angular.io/docs/ts/latest/guide/browser-support.html
 */

/**
 * BROWSER POLYFILLS
 */

/** IE9, IE10 and IE11 requires all of the following polyfills. **/
import 'core-js/features/symbol';
import 'core-js/features/object';
import 'core-js/features/function';
import 'core-js/features/parse-int';
import 'core-js/features/parse-float';
import 'core-js/features/number';
import 'core-js/features/math';
import 'core-js/features/string';
import 'core-js/features/date';
import 'core-js/features/array';
import 'core-js/features/regexp';
import 'core-js/features/map';
import 'core-js/features/weak-map';
import 'core-js/features/set';

/**
 * Zone JS is required by default for Angular itself.
 */
import 'zone.js'; // Included with Angular CLI.


/**
 * APPLICATION IMPORTS
 */

// Tip to make StompJS work
(window as any).global = window;
