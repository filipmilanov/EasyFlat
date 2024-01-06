import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {ChoreService} from "../../../services/chore.service";
import {ToastrService} from "ngx-toastr";
import {ChoresDto} from "../../../dtos/chores";
import {AuthService} from "../../../services/auth.service";

@Component({
  selector: 'app-my-chores',
  templateUrl: './my-chores.component.html',
  styleUrls: ['./my-chores.component.scss']
})
export class MyChoresComponent {
  chores: ChoresDto[];
  completedChores: ChoresDto[] = [];
  private searchParams: string;

  constructor(private router: Router,
              private choreService: ChoreService,
              private notification: ToastrService,
              private authService: AuthService) { }

  ngOnInit() {
    this.choreService.getChoresByUser(this.searchParams).subscribe({
      next: res => {
        if (res.length == 0) {
          this.notification.success("You've completed all of your tasks. Good job!")
        }
        this.chores = res;
      },
      error: err => {
        console.error("Error fetching chores", err);
        this.notification.error("Error loading chores")
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
    console.log(this.completedChores)
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
        this.awardPoints();
        this.completedChores = [];

      },
      error: err => {
        console.error("Chores could not be deleted");
      }
    });
  }

  awardPoints() {
    for (let i = 0; i < this.completedChores.length; i++) {
      let curr = this.completedChores[i];
      let points = curr.points + 20;
      this.authService.updatePoints(points, curr.user.id).subscribe({
        next: res => {
          this.notification.success("Points awarded.", "Success");
        },
        error: err => {
          console.error("Application users could not be update");
        }
      });
    }
  }

}
