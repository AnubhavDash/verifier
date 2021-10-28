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
import {ReportOverviewComponent} from './containers/report-overview/report-overview.component';
import {ProcessorService} from './services/processor.service';
import {VerifierCommonModule} from '../verifier-common-module';
import {ReportPdfComponent} from './containers/report-pdf/report-pdf.component';
import {StatusFilterPipe} from './pipes/statusFilter.pipe';
import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule} from '@angular/forms';
import {LocalizePipe} from './pipes/localize.pipe';

const routes: Routes = [
  {
    path: 'report',
    children: [
      {path: '', component: ReportOverviewComponent},
      {path: 'pdf', component: ReportPdfComponent},
    ]
  }
];

@NgModule({
  declarations: [
    StatusFilterPipe,
    LocalizePipe,
    ReportOverviewComponent,
    ReportPdfComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    RouterModule.forChild(routes),
    VerifierCommonModule
  ],
  exports: [],
  providers: [ProcessorService]
})

export class ReportModule {
}
