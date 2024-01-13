import { Component } from '@angular/core';
import {Observable} from "rxjs";
import {ItemDto} from "../../../dtos/item";
import {ItemCreateEditMode} from "../../digital-storage/item-create-edit/item-create-edit.component";
import {NgForm} from "@angular/forms";
import {ItemService} from "../../../services/item.service";
import {StorageService} from "../../../services/storage.service";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {ShoppingListDto} from "../../../dtos/shoppingList";

@Component({
  selector: 'app-shopping-list-create',
  templateUrl: './shopping-list-create.component.html',
  styleUrls: ['./shopping-list-create.component.scss']
})
export class ShoppingListCreateComponent {

  list: ShoppingListDto = {
    id: 0,
    name: '',
    items: []
  };
  constructor(
    private itemService: ItemService,
    private storageService: StorageService,
    private shoppingService: ShoppingListService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.list);

    if (form.valid) {
      let observable: Observable<ShoppingListDto>;
      observable = this.shoppingService.createList(this.list.name);
      observable.subscribe({
        next: data => {
          this.notification.success(`New shopping list successfully created.`, "Success");
          this.router.navigate(['/shopping-lists']);
        },
        error: error => {
          console.error(`Error list was not created`);
        }
      });
    }
  }
}
