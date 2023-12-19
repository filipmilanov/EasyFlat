import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {DebitDto, SplitBy} from "../../../dtos/expenseDto";

@Component({
  selector: 'app-show-user-for-expense',
  templateUrl: './show-user-for-expense.component.html',
  styleUrls: ['./show-user-for-expense.component.scss']
})
export class ShowUserForExpenseComponent implements OnChanges {
  @Input() amountInEuro: number;
  @Input() splitBy: SplitBy;
  @Input() users: DebitDto[];

  @Output() usersChange = new EventEmitter<DebitDto[]>();

  selectedUsers: boolean[] = [];

  onUsersChange() {
    this.usersChange.emit(this.users);
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.updateSelectedUsersArray();
    this.adaptedToChange();
  }

  kindOfValue(value: DebitDto): string {
    if (this.splitBy === SplitBy.EQUAL) {
      return '€';
    } else if (this.splitBy === SplitBy.UNEQUAL) {
      return '€';
    } else if (this.splitBy === SplitBy.PERCENTAGE) {
      return '%';
    } else if (this.splitBy === SplitBy.PROPORTIONAL) {
      return 'Proportion';
    }
  }

  adaptedToChange() {
    if (this.splitBy === SplitBy.EQUAL) {
      this.users.forEach(user => {
        if (this.selectedUsers[this.users.indexOf(user)]) {
          user.value = (this.amountInEuro ? this.amountInEuro : 0) / this.sizeOfSelectedUsers();
        } else {
          user.value = 0;
        }
      })
    } else if (this.splitBy === SplitBy.PERCENTAGE) {
      this.users.forEach(user => {
        if (this.selectedUsers[this.users.indexOf(user)]) {
          user.value = 100 / this.sizeOfSelectedUsers();
        } else {
          user.value = 0;
        }
      })
    } else {
      this.users.forEach(user => {
        user.value = 0
      })
    }
  }


  private sizeOfSelectedUsers(): number {
    return this.selectedUsers.filter(value => value).length;
  }

  private updateSelectedUsersArray() {
    this.users.forEach((value, index) => {
      this.selectedUsers[index] = this.selectedUsers[index] !== false;
    });
  }

  protected readonly SplitBy = SplitBy;
}
