import {Component, OnInit} from '@angular/core';
import {BalanceDebitDto} from "../../dtos/expenseDto";
import {ToastrService} from "ngx-toastr";
import {FinanceService} from "../../services/finance.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-finance',
  templateUrl: './finance.component.html',
  styleUrls: ['./finance.component.scss']
})
export class FinanceComponent implements OnInit {

  balanceDebits: BalanceDebitDto[] = [];

  constructor(
      private financeService: FinanceService,
      private notification: ToastrService,
      private activatedRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.reloadData();
  }

  reloadData() {
    let o = this.financeService.findBalanceDebits().subscribe({
      next: (balanceDebits) => {
        console.log(balanceDebits);
        this.balanceDebits = balanceDebits;
      },
      error: (error) => {
        console.log(error);
        this.notification.error("Could not load balance debits", "Error");
      }
    })
  }



}

