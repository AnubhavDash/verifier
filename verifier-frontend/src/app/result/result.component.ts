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
import {Component, inject, OnInit, signal} from '@angular/core';
import {ResultService} from "./result.service";
import {BallotBoxResults} from "e-voting-libraries-ui-kit";
import {DatasetConfiguration} from "../shared/types/dataset-configuration";
import {ExportToPdfService} from "../shared/services/export-to-pdf.service";
import {SessionStorageService} from "../shared/services/session-storage.service";

@Component({
  templateUrl: 'result.component.html',
  providers: [],
  standalone: false
})
export class ResultComponent implements OnInit {
  configuration: DatasetConfiguration;
  result: BallotBoxResults[];
  isReportGenerating = signal(false);
  isReportGenerated= signal(false);

  private readonly resultService: ResultService = inject(ResultService);
  private readonly exportToPdfService: ExportToPdfService = inject(ExportToPdfService);
  private readonly sessionStorageService = inject(SessionStorageService);

  ngOnInit(): void {
    this.configuration = this.sessionStorageService.getConfiguration();
    this.resultService.getElectionEventResult().subscribe(result => {
      this.result = this.sortBallotBoxes(result);
    });
    this.isReportGenerated.set(this.sessionStorageService.getElectionResultReportGenerated());
  }

  sortBallotBoxes(ballotBoxResults: BallotBoxResults[]): BallotBoxResults[] {
    return [...ballotBoxResults].sort((a, b) => a.description.localeCompare(b.description));
  }

  // PDF Export.
  exportToPDF(): void {
    this.isReportGenerating.set(true);
    this.isReportGenerated.set(false);
    // PDF Prefix: Election-result-{Seed}
    const pdfFilenamePrefix = `Election-result-${this.configuration.context.electionEventSeed}`;
    this.exportToPdfService.export(
      document.getElementById('election-event-result'),
      pdfFilenamePrefix,
      () => this.completeReportGeneration());
  }

  completeReportGeneration(){
    this.isReportGenerating.set(false);
    this.isReportGenerated.set(true);
    this.sessionStorageService.setElectionResultReportGenerated(this.isReportGenerated());
  }
}

