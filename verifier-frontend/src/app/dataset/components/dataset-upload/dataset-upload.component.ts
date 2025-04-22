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
  templateUrl: 'dataset-upload.component.html',
  styleUrls: ['dataset-upload.component.css'],
  selector: 'ver-dataset-upload',
  standalone: false
})
export class DatasetUploadComponent implements OnChanges, OnInit {

  @Input() datasetType: DatasetType | undefined;
  @Input() uploadDisabled: boolean;
  @Input() verifierMode: VerifierMode;
  @Input() configuration: DatasetConfiguration;
  @Output() datasetConfiguration: EventEmitter<DatasetConfiguration> = new EventEmitter<DatasetConfiguration>();
  @Output() uploadingDataset: EventEmitter<boolean> = new EventEmitter<boolean>();

  uploading = false;
  uploadError = false;
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

  upload(event) {
    const file = event.target.files[0];
    if (file) {
      this.uploading = true;
      this.filename = undefined;

      this.uploadingDataset.emit(this.uploading);

      this.datasetService.uploadDataset(file, this.datasetType).subscribe({
        next: () => {
          this.datasetService.getDatasetConfiguration().subscribe(configuration => {
            this.uploading = false;
            this.uploadError = false;
            this.filename = this.getDatasetFilename(configuration);

            this.datasetConfiguration.emit(configuration);
            this.uploadingDataset.emit(this.uploading);
          });
        },
        error: () => {
          this.uploading = false;
          this.uploadError = true;

          this.uploadingDataset.emit(this.uploading);
        }
      });
    }
  }

  isUploadDisabled(): boolean {
    return this.uploading || this.uploadDisabled;
  }

  isNotUploaded(): boolean {
    return (!this.datasetType || !this.filename) && !this.uploading && !this.uploadError;
  }

  isUploading(): boolean {
    return this.uploading;
  }

  errorOccurred(): boolean {
    return !this.uploading && this.uploadError;
  }

  isUploaded(): boolean {
    return this.filename && !this.uploading && !this.uploadError;
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
