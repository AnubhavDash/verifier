import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {environment} from "../../../environments/environment";

@Injectable()
export class ProcessorService {

  constructor(private http: HttpClient) {

  }

  getTestStatus(): Observable<any> {
    return this.http.get<any>(environment.appUrl + '/api/tests');
  }

  processTests(): Observable<Object> {
    return this.http.post(environment.appUrl + '/api/tests', null);
  }


}
