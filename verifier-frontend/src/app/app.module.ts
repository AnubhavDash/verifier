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
import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {DatasetModule} from './dataset/dataset.module';
import {RouterModule, Routes} from '@angular/router';
import {HttpClient, provideHttpClient, withInterceptorsFromDi} from '@angular/common/http';
import {CommonModule} from '@angular/common';
import {VerifierCommonModule} from './verifier-common-module';
import {DatasetComponent} from "./dataset/dataset.component";
import {InternalHeaderComponent, TranslateTextPipe} from "e-voting-libraries-ui-kit";
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from "@ngx-translate/http-loader";
import {VerifyComponent} from "./verify/verify.component";
import {VerifyModule} from "./verify/verify.module";
import {ReportComponent} from "./report/report.component";
import {ReportModule} from "./report/report.module";
import {NgbDropdownModule} from "@ng-bootstrap/ng-bootstrap";
import {VerifyGuard} from "./verify/verify-guard.service";
import {ReportGuard} from "./report/report-guard.service";
import {ResultComponent} from "./result/result.component";
import {ResultGuard} from "./result/result-guard.service";
import {ResultModule} from "./result/result.module";
import {StepperItemComponent} from "./shared/components/stepper-item/stepper-item.component";

const routes: Routes = [
  {path: 'load-dataset', component: DatasetComponent},
  {path: 'verify', component: VerifyComponent, canActivate: [VerifyGuard]},
  {path: 'export-report', component: ReportComponent, canActivate: [ReportGuard]},
  {path: 'export-result', component: ResultComponent, canActivate: [ResultGuard]},
  {path: '**', redirectTo: 'load-dataset'},
];

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    RouterModule.forRoot(routes),
    BrowserModule,
    DatasetModule,
    VerifyModule,
    ReportModule,
    ResultModule,
    NgbDropdownModule,
    CommonModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    VerifierCommonModule,
    InternalHeaderComponent,
    StepperItemComponent,
  ],
  providers: [provideHttpClient(withInterceptorsFromDi()), TranslateTextPipe],
  bootstrap: [AppComponent]
})
export class AppModule {
}

export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}
