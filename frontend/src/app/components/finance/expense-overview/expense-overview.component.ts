import {Component, OnInit} from '@angular/core';
import {ExpenseDto, ExpenseSearchDto, RepeatingExpenseType} from "../../../dtos/expenseDto";
import {FinanceService} from "../../../services/finance.service";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {debounceTime, Subject} from "rxjs";
import {UserListDto} from "../../../dtos/user";
import {UserService} from "../../../services/user.service";

@Component({
  selector: 'app-expense-overview',
  templateUrl: './expense-overview.component.html',
  styleUrls: ['./expense-overview.component.scss']
})
export class ExpenseOverviewComponent implements OnInit {

  expenses: ExpenseDto[];
  searchParams: ExpenseSearchDto = {};
  searchDate: string | null = null;
  searchChangedObservable = new Subject<void>();
  users: UserListDto[] = [];

  constructor(private userService: UserService,
              private financeService: FinanceService,
              private router: Router,
              private notification: ToastrService) {
  }

  searchChanged(): void {
    this.searchChangedObservable.next();
  }

  ngOnInit(): void {
    this.userService.findFlatmates().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (error) => {
        console.error(error);
        this.notification.error("Could not load flatmates", "Error");
      }
    });
    this.reloadExpenses();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadExpenses()});
  }

  reloadExpenses() {
    if (this.searchDate == null || this.searchDate === "") {
      delete this.searchParams.createdAt;
    } else {
      this.searchParams.createdAt = new Date(this.searchDate);
    }
    this.financeService.findAll(this.searchParams)
      .subscribe({
        next: res => {
          this.expenses = res;
        },
        error: error => {
          console.error(`Expenses could not be loaded: ${error}`);
          this.router.navigate(['/finance/']);
          this.notification.error(`Expenses could not be loaded`, "Error");
          let firstBracket = error.error.indexOf('[');
          let lastBracket = error.error.indexOf(']');
          let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
          let errorDescription = error.error.substring(0, firstBracket);
          errorMessages.forEach(message => {
            this.notification.error(message, errorDescription);
          });
        }
      });
  }

  public delete(expense: ExpenseDto): void {
    this.financeService.deleteExpense(expense.id).subscribe({
      next: (): void => {
        this.notification.success(`Expense ${expense.title} was successfully deleted`, "Success");
      },
      error: error => {
        console.error(`Expense could not be deleted: ${error}`);
        this.notification.error(`Expense ${expense.title} could not be deleted`, "Error");
        let firstBracket = error.error.indexOf('[');
        let lastBracket = error.error.indexOf(']');
        let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
        let errorDescription = error.error.substring(0, firstBracket);
        errorMessages.forEach(message => {
          this.notification.error(message, errorDescription);
        });
      }
    });
  }

  protected readonly UserListDto = UserListDto;
}
