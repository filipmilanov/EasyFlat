import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {Message} from "../dtos/message";
import {StorageItemList} from "../dtos/storageItemList";

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  private storageBaseUri: string = 'http://localhost:8080/api/v1/storage';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  getItems(id:string): Observable<StorageItemList[]> {
    return this.httpClient.get<StorageItemList[]>(this.storageBaseUri + '/' + id);
  }

}
