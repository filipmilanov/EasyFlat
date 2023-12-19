import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {ItemDto} from "../dtos/item";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ItemCardService {

  constructor(
    private http: HttpClient,
  ) { }
}
