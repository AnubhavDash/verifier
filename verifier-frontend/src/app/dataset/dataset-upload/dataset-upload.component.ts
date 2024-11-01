/*
 * (c) Copyright 2024 Swiss Post Ltd.
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
import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {DatasetType} from './dataset-type.enum';
import {DatasetConfiguration} from '../dataset-configuration/DatasetConfiguration.interface';
import {ProcessorService} from '../../processor.service';
import {VerifierMode} from '../../verifier-mode/verifier-mode.enum';

@Component({
  templateUrl: 'dataset-upload.component.html',
  styleUrls: ['dataset-upload.component.css'],
  selector: 'app-upload'
})
export class DatasetUploadComponent implements OnChanges {

  @Input() datasetType: DatasetType | undefined;
  @Input() uploadDisabled: boolean;
  @Input() verifierMode: VerifierMode;
  @Output() datasetConfiguration: EventEmitter<DatasetConfiguration> = new EventEmitter<DatasetConfiguration>();
  @Output() uploadingDataset: EventEmitter<boolean> = new EventEmitter<boolean>();

  uploading = false;
  uploadError = false;
  filename = undefined;

  constructor(private processorService: ProcessorService) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.verifierMode && changes.verifierMode.previousValue !== changes.verifierMode.currentValue) {
      this.filename = undefined;
    }
  }

  upload(event) {
    const file = event.target.files[0];
    if (file) {
      this.uploading = true;
      this.filename = undefined;

      this.uploadingDataset.emit(this.uploading);

      this.processorService.uploadDataset(file, this.datasetType).subscribe({
        next: () => {
          this.processorService.getDatasetConfiguration().subscribe(configuration => {
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
        return datasetConfiguration.context.filename;
      case DatasetType.TALLY:
        return datasetConfiguration.tally.filename;
      default:
        return '';
    }
  }

}
