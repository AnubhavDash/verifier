import {Component, OnInit} from "@angular/core";
import {ProcessorService} from "../../services/processor.service";
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {TestDefinition} from "../../models/TestDefinition.interface";
import {Configuration} from "../../models/Configuration.interface";
import {environment} from "../../../../environments/environment";

@Component({
  templateUrl: 'report-overview.component.html',
  styleUrls: ['report-overview.component.css'],
  providers: []
})
export class ReportOverviewComponent implements OnInit {

  inputDirectory: string;
  private stompClient;
  tests = {};
  keys: string[];
  buttonDisabled = false;

  constructor(private processorService: ProcessorService) {
  }

  ngOnInit(): void {
    //console.log(navigator.language.toUpperCase());
    this.initTable();
    this.initializeWebSocketConnection();
    this.processorService.getConfigurationInputDirectory().subscribe(value => {
      console.log(value);
      this.inputDirectory = value.inputDirectory;
    });
  }

  initTable() {
    this.processorService.getTestStatus().subscribe(value => {
        for (let i = 0; i < value.length; i++) {
          console.log(JSON.stringify(value[i]));
          this.tests[value[i].id] = ReportOverviewComponent.convert(value[i]);
        }
        this.keys = Object.keys(this.tests);
      }
    );
  }

  startProcess(): void {
    const configuration = new Configuration();
    configuration.inputDirectory = this.inputDirectory;
    this.processorService.setConfigurationInputDirectory(configuration).subscribe(() => {
      this.buttonDisabled = true;
      this.processorService.processTests().subscribe();
    });
  }

  resetProcess(): void {
    this.processorService.resetTests().subscribe(value => {
      this.initTable();
      this.buttonDisabled = false;
    });
  }

  static convert(input: any): TestDefinition {
    let result = new TestDefinition();
    result.id = input.id;
    result.testId = input.testId;
    result.blockId = input.blockId;
    result.name = input.name;
    result.category = input.category;
    result.status = input.status;
    result.description = input.description ? input.description[navigator.language.toUpperCase().substr(0, 2)] : null;
    result.message = input.message ? input.message[navigator.language.toUpperCase().substr(0, 2)] : null;
    if (input.status === "OK") {
      result.color = "green";
    } else if (input.status === "NOK") {
      result.color = "red";
    } else if (input.status === "NA") {
      result.color = "grey";
    }
    return result;
  }

  initializeWebSocketConnection() {
    let ws = new SockJS("https://localhost:8443/socket");
    this.stompClient = Stomp.over(ws);
    let that = this;
    this.stompClient.connect({authorization: environment.authorizationHeaderValue}, function (frame) {
      that.stompClient.subscribe("/pushUpdate", (message) => {
        if (message.body) {
          let result = JSON.parse(message.body);
          that.tests[result.id] = ReportOverviewComponent.convert(result);
        }
      })
    });
  }

  update() {
    this.initTable();
  }
}
