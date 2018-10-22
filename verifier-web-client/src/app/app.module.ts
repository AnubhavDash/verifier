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
