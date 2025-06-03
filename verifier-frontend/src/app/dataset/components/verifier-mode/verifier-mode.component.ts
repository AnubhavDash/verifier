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
import {Component, EventEmitter, inject, Input, Output, TemplateRef} from '@angular/core';
import {VerifierMode} from '../../../shared/types/verifier-mode.enum';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  templateUrl: 'verifier-mode.component.html',
  selector: 'ver-verifier-mode',
  standalone: false
})
export class VerifierModeComponent {

  @Input() verifierMode: VerifierMode | undefined;
  @Input() changeModeDisabled: boolean;
  @Output() newVerifierMode: EventEmitter<VerifierMode> = new EventEmitter<VerifierMode>();

  private readonly modalService = inject(NgbModal);

  changeModeToTally(content: TemplateRef<any>): void {
    this.changeModeTo(content, VerifierMode.TALLY);
  }

  changeModeToSetup(content: TemplateRef<any>): void {
    this.changeModeTo(content, VerifierMode.SETUP);
  }

  changeModeTo(content: TemplateRef<any>, newVerifierMode: VerifierMode): void {
    if (newVerifierMode === this.verifierMode) {
      return;
    }

    if (this.verifierMode != null){
      this.modalService.open(content, { ariaLabelledBy: 'modal-basic-title' }).result.then(
        (_) => {
          this.emitChangeMode(newVerifierMode);
        },
        (_) => {},
      );
    } else {
      this.emitChangeMode(newVerifierMode);
    }
  }

  emitChangeMode(newVerifierMode: VerifierMode) {
    this.newVerifierMode.emit(newVerifierMode);
  }

  isSetupMode(): boolean {
    return this.verifierMode === VerifierMode.SETUP;
  }

  isTallyMode(): boolean {
    return this.verifierMode === VerifierMode.TALLY;
  }

  protected readonly VerifierMode = VerifierMode;
}
