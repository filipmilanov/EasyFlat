import {Component, OnInit} from '@angular/core';
import {DebitDto, ExpenseDto, SplitBy} from "../../../dtos/expenseDto";
import {NgForm} from "@angular/forms";
import {FinanceService} from "../../../services/finance.service";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {UserService} from "../../../services/user.service";

@Component({
  selector: 'app-expense-create-edit',
  templateUrl: './expense-create-edit.component.html',
  styleUrls: ['./expense-create-edit.component.scss']
})
export class ExpenseCreateEditComponent implements OnInit {

  heading: string = 'Create Expense';
  expense: ExpenseDto = new ExpenseDto();
  splitByOptions = Object.keys(SplitBy).map(key => ({value: key, label: SplitBy[key]}));
  selectedSplitBy: SplitBy = SplitBy.EQUAL;
  submitButtonText: string = 'Create';
  users: DebitDto[] = [];

  constructor(
    private userService: UserService,
    private financeService: FinanceService,
    private router: Router,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.userService.findFlatmates().subscribe({
      next: (users) => {
        this.users = users.map(user => {
          return {
            user: user,
            splitBy: this.selectedSplitBy
          }
        });
      },
      error: (error) => {
        console.log(error);
        this.notification.error("Could not load flatmates", "Error");
      }
    });
    this.onSplitByChange();
  }

  onSubmit(form: NgForm) {
    let o = this.financeService.createExpense(this.expense).subscribe({
      next: (expense: ExpenseDto) => {
        this.notification.success("Expense created");
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
      }
    });
  }

  onSplitByChange() {
    this.users.forEach(user => {
      user.splitBy = this.selectedSplitBy;
    });
  }
}
