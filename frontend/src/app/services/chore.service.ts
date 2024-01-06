import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {ChoresDto, ChoreSearchDto} from "../dtos/chores";
import {ShoppingListDto} from "../dtos/shoppingList";
import {Observable} from "rxjs";
import {Preference} from "../dtos/preference";
import {UserDetail} from "../dtos/auth-request";
import {formatDate} from "@angular/common";

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

  getChores(searchParams: ChoreSearchDto): Observable<ChoresDto[]> {
    let params = new HttpParams();
    if (searchParams.userName) {
      console.log(searchParams.userName)
      params = params.append('userName', searchParams.userName);
    }
    if (searchParams.endDate) {
      params = params.append('endDate', formatDate(searchParams.endDate, 'YYYY-MM-dd', 'en-DK'));
    }
    return this.httpClient.get<ChoresDto[]>(this.choreBaseUri, {params});
  }

  assginChores() {
    console.log("Before Request");
    return this.httpClient.put<ChoresDto[]>(`${this.choreBaseUri}`,{});
  }

  getChoresByUser(searchParams: string) {
    return this.httpClient.get<ChoresDto[]>(this.choreBaseUri + '/user');

  }

  deleteChores(completedChores: ChoresDto[]) {
    const choreIds = completedChores.map(chore => chore.id);
    return this.httpClient.delete<ChoresDto[]>(this.choreBaseUri + '/delete', { params: { choreIds: choreIds.join(',') } });
  }

  getUsers() {
    return this.httpClient.get<UserDetail[]>(this.choreBaseUri + '/users');
  }

  updatePoints(points: number, userId: string) {
    const requestBody = { points: points };  // Assuming UserDetail has a 'points' property
    console.log(requestBody);
    return this.httpClient.patch<UserDetail>(this.choreBaseUri + '/' + userId, requestBody);
  }
}
