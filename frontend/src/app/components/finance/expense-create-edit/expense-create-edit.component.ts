import {Component, OnInit} from '@angular/core';
import {DebitDto, ExpenseDto, SplitBy} from "../../../dtos/expenseDto";
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-expense-create-edit',
  templateUrl: './expense-create-edit.component.html',
  styleUrls: ['./expense-create-edit.component.scss']
})
export class ExpenseCreateEditComponent implements OnInit {

  heading: string = 'Create Expense';
  expense: ExpenseDto = new ExpenseDto();
  splitByOptions = Object.keys(SplitBy).map(key => ({value: key, label: SplitBy[key]}));
  submitButtonText: string = 'Create';
  users: DebitDto[] = [
    {
      isTarget: true,
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
      isTarget: true,
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

  ngOnInit(): void {

  }

  onSubmit(form: NgForm) {

  }
}
