import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ItemDto, ShoppingItemDto} from "../dtos/item";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ShoppingListService {

  private baseUri: string = 'http://localhost:8080/api/v1/shopping';

  constructor(
    private http: HttpClient
  ) {
  }

  createItem(item: ShoppingItemDto): Observable<ShoppingItemDto> {
    console.log('Create item with content ' + item);
    return this.http.post<ItemDto>(this.baseUri, item);
  }

  getById(id: string): Observable<ShoppingItemDto> {
    return this.http.get<ShoppingItemDto>(this.baseUri + '/' + id);
  }
}
