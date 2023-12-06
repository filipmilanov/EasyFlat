import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {StorageItem} from "../dtos/storageItem";
import {Recipe} from "../dtos/recipe";

@Injectable({
  providedIn: 'root'
})
export class CookingService {
  baseUri = environment.backendUrl + '/cooking';
  constructor(private httpClient: HttpClient, private globals: Globals) { }

  loadRecipes() : Observable<Recipe[]>{
   return this.httpClient.get<Recipe[]>(this.baseUri)
  }

}
