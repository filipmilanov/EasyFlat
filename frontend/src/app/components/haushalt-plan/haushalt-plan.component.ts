import { Component } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-haushalt-plan',
  templateUrl: './haushalt-plan.component.html',
  styleUrls: ['./haushalt-plan.component.scss']
})
export class HaushaltPlanComponent {

  constructor(private router: Router) {
  }

  navigateToAllChores() {
    this.router.navigate(['/chores/all']);
  }

  navigateToMyChores() {
    this.router.navigate(['/chores/my']);
  }

  navigateToPreference() {
    this.router.navigate(['/chores/preference']);
  }

  navigateToNewChore() {
    this.router.navigate(['/chores/add'])
  }

  navigateToLeaderboard() {
    this.router.navigate(['/chores/leaderboard']);
  }
}
