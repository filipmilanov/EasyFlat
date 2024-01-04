import {Component} from '@angular/core';
import {EChartsOption} from "echarts";
import {FinanceService} from "../../../../services/finance.service";
import {ToastrService} from "ngx-toastr";
import {UserValuePairDto} from "../../../../dtos/expenseDto";

@Component({
  selector: 'app-radar',
  templateUrl: './radar.component.html',
  styleUrls: ['./radar.component.scss']
})
export class RadarComponent {
  chartOption: EChartsOption;

  constructor(
    private financeService: FinanceService,
    private notification: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.financeService.findTotalExpensesPerUser().subscribe({
      next: (data) => {
        this.financeService.findTotalDebitsPerUser().subscribe({
          next: (data2) => {
            this.initChart(data, data2);
          },
          error: (error) => {
            this.notification.error("Failed to load data for statistics", "Error");
          }
        });
      },
      error: (error) => {
        this.notification.error("Failed to load data for statistics", "Error");
      }
    });
  }


  initChart(totalExpenses: UserValuePairDto[], totalDebits: UserValuePairDto[]): void {
    let highestExpense = totalExpenses.reduce((prev, current) => (prev.value > current.value) ? prev : current).value;
    let highestDebit = totalDebits.reduce((prev, current) => (prev.value > current.value) ? prev : current).value;
    let highestValue = Math.max(highestExpense, highestDebit);
    this.chartOption = {
      tooltip: {},
      legend: {
        data: ['Total Expenses', 'Total Debits']
      },
      radar: {
        indicator:
          totalExpenses.map((value) => {
            return {name: value.user.firstName + ' ' + value.user.lastName, max: highestValue};
          })
      },
      series: [{
        type: 'radar',
        data: [
          {
            value: totalExpenses.map((value) => value.value),
            name: 'Total Expenses'
          },
          {
            value: totalDebits.map((value) => value.value),
            name: 'Total Debits'
          }
        ]
      }]
    };
  }

}
