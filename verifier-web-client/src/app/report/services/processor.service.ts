import {Injectable} from "@angular/core";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {environment} from "../../../environments/environment";
import {Configuration} from "../models/Configuration.interface";

@Injectable()
export class ProcessorService {

  httpOptions = {
    headers: new HttpHeaders({
      'Authorization': environment.authorizationHeaderValue
    })
  };

  constructor(private http: HttpClient) {

  }

  getTestStatus(): Observable<any> {
    return this.http.get<any>(environment.appUrl + '/api/tests', this.httpOptions);
  }

  processTests(runOptions?: string): Observable<Object> {
    var runParams = "";
    if ( runOptions != undefined )
    {
      runParams = `?runOptions=${runOptions}`;
    }
    return this.http.post(environment.appUrl + '/api/tests'+ runParams, null, this.httpOptions);
  }

  resetTests(): Observable<Object> {
    return this.http.post(environment.appUrl + '/api/reset', null, this.httpOptions);
  }

  getConfigurationInputDirectory(): Observable<Configuration> {
    return this.http.get<Configuration>(environment.appUrl + '/api/configurationInputDirectory', this.httpOptions);
  }

  setConfigurationInputDirectory(value: Configuration): Observable<Object> {
    return this.http.post(environment.appUrl + '/api/configurationInputDirectory', value, this.httpOptions);
  }
}
