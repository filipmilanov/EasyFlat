import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {UserDetail} from "../../dtos/auth-request";
import {ShoppingListService} from "../../services/shopping-list.service";
import {Router} from "@angular/router";
import {SharedFlatService} from "../../services/sharedFlat.service";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {ShoppingListDto} from "../../dtos/shoppingList";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  isChoresDropdownOpen = false;

  constructor(public authService: AuthService, private sharedFlatService: SharedFlatService, private httpClient: HttpClient,
              private shoppingService: ShoppingListService, private router: Router) {
  }

  ngOnInit() {
  }

  isInWg() {
    return this.sharedFlatService.isLoggInWg();
  }

  logoutUser() {
    this.sharedFlatService.changeEventToFalse();
    this.authService.logoutUser();
  }

  navigateTo(route: string) {
    switch (route) {
      case 'allChores':
        this.router.navigate(['/allChores']);
        break;
      case 'myChores':
        this.router.navigate(['/myChores']);
        break;
      case 'chorePreference':
        this.router.navigate(['/chorePreference']);
        break;
      case 'newChore':
        this.router.navigate(['/newChore']);
        break;
      default:
        this.router.navigate(['']);
        break;
    }
  }


  navigateToAllChores() {
    console.log("Start");
    this.router.navigate(['/allChores']);
    console.log("After");
  }

  navigateToMyChores() {
    this.router.navigate(['/myChores']);
  }

  navigateToPreference() {
    this.router.navigate(['/chorePreference']);
  }

  navigateToNewChore() {
    this.router.navigate(['/newChore'])
  }

  toggleChoresDropdown(event: Event) {
    event.preventDefault();
    this.isChoresDropdownOpen = !this.isChoresDropdownOpen;
  }
}
