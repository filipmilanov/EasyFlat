import { Component } from '@angular/core';
import {NgForm} from "@angular/forms";
import {Router} from "@angular/router";

@Component({
  selector: 'app-all-chore',
  templateUrl: './all-chore.component.html',
  styleUrls: ['./all-chore.component.scss']
})
export class AllChoreComponent {

  constructor(private router: Router) { }

  onSubmit(form: NgForm) {

  }

  navigateToNewChore() {
    this.router.navigate(['/chores', 'add']);
  }
}
