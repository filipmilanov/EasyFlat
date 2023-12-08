import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {ShoppingItemDto} from "../dtos/item";

export class ShoppingListService{
  @Injectable({
    providedIn: 'root'
  })
  private shoppingListBaseUri: string = this.globals.backendUri + '/shopping';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getItemsWithShopId(shopId: string):Observable<ShoppingItemDto[]> {
    return this.httpClient.get<ShoppingItemDto[]>(this.shoppingListBaseUri + "/list/" + shopId);
  }
}
