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
import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {VerifierMode} from "../types/verifier-mode.enum";
import {VerificationDefinition} from "../types/verification-definition";
import {ProcessTime} from "../types/process-time";
import {DatasetConfiguration} from "../types/dataset-configuration";

const VERIFIER_MODE_KEY = 'verifierMode';
const CONFIGURATION_KEY = 'configuration';
const VERIFICATIONS_KEY = 'verifications';
const PROCESS_TIME_KEY = 'processTime';
const VERIFICATION_REPORT_GENERATED_KEY = 'verificationReportGenerated';
const ELECTION_RESULT_REPORT_GENERATED_KEY = 'electionResultReportGenerated';

@Injectable({
  providedIn: 'root',
})
export class SessionStorageService {
  private readonly datasetLoadedSubject = new BehaviorSubject<boolean>(this.getDatasetLoaded());
  private readonly verificationCompletedSubject = new BehaviorSubject<boolean>(this.getVerificationCompleted());
  private readonly verificationReportGeneratedSubject = new BehaviorSubject<boolean>(this.getVerificationReportGenerated());
  private readonly electionResultReportGeneratedSubject = new BehaviorSubject<boolean>(this.getElectionResultReportGenerated());

  datasetLoaded$ = this.datasetLoadedSubject.asObservable();
  verificationCompleted$ = this.verificationCompletedSubject.asObservable();
  verificationReportGenerated$ = this.verificationReportGeneratedSubject.asObservable();
  electionResultReportGenerated$ = this.electionResultReportGeneratedSubject.asObservable();

  getDatasetLoaded(): boolean {
    return (this.getVerifierMode() !== null && this.getConfiguration() !== null) || false;
  }

  setVerifierMode(value: VerifierMode): void {
    sessionStorage.setItem(VERIFIER_MODE_KEY, JSON.stringify(value));
  }

  getVerifierMode(): VerifierMode {
    return JSON.parse(sessionStorage.getItem(VERIFIER_MODE_KEY));
  }

  clearVerifierMode(): void {
    sessionStorage.removeItem(VERIFIER_MODE_KEY);
    this.datasetLoadedSubject.next(false);
  }

  setConfiguration(value: DatasetConfiguration): void {
    sessionStorage.setItem(CONFIGURATION_KEY, JSON.stringify(value));
    this.datasetLoadedSubject.next(true);
  }

  getConfiguration(): DatasetConfiguration {
    return JSON.parse(sessionStorage.getItem(CONFIGURATION_KEY));
  }

  clearConfiguration(): void {
    sessionStorage.removeItem(CONFIGURATION_KEY);
    this.datasetLoadedSubject.next(false);
  }

  getVerificationCompleted(): boolean {
    return JSON.parse(sessionStorage.getItem(VERIFICATIONS_KEY)) !== null || false;
  }

  setVerifications(value: Record<string, VerificationDefinition>): void {
    sessionStorage.setItem(VERIFICATIONS_KEY, JSON.stringify(value));
    this.verificationCompletedSubject.next(true);
  }

  getVerifications(): Record<string, VerificationDefinition> {
    return JSON.parse(sessionStorage.getItem(VERIFICATIONS_KEY));
  }

  clearVerifications(): void {
    sessionStorage.removeItem(VERIFICATIONS_KEY);
    this.verificationCompletedSubject.next(false);
  }

  setProcessTime(value: ProcessTime): void {
    sessionStorage.setItem(PROCESS_TIME_KEY, JSON.stringify(value));
  }

  getProcessTime(): ProcessTime {
    return JSON.parse(sessionStorage.getItem(PROCESS_TIME_KEY));
  }

  clearProcessTime(): void {
    sessionStorage.removeItem(PROCESS_TIME_KEY);
  }

  setVerificationReportGenerated(value: boolean): void {
    sessionStorage.setItem(VERIFICATION_REPORT_GENERATED_KEY, JSON.stringify(value));
    this.verificationReportGeneratedSubject.next(value);
  }

  getVerificationReportGenerated(): boolean {
    return JSON.parse(sessionStorage.getItem(VERIFICATION_REPORT_GENERATED_KEY)) || false;
  }

  clearVerificationReportGenerated(): void {
    sessionStorage.removeItem(VERIFICATION_REPORT_GENERATED_KEY);
    this.verificationReportGeneratedSubject.next(false);
  }

  setElectionResultReportGenerated(value: boolean): void {
    sessionStorage.setItem(ELECTION_RESULT_REPORT_GENERATED_KEY, JSON.stringify(value));
    this.electionResultReportGeneratedSubject.next(value);
  }

  getElectionResultReportGenerated(): boolean {
    return JSON.parse(sessionStorage.getItem(ELECTION_RESULT_REPORT_GENERATED_KEY)) || false;
  }

  clearElectionResultReportGenerated(): void {
    sessionStorage.removeItem(ELECTION_RESULT_REPORT_GENERATED_KEY);
    this.electionResultReportGeneratedSubject.next(false);
  }

}
