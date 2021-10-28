/*
 * Copyright 2021 Post CH Ltd
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
import {Configuration} from '../../models/Configuration.interface';
import {environment} from '../../../../environments/environment';
import {VerificationTrait} from '../../models/verification-trait.enum';
import {Component, OnInit} from '@angular/core';
import * as html2pdf from '../../../../assets/html2pdf/html2pdf.bundle.min.js';


@Component({
  templateUrl: 'report-overview.component.html',
  styleUrls: ['report-overview.component.css'],
  providers: []
})
export class ReportOverviewComponent implements OnInit {

  inputDirectory: string;
  verifications = {};
  verificationsSize = 0;
  verificationStatusFilter = '';
  toggleMessage = false;
  startDisabled = false;
  processStarted = false;
  verificationTrait = VerificationTrait;

  verificationFilter = {
    ALL: {
      value: '',
      active: true
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
      value: 'FILE_ERROR|UNEXPECTED_ERROR',
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
    result.blockId = input.blockId;
    result.name = input.name;
    result.category = input.category;
    result.status = input.status;
    result.description = input.description;
    result.message = input.message;
    return result;
  }

  ngOnInit(): void {
    this.initTable();
    this.initializeWebSocketConnection();
    this.processorService.getConfigurationInputDirectory().subscribe(value => {
      this.inputDirectory = value.inputDirectory;
    });
  }

  initTable() {
    this.processorService.getVerificationStatus().subscribe(results => {
      this.verificationsSize = results.length;
      const verifications = {};
      for (const verification of results) {
        verifications[verification.id] = ReportOverviewComponent.convert(verification);
      }
      this.verifications = verifications;
      this.startDisabled = this.isProcessComplete();
    });
  }

  startProcess(runOptions?: string): void {
    const configuration = new Configuration();
    configuration.inputDirectory = this.inputDirectory;
    this.processorService.setConfigurationInputDirectory(configuration).subscribe(() => {
      this.processStarted = true;
      this.startDisabled = true;
      this.processorService.processVerifications(runOptions).subscribe();
    });
  }

  isProcessComplete() {
    return this.statusCounterAll() === this.verificationsSize;
  }

  resetProcess(): void {
    this.processorService.resetVerifications().subscribe(value => {
      this.filterVerificationsAll();
      this.initTable();
      this.processStarted = false;
      this.startDisabled = false;
    });
  }

  initializeWebSocketConnection() {
    const ws = new SockJS(environment.appUrl + '/socket');
    this.stompClient = Stomp.over(ws);
    const that = this;
    this.stompClient.connect({authorization: environment.authorizationHeaderValue}, function (frame) {
      that.stompClient.subscribe('/pushUpdate', (message) => {
        if (message.body) {
          const verificationsCopy = (JSON.parse(JSON.stringify(that.verifications)));
          const result = JSON.parse(message.body);
          verificationsCopy[result.id] = ReportOverviewComponent.convert(result);
          that.verifications = verificationsCopy;
        }
      });
    });
  }

  update() {
    this.initTable();
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
    return status === 'UNEXPECTED_ERROR' || status === 'FILE_ERROR';
  }

  filterVerificationsAll() {
    this.verificationStatusFilter = this.verificationFilter.ALL.value;
    this.setActiveFilter('ALL');
  }

  filterVerificationsOK() {
    this.verificationStatusFilter = this.verificationFilter.OK.value;
    this.setActiveFilter('OK');
  }

  filterVerificationsNOK() {
    this.verificationStatusFilter = this.verificationFilter.NOK.value;
    this.setActiveFilter('NOK');
  }

  filterVerificationsERROR() {
    this.verificationStatusFilter = this.verificationFilter.ERROR.value;
    this.setActiveFilter('ERROR');
  }

  filterVerificationsNA() {
    this.verificationStatusFilter = this.verificationFilter.NA.value;
    this.setActiveFilter('NA');
  }

  statusCounterAll() {
    return this.statusCounterOK() + this.statusCounterNOK() + this.statusCounterNA() + this.statusCounterERROR();
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
    const options = {
      margin: 5,
      filename: 'verifier.pdf',
      pagebreak: {mode: ['avoid-all']},
      image: {type: 'jpeg', quality: 0.98},
      html2canvas: {scale: 4},
      jsPDF: {unit: 'mm', format: 'a4', orientation: 'landscape'}
    };

    const element = document.getElementById('verifications-results');
    html2pdf()
      .set(options)
      .from(element)
      .save();
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
      .reduce((obj, key) => {
        return {
          ...obj,
          [key]: this.verifications[key]
        };
      }, {});
    return Object.keys(filtered).length;
  }

  // Filters
  private setActiveFilter(keyToActivate: string) {
    const keys = Object.keys(this.verificationFilter);
    keys.forEach((key, index) => {
      this.verificationFilter[key].active = key === keyToActivate;
    });
  }

}

