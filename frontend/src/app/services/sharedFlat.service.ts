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
  // Variable to track login status event
  event: boolean = false;


  constructor(private httpClient: HttpClient, private globals: Globals) {
  }
  /**
   * Logs in the user to the shared flat.
   * @param sharedFlat The shared flat details to log in.
   * @param authToken The authentication token.
   * @returns An Observable of the SharedFlat object.
   */
  loginWG(sharedFlat: SharedFlat, authToken: string): Observable<SharedFlat> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${authToken}`
    });
    return this.httpClient.post<SharedFlat>(this.sharedFlatBaseUri, sharedFlat, {headers});
  }
  /**
   * Creates a new shared flat.
   * @param sharedFlat The details of the shared flat to create.
   * @param authToken The authentication token.
   * @returns An Observable of the SharedFlat object.
   */
  createWG(sharedFlat: SharedFlat, authToken: string): Observable<SharedFlat>{
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${authToken}`
    });
    return this.httpClient.post<SharedFlat>(this.createFlatBaseUri, sharedFlat, {headers});
  }

  /**
   * Deletes a user from the shared flat.
   * @param user The details of the user to delete.
   * @returns An Observable of the SharedFlat object.
   */
  delete(user: UserDetail): Observable<SharedFlat> {
    return this.httpClient.delete<SharedFlat>(this.sharedFlatBaseUri + '/' + user.email);
  }

  /**
   * Changes the login event status to true.
   */
  changeEvent() {
    this.event = true;
  }
  /**
   * Checks if the user is logged into the shared flat.
   * @returns A boolean indicating login status.
   */
  isLoggInWg(): boolean {
    return this.event;
  }
  /**
   * Changes the login event status to false.
   */
  changeEventToFalse() {
    this.event = false;
  }
}
