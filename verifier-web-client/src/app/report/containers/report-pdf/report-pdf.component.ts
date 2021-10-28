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
import {Component, OnInit} from '@angular/core';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {environment} from '../../../../environments/environment';

@Component({
  templateUrl: 'report-pdf.component.html',
  styleUrls: ['report-pdf.component.css']
})
export class ReportPdfComponent implements OnInit {

  public innerHtml: SafeHtml;

  constructor(private _sanitizer: DomSanitizer) {
  }

  ngOnInit() {
    this.setInnerHtml(environment.appUrl + '/api/verifications/verifier-report.pdf');
  }

  public setInnerHtml(pdfurl: string) {
    this.innerHtml = this._sanitizer.bypassSecurityTrustHtml(
      '<object data="' + pdfurl + '" type="text/html" style="width: 100%">' +
      'Embedded Object failed : ' +
      '<a href="' + pdfurl + '" target="_blank">link</a></object>');
  }
}
