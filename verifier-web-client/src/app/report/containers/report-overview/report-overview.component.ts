/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
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
    result.description = input.description ? input.description[navigator.language.toUpperCase().substr(0, 2)] : null;
    result.message = input.message ? input.message[navigator.language.toUpperCase().substr(0, 2)] : null;
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
    this.processorService.getVerificationStatus().subscribe(value => {
      this.verificationsSize = value.length;
      const verifications = {};
      for (let i = 0; i < value.length; i++) {
        verifications[value[i].id] = ReportOverviewComponent.convert(value[i]);
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

  isOK (status) {
    return status === 'OK';
  }

  isNotOK (status) {
    return status === 'NOK';
  }

  isNA (status) {
    return status === 'NA';
  }

  isError (status) {
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
      margin:       5,
      filename:     'verifier.pdf',
      pagebreak: { mode: ['avoid-all'] },
      image:        { type: 'jpeg', quality: 0.98 },
      html2canvas:  { scale: 4 },
      jsPDF:        { unit: 'mm', format: 'a4', orientation: 'landscape' }
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

