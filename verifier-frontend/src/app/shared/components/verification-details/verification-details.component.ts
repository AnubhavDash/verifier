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
import {KeyValuePipe, NgClass, SlicePipe} from "@angular/common";
import {LocalizePipe} from "../../pipes/localize.pipe";
import {VerificationDefinition} from "../../types/verification-definition";
import {VerificationStatus} from "../../types/verification-status";
import {TranslatePipe} from "@ngx-translate/core";
import {NgbCollapseModule} from "@ng-bootstrap/ng-bootstrap";

@Component({
  templateUrl: 'verification-details.component.html',
  selector: 'ver-verification-details',
  imports: [
    KeyValuePipe,
    LocalizePipe,
    SlicePipe,
    TranslatePipe,
    NgClass,
    NgbCollapseModule
  ],
  standalone: true
})
export class VerificationDetailsComponent {

  @Input() verifications: Record<string, VerificationDefinition>;
  @Input() isReportDisplay: boolean;

  collapsedStates: boolean[] = [];

  toggleCollapse(index: number) {
    this.collapsedStates[index] = !this.collapsedStates[index];
  }

  isDefault(key: unknown): boolean {
    return this.verifications[<string> key].status === VerificationStatus.IDLE;
  }

  isRunning(key: unknown): boolean {
    return this.verifications[<string> key].status === VerificationStatus.RUNNING;
  }

  isOK(key: unknown): boolean {
    return this.verifications[<string> key].status === VerificationStatus.OK;
  }

  isNotOK(key: unknown): boolean {
    return this.verifications[<string> key].status === VerificationStatus.NOK;
  }

  isError(key: unknown): boolean {
    return this.verifications[<string> key].status === VerificationStatus.ERROR;
  }

  showErrorStack(key: unknown): boolean {
    return this.verifications[<string> key].errorStack !== null && !this.isReportDisplay;
  }

  errorStack(key: unknown): string[] {
    return this.verifications[<string> key].errorStack || [];
  }

}
