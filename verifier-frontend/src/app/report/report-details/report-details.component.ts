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

@Component({
  templateUrl: 'report-details.component.html',
  styleUrls: ['report-details.component.css'],
  selector: 'app-report-details'
})
export class ReportDetailsComponent {

  @Input() verifications: {};
  @Input() verificationStatusFilter: string;
  @Input() toggleMessage: boolean;

  isRunning(status): boolean {
    return status === 'RUNNING';
  }

  isOK(status): boolean {
    return status === 'OK';
  }

  isNotOK(status): boolean {
    return status === 'NOK';
  }

  isNA(status): boolean {
    return status === 'NA';
  }

  isError(status): boolean {
    return status === 'UNEXPECTED_ERROR';
  }

}
