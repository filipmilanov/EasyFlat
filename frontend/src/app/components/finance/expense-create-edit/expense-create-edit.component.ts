import {Component, OnInit} from '@angular/core';
import {DebitDto, ExpenseDto, SplitBy} from "../../../dtos/expenseDto";
import {NgForm} from "@angular/forms";
import {SharedFlatService} from "../../../services/sharedFlat.service";
import {FinanceService} from "../../../services/finance.service";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

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
  users: DebitDto[] = [
    {
      user: {
        firstName: 'Max',
        lastName: 'Mustermann',
        email: 'a@a.c',
        flatName: 'WG',
        password: '123',
        admin: true
      }
    },
    {
      user: {
        firstName: 'Max',
        lastName: 'Mustermann',
        email: 'a@a.c',
        flatName: 'WG',
        password: '123',
        admin: true
      }
    }
  ];

  constructor(
    private sharedFlatService: SharedFlatService,
    private financeService: FinanceService,
    private router: Router,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {

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
