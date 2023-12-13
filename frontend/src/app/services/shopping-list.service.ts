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

  /**
   * Creates a shopping item in the system
   *
   * @param item the data without ID for the shopping item that should be stored in the system
   * @return an Observable for the stored shopping list in the system
   */
  createItem(item: ShoppingItemDto): Observable<ShoppingItemDto> {
    console.log('Create item with content ' + item);
    return this.http.post<ItemDto>(this.baseUri, item);
  }

  /**
   * Find an existing shopping item in the system
   *
   * @param id the id of the shopping item that should already be stored in the system
   * @return an Observable for the existing shopping list in the system
   */
  getById(id: string): Observable<ShoppingItemDto> {
    console.log('Get item with ID ' + id);
    return this.http.get<ShoppingItemDto>(this.baseUri + '/' + id);
  }

  /**
   * Find existing shopping items in the system
   *
   * @param shopId the id of the shopping list to which the shopping items are connected in the system
   * @param searchParams search parameters consisting of the products' name and their labels' value
   * @return an Observable for the existing shopping items in the system
   */
  getItemsWithShopId(shopId: string, searchParams: ShoppingItemSearchDto):Observable<ShoppingItemDto[]> {
    console.log('Get items with shopId ' + shopId + ' and search parameters ' + searchParams);
    let params = new HttpParams();
    if (searchParams.productName) {
      params = params.append('productName', searchParams.productName);
    }
    if (searchParams.label) {
      params = params.append('label', searchParams.label);
    }
    return this.http.get<ShoppingItemDto[]>(this.baseUri + "/list-items/" + shopId, {params});
  }

  /**
   * Find an existing shopping list in the system
   *
   * @param shoppingListId the id of the list that should already be stored in the system
   * @return an Observable for the existing shopping list in the system
   */
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
    console.log('Add items to storage ' + shoppingItems)
    return this.http.post<StorageItem[]>(this.baseUri + '/storage', shoppingItems);
  }

  /**
   * Update an item in the system.
   *
   * @param item the data for the item that should be updated
   * @return an Observable for the updated item
   */
  updateItem(item: ShoppingItemDto): Observable<ShoppingItemDto> {
    console.log('Update item with ID ' + item.itemId);
    return this.http.put<ShoppingItemDto>(`${this.baseUri}/${item.itemId}`, item);
  }

}
