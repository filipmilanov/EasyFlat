import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ItemDto, ShoppingItemDto} from "../dtos/item";
import {Observable} from "rxjs";
import {ShoppingListDto} from "../dtos/shoppingList";

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

  getItemsWithShopId(shopId: string):Observable<ShoppingItemDto[]> {
    return this.http.get<ShoppingItemDto[]>(this.baseUri + "/list-items/" + shopId);
  }

  getShoppingListById(shoppingListId: string): Observable<ShoppingListDto> {
    return this.http.get<ShoppingListDto>(this.baseUri + '/list/' + shoppingListId);
  }

  createList(listName: string): Observable<ShoppingListDto> {
    return this.http.post<ShoppingListDto>(this.baseUri + "/list-create", listName);
  }

  deleteItem(itemId: number): Observable<ShoppingItemDto> {
    console.log('Delete item with ID ');
    return this.http.delete<ShoppingItemDto>(this.baseUri + '/' + itemId);
  }

  deleteList(shopId: string): Observable<ShoppingListDto> {
    console.log('Delete list with ID ');
    return this.http.delete<ShoppingListDto>(this.baseUri + '/delete/' + shopId);
  }

  getShoppingLists(): Observable<ShoppingListDto[]> {
    return this.http.get<ShoppingListDto[]>(this.baseUri + '/lists');
  }
}
