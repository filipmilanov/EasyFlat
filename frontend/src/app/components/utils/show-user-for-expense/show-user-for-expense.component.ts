import {Component, Input} from '@angular/core';
import {DebitDto, SplitBy} from "../../../dtos/expenseDto";

@Component({
  selector: 'app-show-user-for-expense',
  templateUrl: './show-user-for-expense.component.html',
  styleUrls: ['./show-user-for-expense.component.scss']
})
export class ShowUserForExpenseComponent {
  @Input() users: DebitDto[];

  kindOfValue(value: DebitDto): string {
    if (value.splitBy === SplitBy.EQUAL) {
      return 'Amount';
    } else if (value.splitBy === SplitBy.UNEQUAL) {
      return 'Amount';
    } else if (value.splitBy === SplitBy.PERCENTAGE) {
      return 'percentage';
    } else if (value.splitBy === SplitBy.PROPORTIONAL) {
      return 'proportion';
    }
  }

}
