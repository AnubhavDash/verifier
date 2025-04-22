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
import {VerifyService} from './verify.service';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {VerificationDefinition} from '../shared/types/verification-definition';
import {environment} from '../../environments/environment';
import {VerifierEvent} from '../shared/types/verifier-event';
import {Component, inject, OnInit} from '@angular/core';
import {DatasetConfiguration} from '../shared/types/dataset-configuration';
import {VerifierMode} from '../shared/types/verifier-mode.enum';
import {Router} from "@angular/router";
import {VerificationStatus} from "../shared/types/verification-status";
import {ProcessTime} from "../shared/types/process-time";

@Component({
  templateUrl: 'verify.component.html',
  providers: [],
  standalone: false
})
export class VerifyComponent implements OnInit {
  startedEvent: string = null;
  processStarted = false;
  processTime: ProcessTime;
  runtimeRefresh: any;

  verifierMode: VerifierMode;
  configuration: DatasetConfiguration = new DatasetConfiguration();
  verifications: Record<string, VerificationDefinition>;
  verificationsSize = 0;

  private readonly router: Router = inject(Router);
  private readonly processorService: VerifyService = inject(VerifyService);

  ngOnInit(): void {
    this.verifierMode = JSON.parse(sessionStorage.getItem("verifierMode"));
    const configuration = JSON.parse(sessionStorage.getItem("configuration"));
    this.configuration = configuration ? configuration : new DatasetConfiguration();
    this.verifications = JSON.parse(sessionStorage.getItem('verifications'));
    if (!this.verifications) {
      this.initVerifications();
    } else {
      this.processStarted = true;
      this.verificationsSize = Object.keys(this.verifications).length;
    }
    const processTime = JSON.parse(sessionStorage.getItem("processTime"))
    this.processTime = processTime ? processTime : new ProcessTime();
    this.initializeWebSocketConnection();
  }

  next() {
    this.router.navigate(['/report']);
  }

  initVerifications(): void {
    this.processorService.getVerifications(this.verifierMode).subscribe(results => {
      this.verifications = {};
      this.verificationsSize = results.length;
      for (const verification of results) {
        this.verifications[verification.id] = verification;
        this.verifications[verification.id].status = 'IDLE'
      }
    });
  }

  initializeWebSocketConnection(): void {
    const ws = new SockJS(environment.appUrl + '/socket');
    const stompClient = Stomp.over(ws);
    const that = this;
    stompClient.connect({authorization: environment.authorizationHeaderValue}, function (_frame) {
      stompClient.subscribe('/pushUpdate', (message) => {
        if (message.body) {
          const verificationsCopy = (JSON.parse(JSON.stringify(that.verifications)));
          const result = JSON.parse(message.body);
          verificationsCopy[result.id] = result;
          that.verifications = verificationsCopy;

          if (that.startedEvent === VerifierEvent.PRE_SETUP) {
            that.startSecondaryProcess(VerifierEvent.SETUP);
          }
          if (that.startedEvent === VerifierEvent.PRE_TALLY) {
            that.startSecondaryProcess(VerifierEvent.TALLY);
          }
        }
      });
    });
  }

  // Start verifications.
  startVerification(): void {
    switch (this.verifierMode) {
      case VerifierMode.SETUP:
        return this.startProcess(VerifierEvent.PRE_SETUP);
      case VerifierMode.TALLY:
        return this.startProcess(VerifierEvent.PRE_TALLY);
      default:
        return;
    }
  }

  startSecondaryProcess(runOption: string): void {
    if (this.isProcessComplete() && this.statusCounter(VerificationStatus.NOK) === 0 && this.statusCounter(VerificationStatus.ERROR) === 0) {
      this.processStarted = false;
      this.resetStatus();
      this.startProcess(runOption);
    }
  }

  startProcess(runOption: string): void {
    this.startedEvent = runOption;
    this.processStarted = true;
    this.processTime.end = null;
    this.processTime.runtime = this.getRunTime();
    this.runtimeRefresh = setInterval(() => this.processTime.runtime += 1000, 1000);

    if (runOption === VerifierEvent.PRE_SETUP || runOption === VerifierEvent.PRE_TALLY) {
      this.processTime.start = new Date();
    }

    this.processorService.processVerifications(runOption).subscribe(() => {
      this.updateStatus(runOption);
    });
  }

  updateStatus(runOptions: string): void {
    Object.keys(this.verifications).forEach(key => {
      let notFound = true;
      this.verifications[key].verifierEvents.forEach(event => {
        if (event.indexOf(runOptions) >= 0) {
          this.verifications[key].status = VerificationStatus.RUNNING;
          notFound = false;
        }
      });
      if (notFound) {
        this.verifications[key].status = VerificationStatus.NA;
      }
    });
  }

  isProcessComplete(): boolean {
    if (!this.processStarted) {
      return false;
    }
    const isProcessComplete = this.statusCounterAll() === this.verificationsSize;

    if (isProcessComplete) {
      if (this.processTime.end == null) {
        this.processTime.end = new Date();
      }
      clearInterval(this.runtimeRefresh);
      sessionStorage.setItem('verifications', JSON.stringify(this.verifications));
      sessionStorage.setItem('processTime', JSON.stringify(this.processTime));
    }

    return isProcessComplete;
  }

  resetProcess(): void {
    this.processorService.resetVerifications().subscribe((_value) => {
      this.initVerifications();
      this.processStarted = false;
      this.startedEvent = null;
      this.processTime = new ProcessTime();
      sessionStorage.removeItem('verifications');
    });
  }

  statusCounterAll(): number {
    return this.statusCounter(VerificationStatus.OK)
      + this.statusCounter(VerificationStatus.NOK)
      + this.statusCounter(VerificationStatus.NA)
      + this.statusCounter(VerificationStatus.ERROR);
  }

  statusCounterRUNNING(): number {
    return this.statusCounter(VerificationStatus.RUNNING);
  }

  hasFailed() {
    return this.statusCounter(VerificationStatus.NOK) > 0;
  }

  hasError() {
    return this.statusCounter(VerificationStatus.ERROR) > 0;
  }

  private statusCounter(status: string): number {
    return Object.keys(this.verifications)
      .filter(key => this.verifications[key].status === status)
      .length;
  }

  private resetStatus(): void {
    Object.keys(this.verifications).forEach(key => this.verifications[key].status = VerificationStatus.IDLE);
  }

  private getRunTime(): number {
    const runTimeBase = new Date(2000, 1, 1, 0, 0, 0).getTime();
    const endDate = this.processTime.end ? new Date(this.processTime.end) : new Date();
    const startDate = this.processTime.start ? new Date(this.processTime.start) : endDate;

    return runTimeBase + (endDate.getTime() - startDate.getTime());
  }
}

