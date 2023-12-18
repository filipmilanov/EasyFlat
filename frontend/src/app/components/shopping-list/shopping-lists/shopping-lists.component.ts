import { Component } from '@angular/core';
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ShoppingListDto} from "../../../dtos/shoppingList";
import {error} from "@angular/compiler-cli/src/transformers/util";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-shopping-lists',
  templateUrl: './shopping-lists.component.html',
  styleUrls: ['./shopping-lists.component.scss']
})
export class ShoppingListsComponent {
  lists: ShoppingListDto[];
  searchParams: string;

  constructor(private shoppingService: ShoppingListService,
              private notification: ToastrService) {
  }

  ngOnInit() {
    this.loadLists();
  }

  loadLists() {
    this.shoppingService.getShoppingLists(this.searchParams).subscribe({
      next: res => {
        this.lists = res;
      },
      error: err => {
        console.error("Error fetching shopping lists", err);
        this.notification.error("Error loading shopping lists")
      }
    })
  }
}
