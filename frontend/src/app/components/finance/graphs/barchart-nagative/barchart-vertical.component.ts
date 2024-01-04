import {Component, OnInit} from '@angular/core';
import {EChartsOption} from "echarts";
import {FinanceService} from "../../../../services/finance.service";
import {ToastrService} from "ngx-toastr";
import {UserValuePairDto} from "../../../../dtos/expenseDto";


@Component({
  selector: 'app-barchart-vertical',
  templateUrl: './barchart-vertical.component.html',
  styleUrls: ['./barchart-vertical.component.scss']
})
export class BarchartVerticalComponent implements OnInit {

  chartOption: EChartsOption;

  constructor(
    private financeService: FinanceService,
    private notification: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.financeService.findBalanceExpenses().subscribe({
      next: (data) => {
        this.initChart(data);
      },
      error: (error) => {
        this.notification.error("Failed to load data for statistics", "Error");
      }
    });
  }


  initChart(data: UserValuePairDto[]): void {
    this.chartOption = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: {
        type: 'value',
        position: 'top'
      },
      yAxis: {
        type: 'category',
        axisLine: {show: false},
        axisLabel: {show: false},
        axisTick: {show: false},
        splitLine: {show: false},
        data: data.map((value) => value.user.firstName + ' ' + value.user.lastName)
      },
      series: [
        {
          type: 'bar',
          label: {
            show: true,
            formatter: '{b}'
          },
          data: data.map((value) => ({
            value: value.value,
            itemStyle: {
              color: value.value < 0 ? '#d9534f' : '#5cb85c'
            }
          }))
        }
      ]
    };
  }

}
