import {Component, OnInit} from '@angular/core';
import {ExpenseDto, SplitBy} from "../../../dtos/expenseDto";
import {NgForm} from "@angular/forms";
import {FinanceService} from "../../../services/finance.service";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {UserService} from "../../../services/user.service";
import {UserListDto} from "../../../dtos/user";
import {AuthService} from "../../../services/auth.service";
import {NgbDateStruct, NgbTimepickerConfig, NgbTimeStruct} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-expense-create-edit',
  templateUrl: './expense-create-edit.component.html',
  styleUrls: ['./expense-create-edit.component.scss']
})
export class ExpenseCreateEditComponent implements OnInit {

  heading: string = 'Create Expense';
  expense: ExpenseDto = new ExpenseDto();
  amountInEuro: number;
  splitByOptions = Object.keys(SplitBy).map(key => ({value: key, label: SplitBy[key]}));
  selectedSplitBy: SplitBy = SplitBy.EQUAL;
  submitButtonText: string = 'Create';
  users: UserListDto[] = [];
  expenseDate: NgbDateStruct;
  expenseTime: NgbTimeStruct = {hour: 13, minute: 30, second: 0};
  selectedRepeatingOption: boolean = false;

  constructor(
    private userService: UserService,
    private financeService: FinanceService,
    private authService: AuthService,
    private router: Router,
    private notification: ToastrService,
    config: NgbTimepickerConfig
  ) {
    config.spinners = false;
  }

  ngOnInit(): void {
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
          },
          error: (error) => {
            console.log(error);
            this.notification.error("Could not load user data", "Error");
          }
        });
      },
      error: (error) => {
        console.log(error);
        this.notification.error("Could not load flatmates", "Error");
      }
    });
    let now = new Date();
    this.expenseDate = {year: now.getFullYear(), month: now.getMonth() + 1, day: now.getDate()}
    this.expenseTime = {hour: now.getHours(), minute: now.getMinutes(), second: now.getSeconds()};
  }

  onSubmit(form: NgForm) {
    if (this.checkIfAmountIsToHigh()) {
      return;
    }
    this.prepareExpense();

    console.log(this.expense);
    let o = this.financeService.createExpense(this.expense).subscribe({
      next: (expense: ExpenseDto) => {
        this.notification.success("Expense created", "Success");
        this.router.navigate(['/finance']);
      },
      error: (error) => {
        console.log(error);
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

  private prepareExpense() {
    this.expense.createdAt = new Date(
      this.expenseDate.year,
      this.expenseDate.month - 1,
      this.expenseDate.day,
      this.expenseTime.hour + 1,
      this.expenseTime.minute,
    );
    this.expense.amountInCents = this.amountInEuro * 100;
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
    this.expense.isRepeating = this.selectedRepeatingOption;
  }
}
