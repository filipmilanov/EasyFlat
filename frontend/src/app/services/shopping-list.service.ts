import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {ItemDto, ShoppingItemDto, ShoppingItemSearchDto} from "../dtos/item";
import {Observable} from "rxjs";
import {ShoppingListDto} from "../dtos/shoppingList";
import {StorageItem} from "../dtos/storageItem";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class ShoppingListService {

  private baseUri: string = 'http://localhost:8080/api/v1/shopping';

  constructor(private http: HttpClient,
              private authService: AuthService) {
  }

  createItem(item: ShoppingItemDto): Observable<ShoppingItemDto> {
    console.log('Create item with content ' + item);
    return this.http.post<ItemDto>(this.baseUri, item);
  }

  getById(id: string): Observable<ShoppingItemDto> {
    return this.http.get<ShoppingItemDto>(this.baseUri + '/' + id);
  }

  getItemsWithShopId(shopId: string, searchParams: ShoppingItemSearchDto):Observable<ShoppingItemDto[]> {
    let params = new HttpParams();
    if (searchParams.productName) {
      params = params.append('productName', searchParams.productName);
    }
    if (searchParams.label) {
      params = params.append('label', searchParams.label);
    }
    return this.http.get<ShoppingItemDto[]>(this.baseUri + "/list-items/" + shopId, {params});
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
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.http.get<ShoppingListDto[]>(this.baseUri + '/lists', {headers});
  }

  transferToStorage(shoppingItems: ShoppingItemDto[]): Observable<StorageItem[]> {
    console.log(shoppingItems)
    return this.http.post<StorageItem[]>(this.baseUri + '/storage', shoppingItems);
  }

  updateItem(item: ShoppingItemDto) {
    console.log('Update item with ID ' + item.itemId);
    return this.http.put<ShoppingItemDto>(`${this.baseUri}/${item.itemId}`, item);
  }

}
