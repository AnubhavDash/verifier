import {NgModule} from "@angular/core";
import {ProcessorService} from "./report/services/processor.service";
import {CommonModule} from "@angular/common";
import {BrowserModule} from "@angular/platform-browser";

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    BrowserModule
  ],
  exports: [
    CommonModule,
    BrowserModule
  ],
  providers: [ProcessorService]
})
export class VerifierCommonModule {

}
