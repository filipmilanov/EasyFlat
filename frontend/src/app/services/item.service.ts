import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ItemDto} from "../dtos/item";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {Message} from "../dtos/message";


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
    console.log('Create message with title ' + item.itemId);
    return this.http.post<ItemDto>(this.baseUri, item);
  }
}
