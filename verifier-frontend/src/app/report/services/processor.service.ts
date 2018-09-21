import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {environment} from "../../../environments/environment";

@Injectable()
export class ProcessorService {

  httpOptions = {
    headers: new HttpHeaders({
      'Authorization': 'Basic dmVyaWZpZXItY2xpZW50OlRPX0JFX0RFRklORUQ='
    })
  };

  constructor(private http: HttpClient) {

  }

  getTestStatus(): Observable<any> {
    return this.http.get<any>(environment.appUrl + '/api/tests', this.httpOptions);
  }

  processTests(): Observable<Object> {
    return this.http.post(environment.appUrl + '/api/tests', null, this.httpOptions);
  }

  resetTests() : Observable<Object> {
    return this.http.post(environment.appUrl + '/api/reset', null, this.httpOptions);
  }

  generatePDF(): Observable<Object> {
    return this.http.post(environment.appUrl + '/api/pdf', null, this.httpOptions);
  }


}
