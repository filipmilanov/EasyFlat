import {Component} from '@angular/core';
import {ChoresDto} from "../../../dtos/chores";
import {NgForm} from "@angular/forms";
import {ShoppingItemDto} from "../../../dtos/item";
import {ItemCreateEditMode} from "../../digital-storage/item-create-edit/item-create-edit.component";
import {ChoreService} from "../../../services/chore.service";
import {Observable} from "rxjs";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {UnitService} from "../../../services/unit.service";

@Component({
  selector: 'app-new-chore',
  templateUrl: './new-chore.component.html',
  styleUrls: ['./new-chore.component.scss']
})
export class NewChoreComponent {
  chore: ChoresDto = {
    id: null,
    name: '',
    description: null,
    endDate: new Date(),
    points: null,
    user: null,
    sharedFlat: null,
    completed: false
  };

  constructor(
    private choreService: ChoreService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  onSubmit(form: NgForm) {
    console.log('is form valid?', form.valid, this.chore);
    if (!this.chore.points) {
      this.chore.points = 0;
    }
    this.choreService.createChore(this.chore).subscribe({
      next: data => {
        this.chore = data;
        this.notification.success(`Chore ${this.chore.name} successfully created.`, "Success");
        this.router.navigate(['/chores', 'all']);
      },
      error: error => {
        console.error(`Error item was not created`);
        this.notification.error("Validation error")
      }
    });
  }
}
