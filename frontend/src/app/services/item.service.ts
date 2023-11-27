import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ItemDto} from "../dtos/item";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

const baseUri = environment.backendUrl + '/item';

@Injectable({
  providedIn: 'root'
})
export class ItemService {

  constructor(
    private http: HttpClient,
  ) { }

  getById(id: number): Observable<ItemDto> {
    return this.http.get<ItemDto>(`${baseUri}/${id}`);
  }
}
