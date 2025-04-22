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
import {Component, inject, OnInit} from '@angular/core';
import {VerifierMode} from '../shared/types/verifier-mode.enum';
import {DatasetConfiguration} from '../shared/types/dataset-configuration';
import {DatasetType} from '../shared/types/dataset-type';
import {Router} from "@angular/router";
import {DatasetService} from "./dataset.service";

@Component({
  templateUrl: 'dataset.component.html',
  providers: [],
  standalone: false
})
export class DatasetComponent implements OnInit {

  configuration: DatasetConfiguration;
  verifierMode: VerifierMode;
  uploadingDataset: boolean = false;

  protected readonly DatasetType = DatasetType;
  private readonly datasetService: DatasetService = inject(DatasetService);
  private readonly router: Router = inject(Router);

  ngOnInit(): void {
    this.verifierMode = JSON.parse(sessionStorage.getItem("verifierMode"));
    const configuration = JSON.parse(sessionStorage.getItem("configuration"))
    this.configuration = configuration ? configuration : new DatasetConfiguration();
  }

  // Change verifier mode.
  setVerifierMode(verifierMode: VerifierMode): void {
    if (this.verifierMode === verifierMode) {
      return;
    }

    sessionStorage.removeItem("verifierMode");
    sessionStorage.removeItem("configuration");

    this.datasetService.shallowCleanDatasets().subscribe((_value) => {
      this.verifierMode = verifierMode;
      this.configuration = new DatasetConfiguration();
    });
  }

  get isShownUploadContext(): boolean {
    return this.verifierMode !== null;
  }

  get isShownUploadTally(): boolean {
    return this.configuration.context && this.configuration.context.filename && this.verifierMode && this.verifierMode === VerifierMode.TALLY;
  }

  get isShownDatasetInformation(): boolean {
    return this.configuration.context && this.configuration.context.filename !== null;
  }

  get isEnabledNextButton(): boolean {
    if (this.verifierMode === VerifierMode.SETUP) {
      return this.configuration.context && this.configuration.context.filename !== null;
    }
    if (this.verifierMode === VerifierMode.TALLY) {
      return this.configuration.context && this.configuration.context.filename !== null
        && this.configuration.tally && this.configuration.tally.filename !== null;
    }
    return false;
  }

  next(){
    const sessionVerifierMode = JSON.parse(sessionStorage.getItem("verifierMode"));
    if (sessionVerifierMode !== this.verifierMode) {
      sessionStorage.removeItem('verifications');
      sessionStorage.removeItem('processTime');
    }
    sessionStorage.setItem("verifierMode", JSON.stringify(this.verifierMode));
    sessionStorage.setItem("configuration", JSON.stringify(this.configuration));
    this.router.navigate(['/verify']);
  }

  // General.
  actionsDisabled(): boolean {
    return this.uploadingDataset;
  }

  // Upload dataset.
  getDatasetTypeContext(): DatasetType {
    return (this.verifierMode) ? DatasetType.CONTEXT : undefined;
  }

  setDatasetConfigurationContext(configuration: DatasetConfiguration): void {
    this.configuration.context = configuration.context;
  }

  setDatasetConfigurationTally(configuration: DatasetConfiguration): void {
    this.configuration.tally = configuration.tally;
  }

  uploadingDatasetContextReset(uploadingState: boolean): void {
    this.uploadingDataset = uploadingState;

    if (uploadingState) {
      this.configuration = new DatasetConfiguration();
    }
  }

  uploadingDatasetTallyReset(uploadingState: boolean): void {
    this.uploadingDataset = uploadingState;
    if (uploadingState) {
      this.configuration.tally = null;
    }
  }
}

