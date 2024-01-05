import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {ChoreService} from "../../../services/chore.service";
import {ToastrService} from "ngx-toastr";
import {AuthService} from "../../../services/auth.service";
import {UserDetail} from "../../../dtos/auth-request";

@Component({
  selector: 'app-leaderboard',
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.scss']
})
export class LeaderboardComponent {
  users: UserDetail[];

  constructor(private router: Router,
              private notification: ToastrService,
              private choreService: ChoreService) { }

  ngOnInit() {
    this.choreService.getUsers().subscribe({
      next: res => {
        this.users = res;
      },
      error: err => {
        console.error("Error getting users from the persistent data");
      }
    });
  }
}
