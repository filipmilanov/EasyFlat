import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Observable, Subscription} from "rxjs";
import {UserDetail} from "../../dtos/auth-request";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  user: UserDetail;

  constructor(public authService: AuthService) { }

  ngOnInit() {
    this.authService.getUser(this.authService.getToken())
      .subscribe(
        (user : UserDetail) => {
          this.user = user;
          console.log(user);
        },
        (error) => {
          console.error('Error fetching user:', error);
        }
      );
  }

  isLoggedWg(): boolean{
    return this.user.flatName != null;
  }
}
