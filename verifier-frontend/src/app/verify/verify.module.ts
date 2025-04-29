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
import {VerifyComponent} from './verify.component';
import {VerifyService} from './verify.service';
import {VerifierCommonModule} from '../verifier-common-module';
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {FormsModule} from '@angular/forms';
import {PageTitleComponent} from "../shared/components/page-title/page-title.component";
import {TranslatePipe} from "@ngx-translate/core";
import {DatasetInformationComponent} from "../shared/components/dataset-information/dataset-information.component";
import {VerificationDetailsComponent} from "../shared/components/verification-details/verification-details.component";

@NgModule({
  declarations: [
    VerifyComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    VerifierCommonModule,
    PageTitleComponent,
    TranslatePipe,
    DatasetInformationComponent,
    VerificationDetailsComponent
  ],
  exports: [],
  providers: [VerifyService]
})

export class VerifyModule {
}
