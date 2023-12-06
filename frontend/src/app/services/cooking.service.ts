import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";

@Injectable({
  providedIn: 'root'
})
export class CookingService {
  baseUri = environment.backendUrl + '/cooking';
  constructor(private httpClient: HttpClient, private globals: Globals) { }

  loadRecipes(){

  }

}
