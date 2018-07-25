import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {ReportOverviewComponent} from "./containers/report-overview/report-overview.component";
import {ProcessorService} from "./services/processor.service";
import {VerifierCommonModule} from "../verifier-common-module";

const routes: Routes = [
  {
    path: 'report',
    children: [
      {path: '', component: ReportOverviewComponent},
    ]
  }
];

@NgModule({
  declarations: [
    ReportOverviewComponent,
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
