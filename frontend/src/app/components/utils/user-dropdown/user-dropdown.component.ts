import {Component, EventEmitter, Input, Output} from '@angular/core';
import {UserListDto} from "../../../dtos/user";

@Component({
  selector: 'app-user-dropdown',
  templateUrl: './user-dropdown.component.html',
  styleUrls: ['./user-dropdown.component.scss']
})
export class UserDropdownComponent {
  @Input() users: UserListDto[];
  @Input() activatedUser: UserListDto;

  @Output() activatedUserChange = new EventEmitter<UserListDto>();

  onActivatedUserChange(event: any) {
    const selectedIndex = event.target.selectedIndex;
    const selectedUser = this.users[selectedIndex];
    console.log(selectedUser);
    this.activatedUserChange.emit(selectedUser);
  }
}

