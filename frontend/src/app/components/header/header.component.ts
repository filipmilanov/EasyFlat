import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {UserDetail} from "../../dtos/auth-request";
import {ShoppingListService} from "../../services/shopping-list.service";
import {Router} from "@angular/router";
import {SharedFlatService} from "../../services/sharedFlat.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  user: UserDetail;
  event: boolean = false;

  constructor(public authService: AuthService, private sharedFlatService: SharedFlatService) {
  }

  ngOnInit() {
  }

  isInWg() {
    return this.sharedFlatService.isLoggInWg();
  }
}
