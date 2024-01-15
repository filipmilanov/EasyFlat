import {Component, OnInit} from '@angular/core';
import {NgForm} from "@angular/forms";
import {ChoresDto, ChoreSearchDto} from "../../../dtos/chores";
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
import {PreferenceStorageService} from "../../../services/preference-storage-service";
import {SharedFlat} from "../../../dtos/sharedFlat";

@Component({
  selector: 'app-chore-preference',
  templateUrl: './chore-preference.component.html',
  styleUrls: ['./chore-preference.component.scss']
})
export class ChorePreferenceComponent implements OnInit {
  preference: Preference = {
    id: null,
    first: null,
    second: null,
    third: null,
    fourth: null
  };
  chores: ChoresDto[] = [];

  oldPreference: Preference = {
    id: null,
    first: null,
    second: null,
    third: null,
    fourth: null
  };

  private searchParams: ChoreSearchDto = {
    userName: '',
    endDate: null,
  };

  constructor(
    private preferenceService: PreferenceService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private choreService: ChoreService,
  ) {
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.preference);
    console.log(this.chores);
    if (form.valid) {
      let observable: Observable<Preference>;
      observable = this.preferenceService.editPreference(this.preference);

      observable.subscribe({
        next: data => {
          this.notification.success(`Preferences successfully changed.`, "Success");
          console.log('Preferences updated successfully:', data);

          // Fetch the updated preference after editing
          this.preferenceService.getLastPreference().subscribe({
            next: (lastPref: Preference) => {
              if (lastPref) {
                this.oldPreference = lastPref;
                console.log(lastPref.first)
                console.log('Updated oldPreference:', this.oldPreference);
              }
            },
            error: (error) => {
              console.error('Error fetching last preference:', error);
            }
          });
          this.router.navigate(['/chores/all']);
        },
        error: error => {
          console.error(`Error preferences were not changed`);
          this.notification.error("Preferences were not changed");
        }
      });
    }
  }


  ngOnInit(): void {
    console.log(this.oldPreference);

    this.preferenceService.getLastPreference().subscribe({
      next: (lastPreference: Preference) => {
        if (lastPreference) {
          this.oldPreference = lastPreference;
          console.log('lastPref', lastPreference);
        }

        // Rest of your code...
        this.choreService.getUnassignedChores().subscribe({
          next: (chores: any[]) => {
            this.chores = chores;
            console.log('chores', this.chores)
          },
          error: (error: any) => {
            console.error('Error fetching chores:', error);
          }
        });
      },
      error: (error: any) => {
        console.error('Error fetching last preference:', error);

        this.choreService.getUnassignedChores().subscribe({
          next: (chores: any[]) => {
            this.chores = chores;
          },
          error: (choreError: any) => {
            console.error('Error fetching chores:', choreError);
          }
        });
      }
    });
  }
}
