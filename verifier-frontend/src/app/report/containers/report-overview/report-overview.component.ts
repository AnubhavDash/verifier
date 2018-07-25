import {Component, OnInit} from "@angular/core";
import {ProcessorService} from "../../services/processor.service";
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import {TestDefinition} from "../../models/TestDefinition.interface";

@Component({
  templateUrl: 'report-overview.component.html',
  styleUrls: ['report-overview.component.css'],
  providers: []
})
export class ReportOverviewComponent implements OnInit {

  private stompClient;
  tests = {};
  keys: string[];
  buttonDisabled = false;

  constructor(private processorService: ProcessorService) {
  }

  ngOnInit(): void {
    this.processorService.getTestStatus().subscribe(value => {
        for (let i = 0; i < value.length; i++) {
          console.log(JSON.stringify(value[i]));
          this.tests[value[i].id] = ReportOverviewComponent.convert(value[i]);
        }
        this.keys = Object.keys(this.tests);
      }
    );
    this.initializeWebSocketConnection();
  }

  startProcess(): void {
    this.buttonDisabled = true;
    this.processorService.processTests().subscribe();
  }

  static convert(input: any): TestDefinition {
    let result = new TestDefinition();
    result.id = input.id;
    result.testId = input.testId;
    result.blockId = input.blockId;
    result.name = input.name;
    result.category = input.category;
    result.status = input.status;
    result.description = input.description ? input.description[navigator.language.toUpperCase()] : null;
    result.message = input.message ? input.message[navigator.language.toUpperCase()] : null;
    if (input.status === "OK") {
      result.color = "green";
    } else if (input.status === "NOK") {
      result.color = "red";
    }
    return result;
  }

  initializeWebSocketConnection() {
    let ws = new SockJS("http://localhost:8080/socket");
    this.stompClient = Stomp.over(ws);
    let that = this;
    this.stompClient.connect({}, function (frame) {
      that.stompClient.subscribe("/pushUpdate", (message) => {
        if (message.body) {
          let result = JSON.parse(message.body);
          that.tests[result.id] = ReportOverviewComponent.convert(result);
        }
      })
    });
  }
}
