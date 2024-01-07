import { Component } from '@angular/core';
import {NgForm} from "@angular/forms";
import {Router} from "@angular/router";
import {ChoresDto, ChoreSearchDto} from "../../../dtos/chores";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ToastrService} from "ngx-toastr";
import {ChoreService} from "../../../services/chore.service";

@Component({
  selector: 'app-all-chore',
  templateUrl: './all-chore.component.html',
  styleUrls: ['./all-chore.component.scss']
})
export class AllChoreComponent {
  chores: ChoresDto[];
  searchParams: ChoreSearchDto = {
    userName: '',
    endDate: null,
  };

  constructor(private router: Router,
  private choreService: ChoreService,
  private notification: ToastrService) { }

  navigateToNewChore() {
    this.router.navigate(['/chores', 'add']);
  }

  ngOnInit() {
    this.loadChores();
  }

  loadChores() {
    this.choreService.getChores(this.searchParams).subscribe({
      next: res => {
        this.chores = res;
      },
      error: err => {
        console.error("Error fetching chores", err);
        this.notification.error("Error loading chores")
      }
    })
  }

  assignChores() {
    this.choreService.assginChores().subscribe({
      next: res => {
        this.chores = res;
      },
      error: err => {
        console.error("Error fetching chores with users")
        this.notification.error("Error loading chores")
      }
    });
  }

  exportPDF() {
    this.choreService.generateChoreListPDF().subscribe({
      next: res => {
        this.notification.success("PDF exported.", "Success")
    },
      error: err => {
        console.error("PDF could not be exported");
        this.notification.error("Error exporting PDF");
      }
    })
  }
}
