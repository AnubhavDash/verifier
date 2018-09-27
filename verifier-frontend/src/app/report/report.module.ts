import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {ReportOverviewComponent} from "./containers/report-overview/report-overview.component";
import {ProcessorService} from "./services/processor.service";
import {VerifierCommonModule} from "../verifier-common-module";
import {ReportPdfComponent} from './containers/report-pdf/report-pdf.component';

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
    RouterModule.forChild(routes),
    VerifierCommonModule
  ],
  exports: [],
  providers: [ProcessorService]
})
export class ReportModule {
}
