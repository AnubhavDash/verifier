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
import {ProcessorService} from '../processor.service';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {VerificationDefinition} from './VerificationDefinition.interface';
import {environment} from '../../environments/environment';
import {VerifierEvent} from './verifier-event.enum';
import {Component, OnInit} from '@angular/core';
import * as html2pdf from 'html2pdf.js';
import {VerifierMode} from '../verifier-mode/verifier-mode.enum';
import {DatasetConfiguration} from '../dataset/dataset-configuration/DatasetConfiguration.interface';
import {DatasetType} from '../dataset/dataset-upload/dataset-type.enum';

declare let $: any;


@Component({
  templateUrl: 'header.component.html',
  styleUrls: ['header.component.css'],
  providers: []
})
export class HeaderComponent implements OnInit {

  verificationsSize = 0;
  displayDatasetInformation = true;
  startDisabled = true;
  processStarted = false;
  eventStarted: string = null;
  verifierEvent = VerifierEvent;
  isExportingToPDF = false;
  configuration: DatasetConfiguration = new DatasetConfiguration();
  contextFilename = '';
  contextHash = '';
  filename = '';
  hash = '';
  startDate: string = null;
  endDate: string = null;
  printMode = false;
  toggleMessage = true;
  totalNumberOfSetupVerifications = 0;
  totalNumberOfTallyVerifications = 0;
  uploadingDataset = false;
  verifications = {};
  verificationStatusFilter = '';
  verifierMode = null;

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

  private stompClient;

  constructor(private processorService: ProcessorService) {
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
        verifications[verification.id] = HeaderComponent.convert(verification);
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

  // Change verifier mode.
  setNewVerifierMode(newVerifierMode: VerifierMode): void {
    this.printMode = false;
    this.contextHash = undefined;
    this.filename = undefined;
    this.hash = undefined;
    this.configuration = new DatasetConfiguration();
    this.configuration.tally = null;
    this.configuration.setup = null;
    this.startDisabled = true;

    if(!this.contextFilename) {
      this.verifierMode = newVerifierMode;
      return;
    }

    this.contextFilename = undefined;
    this.resetProcess();
    this.processorService.changeMode().subscribe((_value) => {
      this.verifierMode = newVerifierMode;
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
      this.printMode = false;
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
          verificationsCopy[result.id] = HeaderComponent.convert(result);
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

  // Filters.
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

  // Status counter.
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

  // PDF Export.
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

  // General.
  actionsDisabled(): boolean {
    return this.uploadingDataset || (this.processStarted && !this.isProcessComplete());
  }

  // Upload dataset.
  getDatasetTypeContext(): DatasetType {
    return (this.verifierMode) ? DatasetType.CONTEXT : undefined;
  }

  getDatasetType(): DatasetType {
    switch (this.verifierMode) {
      case VerifierMode.SETUP: return DatasetType.SETUP;
      case VerifierMode.TALLY: return DatasetType.TALLY;
      default: return undefined;
    }
  }

  setDatasetConfigurationContext(configuration: DatasetConfiguration): void {
    this.contextFilename = configuration.context.filename;
    this.contextHash = configuration.context.hash;
    this.configuration.context = configuration.context;
  }

  setDatasetConfiguration(configuration: DatasetConfiguration): void {
    switch (this.verifierMode) {
      case VerifierMode.SETUP: return this.setDatasetConfigurationSetup(configuration);
      case VerifierMode.TALLY: return this.setDatasetConfigurationTally(configuration);
    }
  }

  uploadingDatasetContextReset(event: boolean): void {
    this.uploadingDataset = event;

    if(this.uploadingDataset) {
      this.contextFilename = undefined;
      this.contextHash = undefined;
      this.configuration = new DatasetConfiguration();

      this.uploadingDatasetReset(event);
    }
  }

  uploadingDatasetReset(event: boolean): void {
    this.uploadingDataset = event;

    if(this.uploadingDataset) {
      this.filename = undefined;
      this.hash = undefined;
      this.configuration.tally = null;
      this.configuration.setup = null;
      this.startDisabled = true;
      this.eventStarted = null;
      this.startDate = null;
      this.endDate = null;
    }
  }

  // Upload dataset.
  private setDatasetConfigurationSetup(configuration: DatasetConfiguration): void {
    this.filename = configuration.setup.filename;
    this.hash = configuration.setup.hash;
    this.configuration.setup = configuration.setup;
    if (configuration.context) {
      this.startDisabled = false;
    }
  }

  private setDatasetConfigurationTally(configuration: DatasetConfiguration): void {
    this.filename = configuration.tally.filename;
    this.hash = configuration.tally.hash;
    this.configuration.tally = configuration.tally;
    if (configuration.context) {
      this.startDisabled = false;
    }
  }

  // Status counter.
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

  // Filters.
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

  // Current Date.
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

