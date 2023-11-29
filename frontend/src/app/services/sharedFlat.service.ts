import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {SharedFlat} from "../dtos/sharedFlat";

@Injectable({
  providedIn: 'root'
})
export class SharedFlatService {
  private sharedFlatBaseUri: string = this.globals.backendUri + '/wgLogin';
  private createFlatBaseUri: string = this.globals.backendUri + '/wgCreate';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  loginWG(sharedFlat: SharedFlat): Observable<SharedFlat> {
    return this.httpClient.post<SharedFlat>(this.sharedFlatBaseUri, sharedFlat);
  }

  createWG(sharedFlat: SharedFlat): Observable<SharedFlat>{
    return this.httpClient.post<SharedFlat>(this.createFlatBaseUri, sharedFlat);
  }

  // deleteSharedFlatByName(name: string): Observable<SharedFlat> {
  //   return this.httpClient.delete(`${this.backendUrl}/entities/${name}`);
  // }
}
