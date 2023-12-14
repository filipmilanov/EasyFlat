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

    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.http.post<ItemDto>(this.baseUri, item, {headers});
  }

  /**
   * Find an existing shopping item in the system
   *
   * @param id the id of the shopping item that should already be stored in the system
   * @return an Observable for the existing shopping list in the system
   */
  getById(id: string): Observable<ShoppingItemDto> {
    console.log('Get item with ID ' + id);
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.http.get<ShoppingItemDto>(this.baseUri + '/' + id, {headers});
  }

  /**
   * Find existing shopping items in the system
   *
   * @param shopName the id of the shopping list to which the shopping items are connected in the system
   * @param searchParams search parameters consisting of the products' name and their labels' value
   * @return an Observable for the existing shopping items in the system
   */
  getItemsWithShopName(shopName: string, searchParams: ShoppingItemSearchDto):Observable<ShoppingItemDto[]> {
    console.log('Get items with shopId ' + shopName + ' and search parameters ' + searchParams);
    let params = new HttpParams();
    if (searchParams.productName) {
      params = params.append('productName', searchParams.productName);
    }
    if (searchParams.label) {
      params = params.append('label', searchParams.label);
    }
    return this.http.get<ShoppingItemDto[]>(this.baseUri + "/list-items/" + shopName, {params});
  }

  /**
   * Find an existing shopping list in the system
   *
   * @param shoppingListName the name of the list that should already be stored in the system
   * @return an Observable for the existing shopping list in the system
   */
  getShoppingListByName(shoppingListName: string): Observable<ShoppingListDto> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.http.get<ShoppingListDto>(this.baseUri + '/list/' + shoppingListName, {headers});
  }

  getShoppingListById(shoppingListId: string): Observable<ShoppingListDto> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.http.get<ShoppingListDto>(this.baseUri + '/listId/' + shoppingListId, {headers});
  }

  createList(listName: string): Observable<ShoppingListDto> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.http.post<ShoppingListDto>(this.baseUri + "/list-create", listName, {headers});
  }

  deleteItem(itemId: number): Observable<ShoppingItemDto> {
    console.log('Delete item with ID ');
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.http.delete<ShoppingItemDto>(this.baseUri + '/' + itemId, {headers});
  }

  deleteList(shopId: string): Observable<ShoppingListDto> {
    console.log('Delete list with ID ');
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.http.delete<ShoppingListDto>(this.baseUri + '/delete/' + shopId, {headers});
  }

  getShoppingLists(): Observable<ShoppingListDto[]> {
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.http.get<ShoppingListDto[]>(this.baseUri + '/lists', {headers});
  }

  transferToStorage(shoppingItems: ShoppingItemDto[]): Observable<StorageItem[]> {
    console.log('Add items to storage ' + shoppingItems)
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.http.post<StorageItem[]>(this.baseUri + '/storage', shoppingItems, {headers});
  }

  /**
   * Update an item in the system.
   *
   * @param item the data for the item that should be updated
   * @return an Observable for the updated item
   */
  updateItem(item: ShoppingItemDto): Observable<ShoppingItemDto> {
    console.log('Update item with ID ' + item.itemId);
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.http.put<ShoppingItemDto>(`${this.baseUri}/${item.itemId}`, item, {headers});
  }

}
