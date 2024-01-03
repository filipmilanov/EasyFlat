import {Component, Input} from '@angular/core';
import {BalanceDebitDto} from "../../../dtos/expenseDto";
import {UserListDto} from "../../../dtos/user";

@Component({
  selector: 'app-debits',
  templateUrl: './debits.component.html',
  styleUrls: ['./debits.component.scss']
})
export class DebitsComponent {
  @Input() balanceDebits: BalanceDebitDto[] = [];

  formatUserName(user: UserListDto): string {
    return user.firstName + ' ' + user.lastName;
  }

  convertAmountToEuro(amountInCent: number): string {
    return (amountInCent / 100.0).toFixed(2);
  }
}
