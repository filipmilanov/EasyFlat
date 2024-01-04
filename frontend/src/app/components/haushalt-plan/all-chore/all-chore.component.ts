import { Component } from '@angular/core';
import {NgForm} from "@angular/forms";
import {Router} from "@angular/router";
import {ChoresDto} from "../../../dtos/chores";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ToastrService} from "ngx-toastr";
import {ChoreService} from "../../../services/chore.service";
import {forEach} from "lodash";

@Component({
  selector: 'app-all-chore',
  templateUrl: './all-chore.component.html',
  styleUrls: ['./all-chore.component.scss']
})
export class AllChoreComponent {
  chores: ChoresDto[];
  completedChores: ChoresDto[] = [];
  private searchParams: string;

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
        this.router.navigate(['/chores/all']);
      },
      error: err => {
        console.error("Error fetching chores with users")
        this.notification.error("Error assigning chores with users")
      }
    });
  }

  updateChoreComplete(chore: ChoresDto) {
    for (let i = 0; i < this.chores.length; i++) {
      if (this.chores[i] == chore) {
        this.chores[i].completed = !this.chores[i].completed;
        break;
      }
    }
    this.completedChores = this.chores.filter(chore => chore.completed);
  }

  completedChoresIsEmpty() {
    return this.completedChores.length == 0;
  }

  deleteCompletedChores() {
    return this.choreService.deleteChores(this.completedChores).subscribe({
      next: res => {
        this.notification.success(`Chores completed.`, "Success");
        for (let i = 0; i < res.length; i++) {
          this.chores = this.chores.filter(chore => chore.id !== res[i].id);
        }
        this.completedChores = [];
      },
      error: err => {
        console.error("Chores could not be deleted");
      }
    });
  }
}
