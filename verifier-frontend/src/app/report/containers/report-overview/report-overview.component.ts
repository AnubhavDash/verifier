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
import {ProcessorService} from '../../services/processor.service';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {VerificationDefinition} from '../../models/VerificationDefinition.interface';
import {environment} from '../../../../environments/environment';
import {VerifierEvent} from '../../models/verifier-event.enum';
import {Component, OnInit} from '@angular/core';
import * as html2pdf from 'html2pdf.js';
import packageJson from '../../../../../package.json';
import {DatasetType} from '../../models/dataset-type.enum';
import {VerifierMode} from '../../models/verifier-mode.enum';


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
  verifierMode = null;
  printMode = false;
  displayDatasetInformation = true;
  triggerModal = false;
  startDisabled = true;
  processStarted = false;
  eventStarted: string = null;
  verifierEvent = VerifierEvent;
  startDate: string = null;
  endDate: string = null;
  isExportingToPDF = false;
  contextDatasetLoading = false;
  datasetLoading = false;
  contextDatasetLoadingError = false;
  datasetLoadingError = false;
  contextFilename = '';
  filename = '';
  contextHash = '';
  hash = '';
  electionEventId = '';
  fingerprints: Map<string, string> = new Map();
  appVersion = '';
  electionEventName = '';
  electionEventSeed = '';
  electionEventDate: string;
  numberOfElections = 0;
  numberOfVotes = 0;
  numberOfBallots = 0;
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
    sdm_config: 'Setup Component',
    sdm_tally: 'Tally Control Component',
    control_component_1: 'Control Component 1',
    control_component_2: 'Control Component 2',
    control_component_3: 'Control Component 3',
    control_component_4: 'Control Component 4'
  };

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

  fingerPrintsNamesKeys() {
    return Object.keys(this.fingerprintsNames);
  };

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

  changeMode() {
    this.triggerModal = false;
    const newVerifierMode = this.isTallyMode() ? VerifierMode.SETUP : VerifierMode.TALLY;

    this.resetProcess();
    this.processorService.changeMode().subscribe((_value) => {
      this.filename = '';
      this.hash = '';
      this.contextFilename = '';
      this.contextHash = '';
      this.startDisabled = true;
      this.datasetLoadingError = false;
      this.verifierMode = newVerifierMode;
    });
  }

  changeModeToTally() {
    this.changeModeTo(VerifierMode.TALLY);
  }

  changeModeToSetup() {
    this.changeModeTo(VerifierMode.SETUP);
  }

  changeModeTo(verifierMode: VerifierMode) {
    this.triggerModal = false;
    if (verifierMode === this.verifierMode) {
      return;
    }

    if (this.contextFilename || this.filename) {
      this.triggerModal = true;
      return;
    }

    this.startDisabled = true;
    this.datasetLoadingError = false;
    this.verifierMode = verifierMode;
  }

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
      this.startDisabled = !this.verifierMode || !this.filename || this.filename === '';
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
      this.startDisabled = !this.verifierMode || !this.filename || this.filename === '';
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

  getVerifierMode() {
    return this.verifierMode.charAt(0) + this.verifierMode.slice(1).toLowerCase();
  }

  isSetupMode() {
    return this.verifierMode === VerifierMode.SETUP;
  }

  isTallyMode() {
    return this.verifierMode === VerifierMode.TALLY;
  }

  isSetupEvent() {
    return (this.eventStarted === VerifierEvent.PRE_SETUP) || (this.eventStarted === VerifierEvent.SETUP);
  }

  isTallyEvent() {
    return (this.eventStarted === VerifierEvent.PRE_TALLY) || (this.eventStarted === VerifierEvent.TALLY);
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

  // Upload dataset
  configurationUpload(event, datasetType: string) {
    const file = event.target.files[0];
    if (file) {
      this.eventStarted = null;
      this.startDate = null;
      this.endDate = null;

      if (datasetType === DatasetType.CONTEXT) {
        return this.configurationUploadContext(file);
      } else if (this.isSetupMode()) {
        return this.configurationUploadSetup(file);
      } else if (this.isTallyMode()) {
        return this.configurationUploadTally(file);
      }
    }
  }

  configurationUploadContext(file) {
    this.startDisabled = true;
    this.contextDatasetLoading = true;
    this.contextFilename = '';
    this.filename = '';
    this.contextHash = '';
    this.hash = '';
    this.electionEventId = '';
    this.fingerprints = new Map();
    this.electionEventName = '';
    this.electionEventSeed = '';
    this.numberOfElections = 0;
    this.numberOfVotes = 0;
    this.numberOfNonTestBallotBoxes = 0;
    this.numberOfTestBallotBoxes = 0;
    this.triggerModal = false;

    this.processorService.uploadDataset(file, DatasetType.CONTEXT).subscribe({
      next: () => {
        this.processorService.getDatasetConfiguration().subscribe(configuration => {
          this.contextDatasetLoading = false;
          this.contextDatasetLoadingError = false;
          this.contextFilename = configuration.context.filename;
          this.contextHash = configuration.context.hash;
          this.electionEventId = configuration.context.electionEventId;
          this.fingerprints = configuration.context.aliasesToFingerprints;
          this.electionEventName = configuration.context.electionEventName;
          this.electionEventSeed = configuration.context.electionEventSeed;
          this.electionEventDate = configuration.context.electionEventDate;
          this.numberOfElections = configuration.context.numberOfElections;
          this.numberOfVotes = configuration.context.numberOfVotes;
          this.numberOfBallots = configuration.context.numberOfBallots;
          this.numberOfNonTestBallotBoxes = configuration.context.numberOfNonTestBallotBoxes;
          this.numberOfTestBallotBoxes = configuration.context.numberOfTestBallotBoxes;
          this.totalNumberOfAuthorizedNonTestVoters = configuration.context.totalNumberOfAuthorizedNonTestVoters;
          this.totalNumberOfTestVoters = configuration.context.totalNumberOfTestVoters;
        });
      },
      error: () => {
        this.contextDatasetLoading = false;
        this.contextDatasetLoadingError = true;
      }
    });
  }

  configurationUploadSetup(file) {
    this.datasetLoading = true;
    this.filename = '';
    this.hash = '';

    this.processorService.uploadDataset(file, DatasetType.SETUP).subscribe({
      next: () => {
        this.processorService.getDatasetConfiguration().subscribe(configuration => {
          this.datasetLoading = false;
          this.datasetLoadingError = false;
          this.filename = configuration.setup.filename;
          this.hash = configuration.setup.hash;
          if (configuration.context) {
            this.startDisabled = false;
          }
        });
      },
      error: () => {
        this.datasetLoading = false;
        this.datasetLoadingError = true;
      }
    });
  }

  configurationUploadTally(file) {
    this.datasetLoading = true;
    this.filename = '';
    this.hash = '';

    this.processorService.uploadDataset(file, DatasetType.TALLY).subscribe({
      next: () => {
        this.processorService.getDatasetConfiguration().subscribe(configuration => {
          this.datasetLoading = false;
          this.datasetLoadingError = false;
          this.filename = configuration.tally.filename;
          this.hash = configuration.tally.hash;
          this.numberOfConfirmedNonTestVotes = configuration.tally.numberOfConfirmedNonTestVotes;
          this.numberOfConfirmedTestVotes = configuration.tally.numberOfConfirmedTestVotes;
          if (configuration.context) {
            this.startDisabled = false;
          }
        });
      },
      error: () => {
        this.datasetLoading = false;
        this.datasetLoadingError = true;
      }
    });
  }

  isDatasetLoading() {
    return this.contextDatasetLoading || this.datasetLoading;
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

