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

@Component({
  templateUrl: 'dataset-information-item.component.html',
  selector: 'ver-dataset-information-item',
  standalone: true
})
export class DatasetInformationItemComponent {
  @Input() info: string;
  @Input() value: string | number;
}
