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
import {Component, inject, OnInit} from '@angular/core';
import * as html2pdf from 'html2pdf.js';
import {DatasetConfiguration} from '../shared/types/dataset-configuration';
import {VerifierMode} from '../shared/types/verifier-mode.enum';
import {ProcessTime} from "../shared/types/process-time";
import {VerificationStatus} from "../shared/types/verification-status";
import {formatDate} from "@angular/common";
import {TranslateService} from "@ngx-translate/core";

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

  private readonly translate: TranslateService = inject(TranslateService);

  ngOnInit(): void {
    this.verifierMode =  JSON.parse(sessionStorage.getItem("verifierMode"));
    this.configuration = JSON.parse(sessionStorage.getItem("configuration"));
    this.verifications = JSON.parse(sessionStorage.getItem("verifications"));
    this.verificationsSize = Object.keys(this.verifications).length;
    this.processTime = JSON.parse(sessionStorage.getItem("processTime"));
  }

  // PDF Export.
  exportToPDF(): void {
    const pdfFileName = this.getFilename();
    html2pdf()
        .from(document.getElementById('verification-report'))
        .set({
          margin: [10, 5],
          image: {type: 'jpeg', quality: 0.98},
          html2canvas: {scale: 4},
          pagebreak: {
            before: ['.html2pdf-break-page'],
            mode: ['avoid-all', 'css', 'legacy']
          },
          jsPDF: {unit: 'mm', format: 'a4', orientation: 'landscape'}
        })
        .toPdf()
        .get('pdf')
        .then(function (pdf) {
          const total = pdf.internal.getNumberOfPages();

          for (let i = 1; i <= total; i++) {
            pdf.setPage(i);
            pdf.setFontSize(10);
            pdf.setTextColor(90);
            pdf.text(`${pdfFileName}`, 5, pdf.internal.pageSize.getHeight() - 5);
            pdf.text(`${i}/${total}`, pdf.internal.pageSize.getWidth() - 10, pdf.internal.pageSize.getHeight() - 5);
          }

        })
        .save(pdfFileName);
  }

  getFilename(): string {
    const phase = this.verifierMode === VerifierMode.SETUP ? 'VerifyConfigPhase' : 'VerifyTally';
    const formattedDate = formatDate(new Date(), 'YYYYMMdd_HHmm', 'en');
    // Verifier-report-{$Type}-{Seed}-{timestamp}.pdf ($Type is VerifyConfigPhase or VerifyTally)
    return `Verifier-report-${phase}-${this.configuration.context.electionEventSeed}-${formattedDate}`;
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

