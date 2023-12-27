import { Component } from '@angular/core';
import {NgForm} from "@angular/forms";
import {ChoresDto} from "../../../dtos/chores";
import {Observable} from "rxjs";
import {ShoppingItemDto} from "../../../dtos/item";
import {ItemCreateEditMode} from "../../digital-storage/item-create-edit/item-create-edit.component";
import {ChoreService} from "../../../services/chore.service";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {UnitService} from "../../../services/unit.service";
import {Preference} from "../../../dtos/preference";
import {PreferenceService} from "../../../services/preference.service";

@Component({
  selector: 'app-chore-preference',
  templateUrl: './chore-preference.component.html',
  styleUrls: ['./chore-preference.component.scss']
})
export class ChorePreferenceComponent {
  preference: Preference;
  constructor(
    private preferenceService: PreferenceService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.preference);

    if (form.valid) {
      let observable: Observable<Preference>;
      observable = this.preferenceService.editPreference(this.preference);

      observable.subscribe({
        next: data => {
          this.notification.success(`Preferences successfully changed.`, "Success");
        },
        error: error => {
          console.error(`Error preferences were not changed`);
          this.notification.error("Validation error");
        }
      });
    }
  }
}
