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

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ReportOverviewComponent} from './containers/report-overview/report-overview.component';
import {ProcessorService} from './services/processor.service';
import {VerifierCommonModule} from '../verifier-common-module';
import {ReportPdfComponent} from './containers/report-pdf/report-pdf.component';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule} from '@angular/forms';

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
