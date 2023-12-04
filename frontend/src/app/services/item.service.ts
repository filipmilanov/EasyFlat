import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ItemDto} from "../dtos/item";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ItemService {

  baseUri = environment.backendUrl + '/item';

  constructor(
    private http: HttpClient,
  ) {
  }

  getById(id: number): Observable<ItemDto> {
    return this.http.get<ItemDto>(`${this.baseUri}/${id}`);
  }


  /**
   * Persists Items to the backend
   *
   * @param item to persist
   */
  createItem(item: ItemDto): Observable<ItemDto> {
    console.log('Create item with content ' + item);
    return this.http.post<ItemDto>(this.baseUri, item);
  }

  /**
   * Update an item in the system.
   *
   * @param item the data for the item that should be updated
   * @return an Observable for the updated item
   */
  updateItem(item: ItemDto): Observable<ItemDto> {
    console.log('Update item with ID ' + item.itemId);
    return this.http.put<ItemDto>(`${this.baseUri}/${item.itemId}`, item);
  }

  /**
   * Delete an item from the system.
   *
   * @param itemId the id of the item that should be deleted
   * @return an Observable for the deleted item
   */
  deleteItem(itemId: number): Observable<ItemDto> {
    console.log('Delete item with ID ' + itemId);
    return this.http.delete<ItemDto>(this.baseUri + '/' + itemId);
  }

}
