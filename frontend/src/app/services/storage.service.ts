import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {ItemSearchDto, StorageItemList} from "../dtos/storageItemList";

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  private storageBaseUri: string = 'http://localhost:8080/api/v1/storage';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getItems(id: string, searchParameters: ItemSearchDto): Observable<StorageItemList[]> {
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
    console.log(searchParameters)
    return this.httpClient.get<StorageItemList[]>(this.storageBaseUri + '/' + id, {params});
  }

}
