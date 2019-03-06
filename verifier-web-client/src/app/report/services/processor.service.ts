///
/// This file is part of Verifier Swiss Post.
/// Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
/// Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
/// You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
///

import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {environment} from '../../../environments/environment';
import {Configuration} from '../models/Configuration.interface';

@Injectable()
export class ProcessorService {

  httpOptions = {
    headers: new HttpHeaders({
      'Authorization': environment.authorizationHeaderValue
    })
  };

  constructor(private http: HttpClient) {

  }

  getTestStatus(): Observable<any> {
    return this.http.get<any>(environment.appUrl + '/api/tests', this.httpOptions);
  }

  processTests(runOptions?: string): Observable<Object> {
    let runParams = '';
    if (runOptions !== undefined) {
      runParams = `?runOptions=${runOptions}`;
    }
    return this.http.post(environment.appUrl + '/api/tests' + runParams, null, this.httpOptions);
  }

  resetTests(): Observable<Object> {
    return this.http.post(environment.appUrl + '/api/reset', null, this.httpOptions);
  }

  getConfigurationInputDirectory(): Observable<Configuration> {
    return this.http.get<Configuration>(environment.appUrl + '/api/configurationInputDirectory', this.httpOptions);
  }

  setConfigurationInputDirectory(value: Configuration): Observable<Object> {
    return this.http.post(environment.appUrl + '/api/configurationInputDirectory', value, this.httpOptions);
  }
}
