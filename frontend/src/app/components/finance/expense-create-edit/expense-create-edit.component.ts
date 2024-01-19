import {Component, OnInit} from '@angular/core';
import {ExpenseDto, RepeatingExpenseOptions, RepeatingExpenseType, SplitBy} from "../../../dtos/expenseDto";
import {NgForm} from "@angular/forms";
import {FinanceService} from "../../../services/finance.service";
import {ActivatedRoute, Router, UrlTree} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {UserService} from "../../../services/user.service";
import {UserListDto} from "../../../dtos/user";
import {AuthService} from "../../../services/auth.service";
import {NgbDateStruct, NgbTimepickerConfig, NgbTimeStruct} from '@ng-bootstrap/ng-bootstrap';
import {Observable} from "rxjs";

export enum ExpenseCreateEditMode {
  create,
  edit,
}

@Component({
  selector: 'app-expense-create-edit',
  templateUrl: './expense-create-edit.component.html',
  styleUrls: ['./expense-create-edit.component.scss']
})
export class ExpenseCreateEditComponent implements OnInit {

  mode: ExpenseCreateEditMode = ExpenseCreateEditMode.create;
  expense: ExpenseDto = new ExpenseDto();
  amountInEuro: number;
  splitByOptions = Object.keys(SplitBy).map(key => ({value: key, label: SplitBy[key]}));
  selectedSplitBy: SplitBy = SplitBy.EQUAL;
  users: UserListDto[] = [];
  expenseDate: NgbDateStruct;
  expenseTime: NgbTimeStruct = {hour: 13, minute: 30, second: 0};
  selectedRepeatingOption: RepeatingExpenseOptions = RepeatingExpenseOptions.NO_REPEAT
  previousUrl: UrlTree;

  constructor(
    private userService: UserService,
    private financeService: FinanceService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    config: NgbTimepickerConfig
  ) {
    config.spinners = false;
    this.previousUrl = this.router.getCurrentNavigation().previousNavigation.finalUrl;
  }

  public get heading(): string {
    switch (this.mode) {
      case ExpenseCreateEditMode.create:
        return 'Create expense';
      case ExpenseCreateEditMode.edit:
        return 'Editing expense';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case ExpenseCreateEditMode.create:
        return 'Create';
      case ExpenseCreateEditMode.edit:
        return 'Update';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === ExpenseCreateEditMode.create;
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case ExpenseCreateEditMode.create:
        return 'created';
      case ExpenseCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    if (this.mode === ExpenseCreateEditMode.create) {
      this.userService.findFlatmates().subscribe({
        next: (users) => {
          this.expense.debitUsers = users.map(user => {
            return {
              user: user,
              splitBy: this.selectedSplitBy,
              value: 0
            }
          });
          this.users = users;
          this.onSplitByChange();

          this.authService.getUser(this.authService.getToken()).subscribe({
            next: (user) => {
              // TODO: this is a quickfix. The UserDetail should contain the ID of the user, but that's not the case
              this.expense.paidBy = this.users.find(u => u.firstName === user.firstName && u.lastName === user.lastName);
              this.expense.repeatingExpenseType = RepeatingExpenseType.FIRST_OF_MONTH;
            },
            error: (error) => {
              console.error(error);
              this.notification.error("Could not load user data", "Error");
            }
          });
        },
        error: (error) => {
          console.error(error);
          this.notification.error("Could not load flatmates", "Error");
        }
      });
      let now = new Date();
      this.expenseDate = {year: now.getFullYear(), month: now.getMonth() + 1, day: now.getDate()}
      this.expenseTime = {hour: now.getHours(), minute: now.getMinutes(), second: now.getSeconds()};
    }

    if (this.mode === ExpenseCreateEditMode.edit) {
      this.route.params.subscribe({
        next: params => {
          const expenseId = params.id;
          this.financeService.findById(expenseId).subscribe({
            next: res => {
              this.expense = res;
              let date: Date = new Date(res.createdAt);
              this.expenseDate = {year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate()}
              this.expenseTime = {hour: date.getHours(), minute: date.getMinutes(), second: date.getSeconds()};
              this.amountInEuro = res.amountInCents / 100;
              this.selectedSplitBy = res.debitUsers[0].splitBy;

              this.userService.findFlatmates().subscribe({
                next: (users) => {
                  this.users = users;
                },
                error: (error) => {
                  console.error(error);
                  this.notification.error("Could not load flatmates", "Error");
                }
              });
            },
            error: error => {
              console.error(`Expense could not be retrieved from the backend: ${error}`);
              this.router.navigate(['/finance']);
              this.notification.error('Expense could not be retrieved', "Error");
            }
          })
        },
        error: error => {
          console.error(`Expense could not be retrieved using the ID from the URL: ${error}`);
          this.router.navigate(['/finance']);
          this.notification.error('No expense provided for editing', "Error");
        }
      });

    }
  }

  onSubmit(form: NgForm): void {
    if (this.checkIfAmountIsToHigh()) {
      return;
    }
    this.prepareExpense();

    if (form.valid) {
      let observable: Observable<ExpenseDto>;
      switch (this.mode) {
        case ExpenseCreateEditMode.create:
          observable = this.financeService.createExpense(this.expense);
          break;
        case ExpenseCreateEditMode.edit:
          observable = this.financeService.updateExpense(this.expense);
          break;
        default:
          console.error('Unknown ExpenseCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: () => {
          this.notification.success(`Expense ${this.expense.title} successfully ${this.modeActionFinished}.`, "Success");
          this.router.navigate(['/finance']);
        },
        error: (error) => {
          console.error(`Error expense was not ${this.modeActionFinished}: ${error}`);
          console.error(error);
          let firstBracket = error.error.indexOf('[');
          let lastBracket = error.error.indexOf(']');
          let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
          let errorDescription = error.error.substring(0, firstBracket);
          errorMessages.forEach(message => {
            this.notification.error(message, errorDescription);
          });

          if (this.selectedSplitBy === SplitBy.EQUAL || this.selectedSplitBy === SplitBy.UNEQUAL) {
            this.expense.debitUsers.forEach(user => {
              user.value = user.value / 100;
            });
          }
        }
      });
    }
  }

  delete(): void {
    this.financeService.deleteExpense(this.expense.id).subscribe({
      next: (): void => {
        this.router.navigate(['/finance/']);
        this.notification.success(`Expense ${this.expense.title} was successfully deleted`, "Success");
      },
      error: error => {
        console.error(`Expense could not be deleted: ${error}`);
        this.notification.error(`Expense ${this.expense.title} could not be deleted`, "Error");
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

  private prepareExpense() {
    this.expense.createdAt = new Date(
      this.expenseDate.year,
      this.expenseDate.month - 1,
      this.expenseDate.day,
      this.expenseTime.hour + 1,
      this.expenseTime.minute,
    );
    this.expense.amountInCents = this.roundToTwoDecimals(this.amountInEuro) * 100;
    if (this.selectedRepeatingOption != RepeatingExpenseOptions.REPEAT_AT) {
      this.expense.repeatingExpenseType = null;
    }
    if (this.selectedSplitBy === SplitBy.EQUAL || this.selectedSplitBy === SplitBy.UNEQUAL) {
      this.expense.debitUsers.forEach(user => {
        user.value = user.value * 100;
      });
    }
  }

  private checkIfAmountIsToHigh() {
    if (this.amountInEuro > 10_000) {
      this.notification.error("Amount too high. The maximum amount possible is 10.000", "Error");
      return true;
    }
    return false;
  }

  onSplitByChange() {
    this.expense.debitUsers.forEach(user => {
      user.splitBy = this.selectedSplitBy;
    });
  }

  onRepeatingChange() {
    this.expense.isRepeating = this.selectedRepeatingOption != RepeatingExpenseOptions.NO_REPEAT;
  }

  private roundToTwoDecimals(value: number): number {
    return Math.round(value * 100) / 100;
  }

  protected readonly RepeatingExpenseOptions = RepeatingExpenseOptions;
  protected readonly RepeatingExpenseTyp = RepeatingExpenseType;
}
