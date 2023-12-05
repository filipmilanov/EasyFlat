import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Subscription} from "rxjs";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  userSubscription;

  constructor(public authService: AuthService) { }

  ngOnInit() {
    this.authService.getUser(this.authService.getToken())
      .subscribe(
        (user) => {
          console.log(user); // Handle the received user data here
        },
        (error) => {
          console.error('Error fetching user:', error);
        }
      );
  }

}
