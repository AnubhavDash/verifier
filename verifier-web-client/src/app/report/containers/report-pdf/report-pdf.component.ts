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
    this.setInnerHtml(environment.appUrl + '/api/tests/verifier-report.pdf');
  }

  public setInnerHtml(pdfurl: string) {
    this.innerHtml = this._sanitizer.bypassSecurityTrustHtml(
      '<object data="' + pdfurl + '" type="text/html" style="width: 100%">' +
      'Embedded Object failed : ' +
      '<a href="' + pdfurl + '" target="_blank">link</a></object>');
  }
}
