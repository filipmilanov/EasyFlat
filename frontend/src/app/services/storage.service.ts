import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {ItemSearchDto, StorageItem, StorageItemListDto} from "../dtos/storageItem";
import {DigitalStorageDto} from "../dtos/digitalStorageDto";
import {ItemDto, ShoppingItemDto} from "../dtos/item";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  private storageBaseUri: string = 'http://localhost:8080/api/v1/storage';

  constructor(private httpClient: HttpClient,
              private authService: AuthService) {
  }

  getItems( searchParameters: ItemSearchDto): Observable<StorageItemListDto[]> {
    let params = new HttpParams();
    if (searchParameters.productName) {
      params = params.append('productName', searchParameters.productName);
    }
    if (searchParameters.fillLevel) {
      params = params.append('fillLevel', searchParameters.fillLevel);
    }
    if (searchParameters.alwaysInStock != null) {
      console.log(searchParameters)
      params = params.append('alwaysInStock', searchParameters.alwaysInStock);
    }
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    params = params.append('orderType', searchParameters.orderBy);
    return this.httpClient.get<StorageItemListDto[]>(this.storageBaseUri + '/items' , {params,headers});
  }

  findAll(titleSearch: string, limit: number): Observable<DigitalStorageDto[]> {
    console.log(titleSearch);
    let params = new HttpParams();
    params = params.append('title', titleSearch);
    params = params.append('limit', limit);
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.httpClient.get<DigitalStorageDto[]>(
      this.storageBaseUri,
      {params, headers}
    );
  }

  getItemsWithGenaralName(generalName:string): Observable<StorageItem[]> {
    console.log('Get all items with general name ' + generalName);
    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });

    return this.httpClient.get<StorageItem[]>(this.storageBaseUri +  '/info/' +  generalName,{headers});
  }

  /**
   * Save a shopping item in the system.
   *
   * @param item the data for the shopping item that should be saved
   * @return an Observable for the saved shopping item
   */
  addItemToShoppingList(item: ItemDto): Observable<ShoppingItemDto> {
    console.log('Add item ' + item + ' to shopping list')

    const headers = new HttpHeaders({
      'Authorization': this.authService.getToken()
    });
    return this.httpClient.post<ShoppingItemDto>(this.storageBaseUri + '/shop', item, {headers});
  }

}
