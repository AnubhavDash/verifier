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
import {VerificationDefinition} from '../shared/types/verification-definition';
import {Component, inject, OnInit, signal} from '@angular/core';
import {DatasetConfiguration} from '../shared/types/dataset-configuration';
import {VerifierMode} from '../shared/types/verifier-mode.enum';
import {ProcessTime} from "../shared/types/process-time";
import {VerificationStatus} from "../shared/types/verification-status";
import {ExportToPdfService} from "../shared/services/export-to-pdf.service";
import {Router} from "@angular/router";
import {SessionStorageService} from "../shared/services/session-storage.service";
import {formatDate} from "@angular/common";

@Component({
  templateUrl: 'report.component.html',
  providers: [],
  standalone: false
})
export class ReportComponent implements OnInit {
  configuration: DatasetConfiguration;
  verifications: Record<string, VerificationDefinition> = {};
  verificationsSize = 0;
  verifierMode: VerifierMode;
  processTime: ProcessTime;
  isReportGenerating = signal(false);
  isReportGenerated= signal(false);

  private readonly exportToPdfService: ExportToPdfService = inject(ExportToPdfService);
  private readonly router: Router = inject(Router);
  private readonly sessionStorageService = inject(SessionStorageService);

  ngOnInit(): void {
    this.verifierMode =  this.sessionStorageService.getVerifierMode();
    this.configuration = this.sessionStorageService.getConfiguration();
    this.verifications = this.sessionStorageService.getVerifications();
    this.verificationsSize = Object.keys(this.verifications).length;
    this.processTime = this.sessionStorageService.getProcessTime();
    this.isReportGenerated.set(this.sessionStorageService.getVerificationReportGenerated());
  }

  next() {
    this.router.navigate(['/export-result']);
  }

  // PDF Export.
  exportToPDF(): void {
    this.isReportGenerating.set(true);
    this.isReportGenerated.set(false);
    // PDF Prefix: Verifier-report-{$Type}-{Seed} ($Type is VerifyConfigPhase or VerifyTally)
    const type = this.verifierMode === VerifierMode.SETUP ? 'VerifyConfigPhase' : 'VerifyTally';
    const pdfFilenamePrefix = `Verifier-report-${type}-${this.configuration.context.electionEventSeed}`;

    this.exportToPdfService.exportVerificationReport(
      pdfFilenamePrefix,
      {
        configuration: this.configuration,
        startDate: formatDate(this.processTime.start, 'dd.MM.yyyy HH:mm:ss', 'en'),
        endDate: formatDate(this.processTime.end, 'dd.MM.yyyy HH:mm:ss', 'en'),
        verifierMode: this.verifierMode,
        statusCounterOK: this.statusCounterOK(),
        statusCounterNOK: this.statusCounterNOK(),
        statusCounterERROR: this.statusCounterERROR(),
        verifications: this.verifications
      },
      () => this.completeReportGeneration()
    );
  }

  isShownNextButton(): boolean {
    return this.verifierMode === VerifierMode.TALLY;
  }

  completeReportGeneration(){
    this.isReportGenerating.set(false);
    this.isReportGenerated.set(true);
    this.sessionStorageService.setVerificationReportGenerated(this.isReportGenerated());
  }

  statusCounterOK(): number {
    return this.statusCounter(VerificationStatus.OK);
  }

  statusCounterNOK(): number {
    return this.statusCounter(VerificationStatus.NOK);
  }
  statusCounterERROR(): number {
    return this.statusCounter(VerificationStatus.ERROR);
  }

  private statusCounter(status: string): number {
    return Object.keys(this.verifications)
      .filter(key => this.verifications[key].status === status)
      .length;
  }
}

