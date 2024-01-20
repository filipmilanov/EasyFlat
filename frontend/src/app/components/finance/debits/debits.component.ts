import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BalanceDebitDto, ExpenseDto, SplitBy} from "../../../dtos/expenseDto";
import {UserListDto} from "../../../dtos/user";
import {FinanceService} from "../../../services/finance.service";
import {ToastrService} from "ngx-toastr";

@Component({
    selector: 'app-debits',
    templateUrl: './debits.component.html',
    styleUrls: ['./debits.component.scss']
})
export class DebitsComponent {
    @Input() balanceDebits: BalanceDebitDto[] = [];
  @Input() activeUserId: number;

    @Output() reloadData = new EventEmitter<void>();

    constructor(
        private financeService: FinanceService,
        private notification: ToastrService,
    ) {
    }

    formatUserName(user: UserListDto): string {
        return user.firstName + ' ' + user.lastName;
    }

    convertAmountToEuro(amountInCent: number): string {
        return (amountInCent / 100.0).toFixed(2);
    }


    payback(debit: BalanceDebitDto) {
        let expenseDto: ExpenseDto = {
            amountInCents: debit.valueInCent,
            title: "Payback",
            description: this.formatUserName(debit.debtor) + " pays back " + this.formatUserName(debit.creditor),
            paidBy: debit.debtor,
            createdAt: new Date(),

            debitUsers: [
                {
                    user: debit.creditor,
                    value: debit.valueInCent,
                    splitBy: SplitBy.UNEQUAL
                }
            ],
        }

        this.financeService.createExpense(expenseDto).subscribe({
            next: (expense) => {
                this.notification.success("Payback successful", "Success");
                this.reloadData.emit();
            },
            error: (error) => {
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
}
