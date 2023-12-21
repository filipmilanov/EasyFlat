import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
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

  findByGeneralName(generalName: string): Observable<ItemDto[]> {
    let params = new HttpParams();
    params = params.append('generalName', generalName);
    return this.http.get<ItemDto[]>(`${this.baseUri}/search`, {params});
  }

  findByBrand(barnd: string): Observable<ItemDto[]> {
    let params = new HttpParams();
    params = params.append('brand', barnd);
    return this.http.get<ItemDto[]>(`${this.baseUri}/search`, {params});
  }

  findByBoughtAt(boughtAt: string): Observable<ItemDto[]> {
    let params = new HttpParams();
    params = params.append('boughtAt', boughtAt);
    return this.http.get<ItemDto[]>(`${this.baseUri}/search`, {params});
  }

  findByDigitalStorageAndGeneralName(generalName: string): Observable<ItemDto[]> {
    console.log('Find items with general name ' + generalName)
    return this.http.get<ItemDto[]>(this.baseUri + '/general-name/' + generalName);
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
