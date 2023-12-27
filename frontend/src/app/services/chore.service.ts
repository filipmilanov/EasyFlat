import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {ChoresDto} from "../dtos/chores";
import {ShoppingListDto} from "../dtos/shoppingList";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ChoreService {
  private choreBaseUri: string = this.globals.backendUri + '/chores'

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  createChore(chore: ChoresDto) {
    return this.httpClient.post<ShoppingListDto>(this.choreBaseUri, chore);
  }

  getChores(searchParams: string): Observable<ChoresDto[]> {
    let params = new HttpParams();
    if (searchParams) {
      params = params.append('searchParams', searchParams);
    }
    return this.httpClient.get<ChoresDto[]>(this.choreBaseUri, {params});
  }
}
