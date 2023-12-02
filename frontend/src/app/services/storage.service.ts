import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {ItemSearchDto, StorageItemList, StorageItemListDto} from "../dtos/storageItemList";
import {DigitalStorageDto} from "../dtos/digitalStorageDto";
import {ItemDto} from "../dtos/item";

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  private storageBaseUri: string = 'http://localhost:8080/api/v1/storage';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getItems(id: string, searchParameters: ItemSearchDto): Observable<StorageItemListDto[]> {
    let params = new HttpParams();
    if (searchParameters.productName) {
      params = params.append('productName', searchParameters.productName);
    }
    if (searchParameters.brand) {
      params = params.append('brand', searchParameters.brand);
    }
    if (searchParameters.expireDateStart) {
      params = params.append('expireDateStart', searchParameters.expireDateStart);
    }
    if (searchParameters.expireDateEnd) {
      params = params.append('expireDateEnd', searchParameters.expireDateEnd);
    }
    if (searchParameters.fillLevel) {
      params = params.append('fillLevel', searchParameters.fillLevel);
    }
    if (searchParameters.alwaysInStock != null) {
      console.log(searchParameters)
      params = params.append('alwaysInStock', searchParameters.alwaysInStock);
    }
    params = params.append('orderType', searchParameters.orderBy);
    return this.httpClient.get<StorageItemListDto[]>(this.storageBaseUri + '/' + id, {params});
  }

  updateItemQuantity(storageId: string, value: string, item: ItemDto) {
    return this.httpClient.patch<ItemDto>(this.storageBaseUri + '/' + storageId + '/' + item.itemId, item.quantityCurrent)
  }

  findAll(titleSearch: string, limit: number): Observable<DigitalStorageDto[]> {
    console.log(titleSearch);
    let params = new HttpParams();
    params = params.append('title', titleSearch);
    params = params.append('limit', limit);
    return this.httpClient.get<DigitalStorageDto[]>(
      this.storageBaseUri,
      {params}
    );
  }

  getItemsWithGenaralName(generalName:string, storId:string): Observable<StorageItemListDto[]> {
    let params = new HttpParams();


    if (storId) {
      params = params.append('storId', storId);
    }


    return this.httpClient.get<StorageItemListDto[]>(this.storageBaseUri +  '/info/' +  generalName , {params});
  }
}
