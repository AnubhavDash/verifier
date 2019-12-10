///
/// This file is part of Verifier Swiss Post.
///
/// Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
/// the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
/// or (at your option) any later version.
///
/// Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
/// the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
/// See the GNU General Public License for more details.
///
/// You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
/// If not, see <https://www.gnu.org/licenses/>.
///

import {Component, OnInit} from '@angular/core';
import {ProcessorService} from '../../services/processor.service';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {VerificationDefinition} from '../../models/VerificationDefinition.interface';
import {Configuration} from '../../models/Configuration.interface';
import {environment} from '../../../../environments/environment';

@Component({
  templateUrl: 'report-overview.component.html',
  styleUrls: ['report-overview.component.css'],
  providers: []
})
export class ReportOverviewComponent implements OnInit {

  inputDirectory: string;
  private stompClient;
  verifications = {};
  buttonDisabled = false;

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
      console.log(value);
      this.inputDirectory = value.inputDirectory;
    });
  }

  initTable() {
    this.processorService.getVerificationStatus().subscribe(value => {
        for (let i = 0; i < value.length; i++) {
          console.log(JSON.stringify(value[i]));
          this.verifications[value[i].id] = ReportOverviewComponent.convert(value[i]);
        }
      }
    );
  }

  startProcess(runOptions?: string): void {
    const configuration = new Configuration();
    configuration.inputDirectory = this.inputDirectory;
    this.processorService.setConfigurationInputDirectory(configuration).subscribe(() => {
      this.buttonDisabled = true;
      this.processorService.processVerifications(runOptions).subscribe();
    });
  }

  resetProcess(): void {
    this.processorService.resetVerifications().subscribe(value => {
      this.initTable();
      this.buttonDisabled = false;
    });
  }

  initializeWebSocketConnection() {
    const ws = new SockJS(environment.appUrl + '/socket');
    this.stompClient = Stomp.over(ws);
    const that = this;
    this.stompClient.connect({authorization: environment.authorizationHeaderValue}, function (frame) {
      that.stompClient.subscribe('/pushUpdate', (message) => {
        if (message.body) {
          const result = JSON.parse(message.body);
          that.verifications[result.id] = ReportOverviewComponent.convert(result);
        }
      });
    });
  }

  update() {
    this.initTable();
  }

}

