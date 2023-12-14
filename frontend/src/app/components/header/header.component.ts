import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {UserDetail} from "../../dtos/auth-request";
import {ShoppingListService} from "../../services/shopping-list.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  user: UserDetail;

  constructor(public authService: AuthService,
              private shoppingListService: ShoppingListService,
              private router: Router) {
  }

  ngOnInit() {
    this.authService.getUser(this.authService.getToken()).subscribe({
      next: res => {
        this.user = res;
      }
    });
  }

  isInWg() {
    return this.user.flatName != null;
  }

  navToDefaultShoppingList() {
    this.router.navigate(["shopping-list/" + this.shoppingListService.getDefaultShopList().id]);
  }
}
