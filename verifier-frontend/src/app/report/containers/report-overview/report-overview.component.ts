/*
 * Copyright 2022 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import {ProcessorService} from '../../services/processor.service';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {VerificationDefinition} from '../../models/VerificationDefinition.interface';
import {environment} from '../../../../environments/environment';
import {VerifierEvent} from '../../models/verifier-event.enum';
import {Component, OnInit} from '@angular/core';
import * as html2pdf from 'html2pdf.js';
import packageJson from '../../../../../package.json';


@Component({
  templateUrl: 'report-overview.component.html',
  styleUrls: ['report-overview.component.css'],
  providers: []
})
export class ReportOverviewComponent implements OnInit {

  verifications = {};
  verificationsSize = 0;
  totalNumberOfSetupVerifications = 0;
  totalNumberOfTallyVerifications = 0;
  verificationStatusFilter = '';
  toggleMessage = true;
  printMode = false;
  startDisabled = true;
  processStarted = false;
  eventStarted: string = null;
  verifierEvent = VerifierEvent;
  startDate: string = null;
  endDate: string = null;
  isExportingToPDF = false;
  datasetLoading = false;
  datasetLoadingError = false;
  filename = '';
  hash = '';
  electionEventId = '';
  numberOfAuthorizedVoters = 0;
  numberOfTestVoters = 0;
  fingerprints: Map<string, string> = new Map();
  appVersion = '';
  electionEventName = '';
  electionEventDate: string;
  numberOfElections = 0;
  numberOfVotes = 0;
  numberOfNonTestBallotBoxes = 0;
  numberOfTestBallotBoxes = 0;
  totalNumberOfAuthorizedNonTestVoters = 0;
  totalNumberOfTestVoters = 0;
  numberOfConfirmedNonTestVotes: number = null;
  numberOfConfirmedTestVotes: number = null;

  verificationFilter = {
    ALL: {
      value: '',
      active: true
    },
    RUNNING: {
      value: 'RUNNING',
      active: false
    },
    OK: {
      value: 'OK',
      active: false
    },
    NOK: {
      value: 'NOK',
      active: false
    },
    ERROR: {
      value: 'UNEXPECTED_ERROR',
      active: false
    },
    NA: {
      value: 'NA',
      active: false
    }
  };

  fingerprintsNames = {
    canton: 'Canton',
    control_component_1: 'Control Component 1',
    control_component_2: 'Control Component 2',
    control_component_3: 'Control Component 3',
    control_component_4: 'Control Component 4',
    sdm_config: 'Setup Component',
    sdm_tally: 'Tally Control Component'
  };

  protected readonly VerifierEvent = VerifierEvent;

  private stompClient;

  constructor(private processorService: ProcessorService) {
    this.appVersion = packageJson.version;
  }

  static convert(input: any): VerificationDefinition {
    const result = new VerificationDefinition();
    result.id = input.id;
    result.verificationId = input.verificationId;
    result.block = input.block;
    result.name = input.name;
    result.category = input.category;
    result.status = input.status;
    result.description = input.description;
    result.message = input.message;
    result.events = input.verifierEvents;
    result.errorStack = input.errorStack;
    return result;
  }

  ngOnInit(): void {
    this.initTable();
    this.initializeWebSocketConnection();
  }

  initTable() {
    this.processorService.getVerifications().subscribe(results => {
      this.verificationsSize = results.length;
      const verifications = {};
      for (const verification of results) {
        verifications[verification.id] = ReportOverviewComponent.convert(verification);
      }
      this.verifications = verifications;

      this.totalNumberOfSetupVerifications = Object.keys(verifications)
          .filter(key => verifications[key].block === 'setup')
          .length;

      this.totalNumberOfTallyVerifications = Object.keys(verifications)
          .filter(key => verifications[key].block === 'tally')
          .length;
    });
  }

  startProcess(runOption: string): void {
    this.processStarted = true;
    this.startDisabled = true;
    this.eventStarted = runOption;
    this.endDate = null;
    if (runOption === VerifierEvent.PRE_SETUP || runOption === VerifierEvent.PRE_TALLY) {
      this.startDate = this.getCurrentDate();
    }
    this.processorService.processVerifications(runOption).subscribe(() => {
      console.log('processVerifications ', runOption);
      this.updateStatus(runOption);
    });
  }

  startSecondaryProcess(runOption: string): void {
    if (this.isProcessComplete() && this.statusCounterNOK() === 0 && this.statusCounterERROR() === 0) {
      this.processStarted = false;
      this.startDisabled = false;
      this.resetStatus();
      this.startProcess(runOption);
    }
  }

  updateStatus(runOptions: string) {
    Object.keys(this.verifications).forEach(key => {
      let notFound = true;
      this.verifications[key].events.forEach(event => {
        if (event.indexOf(runOptions) >= 0) {
          this.verifications[key].status = this.verificationFilter.RUNNING.value;
          notFound = false;
        }
      });
      if (notFound) {
        this.verifications[key].status = this.verificationFilter.NA.value;
      }
    });
  }

  isProcessComplete() {
    const isProcessCompete = this.statusCounterAll() === this.verificationsSize;

    if (isProcessCompete && this.endDate === null) {
      this.endDate = this.getCurrentDate();
    }

    return isProcessCompete;
  }

  resetProcess(): void {
    this.processorService.resetVerifications().subscribe((_value) => {
      this.filterVerificationsAll();
      this.initTable();
      this.processStarted = false;
      this.startDisabled = false;
      this.eventStarted = null;
      this.startDate = null;
      this.endDate = null;
    });
  }

  initializeWebSocketConnection() {
    const ws = new SockJS(environment.appUrl + '/socket');
    this.stompClient = Stomp.over(ws);
    const that = this;
    this.stompClient.connect({authorization: environment.authorizationHeaderValue}, function (_frame) {
      that.stompClient.subscribe('/pushUpdate', (message) => {
        if (message.body) {
          const verificationsCopy = (JSON.parse(JSON.stringify(that.verifications)));
          const result = JSON.parse(message.body);
          verificationsCopy[result.id] = ReportOverviewComponent.convert(result);
          that.verifications = verificationsCopy;
          that.activateResultVerificationFilters();

          if (that.eventStarted === that.verifierEvent.PRE_SETUP) {
            that.startSecondaryProcess(that.verifierEvent.SETUP);
          }
          if (that.eventStarted === that.verifierEvent.PRE_TALLY) {
            that.startSecondaryProcess(that.verifierEvent.TALLY);
          }
        }
      });
    });
  }

  isRUNNING(status) {
    return status === 'RUNNING';
  }

  isOK(status) {
    return status === 'OK';
  }

  isNotOK(status) {
    return status === 'NOK';
  }

  isNA(status) {
    return status === 'NA';
  }

  isError(status) {
    return status === 'UNEXPECTED_ERROR';
  }

  filterVerificationsAll() {
    this.verificationStatusFilter = this.verificationFilter['ALL'].value;
    const allFilters = Object.keys(this.verificationFilter);
    allFilters.forEach((key) => {
      this.verificationFilter[key].active = key === 'ALL';
    });
  }

  filterVerificationsRUNNING() {
    this.toggleVerificationFilter('RUNNING');
  }

  filterVerificationsOK() {
    this.toggleVerificationFilter('OK');
  }

  filterVerificationsNOK() {
    this.toggleVerificationFilter('NOK');
  }

  filterVerificationsERROR() {
    this.toggleVerificationFilter('ERROR');
  }

  filterVerificationsNA() {
    this.toggleVerificationFilter('NA');
  }

  statusCounterAll() {
    return this.statusCounterOK() + this.statusCounterNOK() + this.statusCounterNA() + this.statusCounterERROR();
  }

  statusCounterRUNNING() {
    return this.statusCounter(this.verificationFilter.RUNNING.value);
  }

  statusCounterOK() {
    return this.statusCounter(this.verificationFilter.OK.value);
  }

  statusCounterNOK() {
    return this.statusCounter(this.verificationFilter.NOK.value);
  }

  statusCounterNA() {
    return this.statusCounter(this.verificationFilter.NA.value);
  }

  statusCounterERROR() {
    return this.statusCounter(this.verificationFilter.ERROR.value);
  }

  // PDF Export
  exportToPDF() {
    this.isExportingToPDF = true;
    const pdfFileName = `verifier-report-${this.getCurrentDateFile()}`;
    const options = {
      margin: [5, 0],
      filename: `${pdfFileName}.pdf`,
      pagebreak: {before: ['.html2pdf-break-page'], avoid: ['.html2pdf-no-break']},
      image: {type: 'jpeg', quality: 0.1},
      html2canvas: {scale: 4},
      jsPDF: {unit: 'mm', format: 'a4', orientation: 'landscape'}
    };

    const element = document.getElementById('verification-report');

    const component = this;
    html2pdf().from(element).set(options).toPdf().get('pdf').then(function (pdf) {
      const totalPages = pdf.internal.getNumberOfPages();

      for (let i = 1; i <= totalPages; i++) {
        pdf.setPage(i);
        pdf.setFontSize(10);
        pdf.setTextColor(100);
        pdf.text(`${pdfFileName} ${i}/${totalPages}`, pdf.internal.pageSize.getWidth() - 60, 5);
      }

      component.isExportingToPDF = false;
    }).save();
  }

  configurationUpload(event) {
    const file = event.target.files[0];
    if (file) {
      this.datasetLoading = true;
      this.filename = '';
      this.hash = '';
      this.electionEventId = '';
      this.numberOfAuthorizedVoters = 0;
      this.numberOfTestVoters = 0;
      this.fingerprints = new Map();
      this.electionEventName = '';
      this.numberOfElections = 0;
      this.numberOfVotes = 0;
      this.numberOfNonTestBallotBoxes = 0;
      this.numberOfTestBallotBoxes = 0;
      this.totalNumberOfAuthorizedNonTestVoters = 0;
      this.totalNumberOfTestVoters = 0;
      this.eventStarted = null;
      this.startDate = null;
      this.endDate = null;

      this.processorService.uploadDataset(file).subscribe({
        next: () => {
          this.startDisabled = false;
          this.processorService.getDatasetConfiguration().subscribe(configuration => {
            this.datasetLoading = false;
            this.datasetLoadingError = false;
            this.filename = configuration.filename;
            this.hash = configuration.hash;
            this.electionEventId = configuration.electionEventId;
            this.numberOfAuthorizedVoters = configuration.numberOfAuthorizedVoters;
            this.numberOfTestVoters = configuration.numberOfTestVoters;
            this.fingerprints = configuration.aliasesToFingerprints;
            this.electionEventName = configuration.electionEventName;
            this.electionEventDate = configuration.electionEventDate;
            this.numberOfElections = configuration.numberOfElections;
            this.numberOfVotes = configuration.numberOfVotes;
            this.numberOfNonTestBallotBoxes = configuration.numberOfNonTestBallotBoxes;
            this.numberOfTestBallotBoxes = configuration.numberOfTestBallotBoxes;
            this.totalNumberOfAuthorizedNonTestVoters = configuration.totalNumberOfAuthorizedNonTestVoters;
            this.totalNumberOfTestVoters = configuration.totalNumberOfTestVoters;
            this.numberOfConfirmedNonTestVotes = configuration.numberOfConfirmedNonTestVotes;
            this.numberOfConfirmedTestVotes = configuration.numberOfConfirmedTestVotes;
          });
        },
        error: () => {
          this.datasetLoading = false;
          this.datasetLoadingError = true;
        }
      });
    }
  }

  // Status counter
  private statusCounter(statusValue: string) {
    const filtered = Object.keys(this.verifications)
      .filter(key => {
        if (statusValue.indexOf('|') > -1) {
          const filters = statusValue.split('|');
          return this.verifications[key].status === filters[0] || this.verifications[key].status === filters[1];
        } else {
          return this.verifications[key].status === statusValue;
        }
      })
      .reduce((obj, key) => ({
        ...obj,
        [key]: this.verifications[key]
      }), {});
    return Object.keys(filtered).length;
  }

  private resetStatus() {
    Object.keys(this.verifications).forEach(key => this.verifications[key].status = null);
  }

  // Filters
  private toggleVerificationFilter(key: string) {
    const filter = this.verificationFilter[key];
    if (filter.active) {
      filter.active = false;
      this.verificationStatusFilter = this.verificationStatusFilter.replace(new RegExp('\\b' + filter.value + '\\|', 'g'), '');
      this.verificationFilter['ALL'].active = this.verificationStatusFilter === '';
    } else {
      this.activateVerificationFilter(key);
    }
  }

  private activateVerificationFilter(key: string) {
    const filter = this.verificationFilter[key];
    if (!filter.active) {
      this.verificationFilter['ALL'].active = false;
      filter.active = true;
      this.verificationStatusFilter += `${filter.value}|`;
    }
  }

  private activateResultVerificationFilters() {
    if (this.processStarted && this.isProcessComplete()) {
      this.activateVerificationFilter('OK');
      this.activateVerificationFilter('NOK');
      this.activateVerificationFilter('ERROR');
    }
  }

  // Current Date
  private getCurrentDateFile() {
    return this.formatDate(new Date(), true);
  }

  private getCurrentDate() {
    return this.formatDate(new Date(), false);
  }

  private formatDate(dateToFormat, isFileFormat) {
    const hours = String(dateToFormat.getHours()).padStart(2, '0');
    const minutes = String(dateToFormat.getMinutes()).padStart(2, '0');
    const date = String(dateToFormat.getDate()).padStart(2, '0');
    const month = String(dateToFormat.getMonth() + 1).padStart(2, '0');
    const year = dateToFormat.getFullYear();

    if (isFileFormat) {
      return `${year}${month}${date}-${hours}${minutes}`;
    } else {
      return `${date}.${month}.${year} ${hours}:${minutes}`;
    }
  }
}

