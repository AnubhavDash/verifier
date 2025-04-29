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
import {Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {DatasetType} from '../../../shared/types/dataset-type';
import {DatasetConfiguration} from '../../../shared/types/dataset-configuration';
import {VerifierMode} from "../../../shared/types/verifier-mode.enum";
import {DatasetService} from "../../dataset.service";

@Component({
  templateUrl: 'dataset-load.component.html',
  selector: 'ver-dataset-load',
  standalone: false
})
export class DatasetLoadComponent implements OnChanges, OnInit {

  @Input() datasetType: DatasetType | undefined;
  @Input() loadDisabled: boolean;
  @Input() verifierMode: VerifierMode;
  @Input() configuration: DatasetConfiguration;
  @Output() datasetConfiguration: EventEmitter<DatasetConfiguration> = new EventEmitter<DatasetConfiguration>();
  @Output() loadingDataset: EventEmitter<boolean> = new EventEmitter<boolean>();

  loading = false;
  loadError = false;
  filename = undefined;
  private readonly datasetService: DatasetService = inject(DatasetService);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.verifierMode && changes.verifierMode.previousValue !== changes.verifierMode.currentValue) {
      this.filename = undefined;
    }
  }

  ngOnInit(): void {
    if (this.configuration) {
      this.filename = this.getDatasetFilename(this.configuration);
    }
  }

  load(event) {
    const file = event.target.files[0];
    if (file) {
      this.loading = true;
      this.filename = undefined;

      this.loadingDataset.emit(this.loading);

      this.datasetService.loadDataset(file, this.datasetType).subscribe({
        next: () => {
          this.datasetService.getDatasetConfiguration().subscribe(configuration => {
            this.loading = false;
            this.loadError = false;
            this.filename = this.getDatasetFilename(configuration);

            this.datasetConfiguration.emit(configuration);
            this.loadingDataset.emit(this.loading);
          });
        },
        error: () => {
          this.loading = false;
          this.loadError = true;

          this.loadingDataset.emit(this.loading);
        }
      });
    }
  }

  isLoadDisabled(): boolean {
    return this.loading || this.loadDisabled;
  }

  isNotLoaded(): boolean {
    return (!this.datasetType || !this.filename) && !this.loading && !this.loadError;
  }

  isLoading(): boolean {
    return this.loading;
  }

  errorOccurred(): boolean {
    return !this.loading && this.loadError;
  }

  isLoaded(): boolean {
    return this.filename && !this.loading && !this.loadError;
  }

  private getDatasetFilename(datasetConfiguration: DatasetConfiguration): string {
    switch (this.datasetType) {
      case DatasetType.CONTEXT:
        return datasetConfiguration.context?.filename;
      case DatasetType.TALLY:
        return datasetConfiguration.tally?.filename;
      default:
        return '';
    }
  }

}
