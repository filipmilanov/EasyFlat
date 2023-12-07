import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {StorageItem} from "../dtos/storageItem";
import {RecipeSuggestion} from "../dtos/recipeSuggestion";

@Injectable({
  providedIn: 'root'
})
export class CookingService {
  baseUri = environment.backendUrl + '/cooking';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  loadRecipes(): Observable<RecipeSuggestion[]> {
    return this.httpClient.get<RecipeSuggestion[]>(this.baseUri)
  }

}
