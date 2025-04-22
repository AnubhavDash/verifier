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
import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {DatasetConfiguration} from '../shared/types/dataset-configuration';
import {DatasetType} from '../shared/types/dataset-type';

@Injectable()
export class DatasetService {

  private readonly DATASET_API = `${environment.appUrl}/api/dataset`;
  private readonly http: HttpClient = inject(HttpClient);

  getDatasetConfiguration(): Observable<DatasetConfiguration> {
    return this.http.get<DatasetConfiguration>(this.DATASET_API);
  }

  shallowCleanDatasets(): Observable<Object> {
    return this.http.post(`${this.DATASET_API}/clean`, null);
  }

  uploadDataset(file: File, datasetType: DatasetType): Observable<Object> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.DATASET_API}/${datasetType}`, formData);
  }

}
