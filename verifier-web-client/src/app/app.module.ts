///
/// This file is part of Verifier Swiss Post.
/// Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
/// Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
/// You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
///

import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';


import {AppComponent} from './app.component';
import {ReportModule} from "./report/report.module";
import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./home.component";
import {HttpClientModule} from "@angular/common/http";
import {CommonModule} from "@angular/common";
import {VerifierCommonModule} from "./verifier-common-module";

const routes: Routes = [
  {path: '**', component: HomeComponent}
];

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent
  ],
  imports: [
    RouterModule.forRoot(routes),
    BrowserModule,
    ReportModule,
    HttpClientModule,
    CommonModule,
    VerifierCommonModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
