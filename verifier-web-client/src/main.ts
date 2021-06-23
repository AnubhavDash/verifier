/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
import {enableProdMode, LOCALE_ID} from '@angular/core';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';

import {AppModule} from './app/app.module';
import {environment} from './environments/environment';

if (environment.production) {
  enableProdMode();
}

const providers = [
  {provide: LOCALE_ID, useValue: getUsersLocale()}
];

platformBrowserDynamic([providers]).bootstrapModule(AppModule)
  .catch(err => console.log(err));


function getUsersLocale(): string {
  const supportedLangs = ['de', 'fr', 'it', 'en'];

  // Get lang from navigator
  if (typeof window === 'undefined' || typeof window.navigator === 'undefined') {
    return supportedLangs[0].toUpperCase();
  }
  const wn = window.navigator as any;
  let lang = wn.languages ? wn.languages[0] : supportedLangs[0];
  lang = lang || wn.language || wn.browserLanguage || wn.userLanguage;

  // Filter on supported langs
  lang = supportedLangs.filter(function (value) {
    return value === lang;
  })[0] || supportedLangs[0];

  return lang.toUpperCase();
}
