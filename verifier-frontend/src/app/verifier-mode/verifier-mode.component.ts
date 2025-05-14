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
import {Component, EventEmitter, Input, Output} from '@angular/core';
import {VerifierMode} from './verifier-mode.enum';

@Component({
  templateUrl: 'verifier-mode.component.html',
  selector: 'app-verifier-mode'
})
export class VerifierModeComponent {

  @Input() verifierMode: VerifierMode | undefined;
  @Input() changeModeDisabled: boolean;
  @Input() triggerModal: boolean;
  @Output() newVerifierMode: EventEmitter<VerifierMode> = new EventEmitter<VerifierMode>();

  changeMode() {
    this.newVerifierMode.emit(this.isTallyMode() ? VerifierMode.SETUP : VerifierMode.TALLY);
  }

  changeModeToTally(): void {
    this.changeModeTo(VerifierMode.TALLY);
  }

  changeModeToSetup(): void {
    this.changeModeTo(VerifierMode.SETUP);
  }

  changeModeTo(newVerifierMode: VerifierMode): void {
    if (newVerifierMode === this.verifierMode || this.triggerModal) {
      return;
    }

    this.newVerifierMode.emit(newVerifierMode);
  }

  isSetupMode(): boolean {
    return this.verifierMode === VerifierMode.SETUP;
  }

  isTallyMode(): boolean {
    return this.verifierMode === VerifierMode.TALLY;
  }
}
