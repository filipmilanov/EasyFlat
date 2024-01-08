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
  showDropdown: boolean = false;


  constructor(public authService: AuthService, private sharedFlatService: SharedFlatService, private httpClient: HttpClient,
              private shoppingService: ShoppingListService, private router: Router) {
  }

  ngOnInit() {
  }

  toggleDropdown(event: Event) {
    event.stopPropagation(); // Prevents the dropdown from closing immediately after opening
    this.showDropdown = !this.showDropdown;
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

  toggleChoresDropdown(event: Event) {
    event.preventDefault();
    this.isChoresDropdownOpen = !this.isChoresDropdownOpen;
  }

  navigateToLeaderboard() {
    this.router.navigate(['/chores/leaderboard']);
  }
}
