import {Component, OnInit} from '@angular/core';
import {DomSanitizer, SafeHtml} from "@angular/platform-browser";
import {environment} from "../../../../environments/environment";

@Component({
  templateUrl: 'report-pdf.component.html',
  styleUrls: ['report-pdf.component.css']
})
export class ReportPdfComponent implements OnInit {

  constructor(private _sanitizer: DomSanitizer) {
  }

  ngOnInit() {
    this.setInnerHtml(environment.appUrl + "/api/tests/verifier-report.pdf");
  }

  public innerHtml: SafeHtml;

  public setInnerHtml(pdfurl: string) {
    this.innerHtml = this._sanitizer.bypassSecurityTrustHtml(
      "<object data='" + pdfurl + "' type='text/html' style='width: 100%'>" +
      "Embedded Object failed : " +
      "<a href='" + pdfurl + "' target='_blank'>link</a></object>");
  }
}
