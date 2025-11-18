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
import {Component, Input} from '@angular/core';
import packageJson from '../../../../../package.json';
import {DatasetConfiguration} from '../../types/dataset-configuration';
import {VerifierMode} from '../../types/verifier-mode.enum';
import {DatasetInformationItemComponent} from "../dataset-information-item/dataset-information-item.component";
import {TranslatePipe} from "@ngx-translate/core";
import {DatasetDisplayMode} from "../../types/dataset-display-mode.enum";

@Component({
  templateUrl: 'dataset-information.component.html',
  selector: 'ver-dataset-information',
  imports: [
    DatasetInformationItemComponent,
    TranslatePipe
  ],
  standalone: true
})
export class DatasetInformationComponent {
  @Input({required: true}) displayMode: DatasetDisplayMode;
  @Input({required: true}) configuration: DatasetConfiguration | undefined;

  @Input() startDate: string | undefined;
  @Input() endDate: string | undefined;
  @Input() statusCounterERROR: number | undefined;
  @Input() statusCounterNOK: number | undefined;
  @Input() statusCounterOK: number | undefined;
  @Input() totalNumberOfVerifications: number | undefined;
  @Input() verifierMode: VerifierMode | undefined;

  appVersion = packageJson.version;
  fingerprintsKeys: string[] = [
    'canton',
    'sdm_config',
    'sdm_tally',
    'control_component_1',
    'control_component_2',
    'control_component_3',
    'control_component_4'
  ];

  protected readonly DatasetDisplayMode = DatasetDisplayMode;

  isReportMode(): boolean {
    return this.displayMode === DatasetDisplayMode.REPORT;
  }

  isVerificationMode(): boolean {
    return this.displayMode === DatasetDisplayMode.VERIFICATION;
  }

  isTally(): boolean {
    return this.verifierMode === VerifierMode.TALLY;
  }

  getEndDate(): string {
    return this.endDate ?? '-';
  }

  getTallyHash(): string {
    return this.configuration.tally?.hash ?? '-';
  }

  getECH0222Hash(): string {
    return this.configuration.tally?.eCH0222Hash ?? '-';
  }

  getTotalNumberOfVerifications(): number {
    return this.totalNumberOfVerifications ? this.totalNumberOfVerifications : 0;
  }
}
