import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {AuthRequest} from "../../dtos/auth-request";

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {

  user: AuthRequest;
  isEditing = false;

  constructor(public authService: AuthService) {
  }

  ngOnInit(): void {
    this.authService.getUser(this.authService.getToken()).subscribe(
      (user) => {
        this.user = user;
        console.log('User :', this.user); // Use user data as needed
      },
      (error) => {
        console.error('Error fetching user:', error);
      }
    );
  }

}
