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
import {Component, inject, OnInit} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";
import packageInfo from "../../package.json";
import {SessionStorageService} from "./shared/services/session-storage.service";
import {combineLatest} from "rxjs";
import {VerifierMode} from "./shared/types/verifier-mode.enum";


@Component({
  selector: 'ver-root',
  templateUrl: './app.component.html',
  styleUrls: [],
  standalone: false
})
export class AppComponent implements OnInit {
  packageDescription: string = packageInfo.description;
  packageVersion: string = packageInfo.version;
  availableLanguages: string [];
  isDatasetLoaded: boolean = false;
  isVerificationComplete: boolean = false;
  isVerificationReportGenerated: boolean = false;
  isElectionResultReportGenerated: boolean = false;
  verifierMode: VerifierMode;

  readonly translate: TranslateService = inject(TranslateService);
  private readonly sessionStorageService = inject(SessionStorageService);

  constructor() {
    this.translate.addLangs(['en', 'de', 'fr', 'it']);
    this.translate.setDefaultLang('en');
    this.translate.use('en');
    this.availableLanguages = this.translate.getLangs();
  }

  setLanguage(lang: string) {
    this.translate.use(lang);
  }

  ngOnInit(): void {
    combineLatest([
      this.sessionStorageService.datasetLoaded$,
      this.sessionStorageService.verificationCompleted$,
      this.sessionStorageService.verificationReportGenerated$,
      this.sessionStorageService.electionResultReportGenerated$
    ]).subscribe(([datasetLoadedStatus, verificationCompletedStatus, verificationReportGeneratedStatus, electionResultReportGeneratedStatus]) => {
      this.isDatasetLoaded = datasetLoadedStatus;
      this.verifierMode = this.sessionStorageService.getVerifierMode();
      this.isVerificationComplete = verificationCompletedStatus;
      this.isVerificationReportGenerated = verificationReportGeneratedStatus;
      this.isElectionResultReportGenerated = electionResultReportGeneratedStatus;
    });
  }

  isTallyMode() {
    return this.verifierMode === VerifierMode.TALLY;
  }

}
