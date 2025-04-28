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
import {SessionStorageService} from "../shared/services/session-storage.service";

@Component({
  templateUrl: 'dataset.component.html',
  providers: [],
  standalone: false
})
export class DatasetComponent implements OnInit {

  configuration: DatasetConfiguration;
  verifierMode: VerifierMode;
  loadingDataset: boolean = false;

  protected readonly DatasetType = DatasetType;
  private readonly datasetService: DatasetService = inject(DatasetService);
  private readonly router: Router = inject(Router);
  private readonly sessionStorageService = inject(SessionStorageService);

  ngOnInit(): void {
    this.verifierMode = this.sessionStorageService.getVerifierMode();
    this.configuration = this.sessionStorageService.getConfiguration() || new DatasetConfiguration();
  }

  // Change verifier mode.
  setVerifierMode(verifierMode: VerifierMode): void {
    if (this.verifierMode === verifierMode) {
      return;
    }

    this.sessionStorageService.setVerifierMode(verifierMode);
    this.sessionStorageService.clearConfiguration();
    this.sessionStorageService.clearVerifications();
    this.sessionStorageService.clearProcessTime();
    this.sessionStorageService.clearVerificationReportGenerated();
    this.sessionStorageService.clearElectionResultReportGenerated();

    this.datasetService.cleanDatasets().subscribe((_value) => {
      this.verifierMode = verifierMode;
      this.configuration = new DatasetConfiguration();
    });
  }

  get isShownLoadContext(): boolean {
    return this.verifierMode !== null;
  }

  get isShownLoadTally(): boolean {
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
    this.router.navigate(['/verify']);
  }

  actionsDisabled(): boolean {
    return this.loadingDataset;
  }

  getDatasetTypeContext(): DatasetType {
    return (this.verifierMode) ? DatasetType.CONTEXT : undefined;
  }

  setDatasetConfigurationContext(configuration: DatasetConfiguration): void {
    this.configuration.context = configuration.context;
    if (this.verifierMode === VerifierMode.SETUP) {
      this.sessionStorageService.setConfiguration(this.configuration);
    }
  }

  setDatasetConfigurationTally(configuration: DatasetConfiguration): void {
    this.configuration.tally = configuration.tally;
    this.sessionStorageService.setConfiguration(this.configuration);
  }

  datasetContextReset(loadingState: boolean): void {
    this.loadingDataset = loadingState;

    if (loadingState) {
      this.configuration = new DatasetConfiguration();
    }
  }

  datasetTallyReset(loadingState: boolean): void {
    this.loadingDataset = loadingState;
    if (loadingState) {
      this.configuration.tally = null;
    }
  }
}

