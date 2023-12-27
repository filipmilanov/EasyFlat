import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {ChoresDto} from "../dtos/chores";
import {ShoppingListDto} from "../dtos/shoppingList";

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
}
