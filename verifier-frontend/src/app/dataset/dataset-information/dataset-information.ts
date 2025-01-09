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
import {Component, Input} from '@angular/core';
import packageJson from '../../../../package.json';
import {DatasetConfiguration} from '../dataset-configuration/DatasetConfiguration.interface';
import {VerifierMode} from '../../verifier-mode/verifier-mode.enum';

@Component({
  templateUrl: 'dataset-information.html',
  selector: 'app-dataset-information',
  standalone: false
})
export class DatasetInformationComponent {

  @Input() configuration: DatasetConfiguration | undefined;
  @Input() displayDatasetInformation: boolean;
  @Input() endDate: string | undefined;
  @Input() startDate: string | undefined;
  @Input() statusCounterERROR: number | undefined;
  @Input() statusCounterNOK: number | undefined;
  @Input() statusCounterOK: number | undefined;
  @Input() totalNumberOfSetupVerifications: number | undefined;
  @Input() totalNumberOfTallyVerifications: number | undefined;
  @Input() verifierMode: VerifierMode | undefined;

  appVersion = '';
  fingerprintsNames = {
    canton: 'Canton',
    sdm_config: 'Setup Component',
    sdm_tally: 'Tally Control Component',
    control_component_1: 'Control Component 1',
    control_component_2: 'Control Component 2',
    control_component_3: 'Control Component 3',
    control_component_4: 'Control Component 4'
  };

  protected readonly VerifierMode = VerifierMode;

  constructor() {
    this.appVersion = packageJson.version;
  }

  isPrintMode(): boolean {
    return this.startDate !== undefined;
  }

  getEndDate(): string {
    return this.endDate ?? '-';
  }

  getTallyHash(): string {
    return this.configuration.tally?.hash ?? '-';
  }

  fingerPrintsNamesKeys(): {} {
    return Object.keys(this.fingerprintsNames);
  };

  getNumberOfConfirmedNonTestVotes(): string {
    return this.verifierMode === VerifierMode.TALLY && this.configuration.tally
      ? this.configuration.tally.numberOfConfirmedNonTestVotes.toString()
      : '-';
  }

  getNumberOfConfirmedTestVotes(): string {
    return this.verifierMode === VerifierMode.TALLY && this.configuration.tally
      ? this.configuration.tally.numberOfConfirmedTestVotes.toString()
      : '-';
  }

  getTotalNumberOfVerifications(): number {
    switch (this.verifierMode) {
      case VerifierMode.SETUP:
        return this.totalNumberOfSetupVerifications;
      case VerifierMode.TALLY:
        return this.totalNumberOfTallyVerifications;
      default:
        return 0;
    }
  }
}
