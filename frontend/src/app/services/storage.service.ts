import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {Message} from "../dtos/message";

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor(private httpClient: HttpClient, private globals: Globals) { }


  getMessage(): Observable<> {
    return this.httpClient.get<Message[]>(this.messageBaseUri);
  }

}
