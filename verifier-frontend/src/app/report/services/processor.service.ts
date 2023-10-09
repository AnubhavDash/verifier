/*
 * Copyright 2022 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {DatasetConfiguration} from '../models/DatasetConfiguration.interface';
import {DatasetType} from '../models/dataset-type.enum';

@Injectable()
export class ProcessorService {

  httpOptions = {
    headers: new HttpHeaders({
      'Authorization': environment.authorizationHeaderValue
    })
  };

  constructor(private http: HttpClient) {

  }

  getVerifications(): Observable<any> {
    return this.http.get<any>(environment.appUrl + '/api/verifications', this.httpOptions);
  }

  processVerifications(runOptions?: string): Observable<Object> {
    const runParams = `?runOptions=${runOptions}`;
    return this.http.post(environment.appUrl + '/api/verifications' + runParams, null, this.httpOptions);
  }

  resetVerifications(): Observable<Object> {
    return this.http.post(environment.appUrl + '/api/reset', null, this.httpOptions);
  }

  changeMode(): Observable<Object> {
    return this.http.post(environment.appUrl + '/api/changeMode', null, this.httpOptions);
  }

  getDatasetConfiguration(): Observable<DatasetConfiguration> {
    return this.http.get<DatasetConfiguration>(environment.appUrl + '/api/datasetConfiguration', this.httpOptions);
  }

  uploadDataset(file: File, dataset: DatasetType): Observable<Object> {
    const options = Object.create(this.httpOptions);
    options.headers['Content-Type'] = undefined;
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(environment.appUrl + '/api/dataset/' + dataset, formData, options);
  }

}
