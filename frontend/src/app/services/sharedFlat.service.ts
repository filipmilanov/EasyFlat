import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {SharedFlat} from "../dtos/sharedFlat";
import {UserDetail} from "../dtos/auth-request";

@Injectable({
  providedIn: 'root'
})
export class SharedFlatService {
  private sharedFlatBaseUri: string = this.globals.backendUri + '/wgLogin';
  private createFlatBaseUri: string = this.globals.backendUri + '/wgCreate';

  event: boolean = false;


  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  loginWG(sharedFlat: SharedFlat, authToken: string): Observable<SharedFlat> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${authToken}`
    });
    return this.httpClient.post<SharedFlat>(this.sharedFlatBaseUri, sharedFlat, {headers});
  }

  createWG(sharedFlat: SharedFlat, authToken: string): Observable<SharedFlat>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${authToken}`
    });
    return this.httpClient.post<SharedFlat>(this.createFlatBaseUri, sharedFlat, {headers});
  }


  delete(user: UserDetail): Observable<SharedFlat> {
    return this.httpClient.delete<SharedFlat>(this.sharedFlatBaseUri + '/' + user.email);
  }


  changeEvent() {
    this.event = true;
  }

  isLoggInWg(): boolean {
    return this.event;
  }
}
