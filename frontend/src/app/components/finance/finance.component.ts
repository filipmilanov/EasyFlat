import {Component, OnInit} from '@angular/core';
import {BalanceDebitDto} from "../../dtos/expenseDto";
import {ToastrService} from "ngx-toastr";
import {FinanceService} from "../../services/finance.service";
import {ActivatedRoute} from "@angular/router";
import {Subject} from "rxjs";

@Component({
  selector: 'app-finance',
  templateUrl: './finance.component.html',
  styleUrls: ['./finance.component.scss']
})
export class FinanceComponent implements OnInit {
  selectedGraphType: string = 'barchart';
  balanceDebits: BalanceDebitDto[] = [];
  reloadGraph: Subject<boolean> = new Subject<boolean>();

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
    this.reloadGraph.next(true);

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

