import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {BalanceDebitDto, ExpenseDto, UserValuePairDto} from "../dtos/expenseDto";
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

  updateExpense(expense: ExpenseDto): Observable<ExpenseDto> {
    console.log("Update expense with ID: " + expense.id);
    console.log("and expense:");
    console.log(expense);
    return this.http.put<ExpenseDto>(`${this.baseUri}/${expense.id}`, expense);
  }

  findTotalExpensesPerUser(): Observable<UserValuePairDto[]> {
    return this.http.get<UserValuePairDto[]>(this.baseUri + '/statistics/expenses');
  }

  findTotalDebitsPerUser(): Observable<UserValuePairDto[]> {
    return this.http.get<UserValuePairDto[]>(this.baseUri + '/statistics/debits');
  }

  findBalanceExpenses(): Observable<UserValuePairDto[]> {
    return this.http.get<UserValuePairDto[]>(this.baseUri + '/statistics/balance');
  }

  findBalanceDebits(): Observable<BalanceDebitDto[]> {
    return this.http.get<BalanceDebitDto[]>(this.baseUri + '/debits');
  }

  /**
   * Find expense with given id.
   *
   * @param id of the expense
   */
  findById(id: number): Observable<ExpenseDto> {
    return this.http.get<ExpenseDto>(this.baseUri + '/' + id);
  }
}
