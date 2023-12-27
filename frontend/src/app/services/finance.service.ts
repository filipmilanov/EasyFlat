import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {ExpenseDto} from "../dtos/expenseDto";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FinanceService {
  baseUri = environment.backendUrl + '/expense';

  constructor(
    private http: HttpClient,
  ) {
  }

  createExpense(expense: ExpenseDto): Observable<ExpenseDto> {
    console.log("Create expense: " + expense.debitUsers);
    return this.http.post<ExpenseDto>(this.baseUri, expense);
  }
}
