import {Component, OnInit} from '@angular/core';
import {BalanceDebitDto} from "../../dtos/expenseDto";
import {ToastrService} from "ngx-toastr";
import {FinanceService} from "../../services/finance.service";
import {ActivatedRoute} from "@angular/router";
import {Subject} from "rxjs";
import {UserDetail} from "../../dtos/auth-request";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-finance',
  templateUrl: './finance.component.html',
  styleUrls: ['./finance.component.scss']
})
export class FinanceComponent implements OnInit {
  selectedGraphType: string = 'barchart';
  balanceDebits: BalanceDebitDto[] = [];
  reloadGraph: Subject<boolean> = new Subject<boolean>();
  activeUser: UserDetail;

  constructor(
    private financeService: FinanceService,
    private notification: ToastrService,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService,
  ) {
  }

  ngOnInit(): void {
    this.findActiveUser();
    this.reloadData();
  }

  findActiveUser(): void {
    this.authService.getUser(this.authService.getToken()).subscribe({
      next: (user) => {
        this.activeUser = user;
      },
      error: (error) => {
        console.log(error);
        this.notification.error("Could not load active user", "Error");
      }
    });
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


  protected readonly parseInt = parseInt;
}

