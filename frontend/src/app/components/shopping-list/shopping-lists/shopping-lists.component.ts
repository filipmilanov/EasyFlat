import {Component, OnInit} from '@angular/core';
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ShoppingListDto} from "../../../dtos/shoppingList";
import {error} from "@angular/compiler-cli/src/transformers/util";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-shopping-lists',
  templateUrl: './shopping-lists.component.html',
  styleUrls: ['./shopping-lists.component.scss']
})
export class ShoppingListsComponent implements OnInit{
  lists: ShoppingListDto[];
  searchParams: string;
  showInput: boolean = false;

  constructor(private shoppingService: ShoppingListService,
              private notification: ToastrService) {
  }


  openInput() {
    this.showInput = true;
  }

  ngOnInit() {
    this.loadLists();
  }

  loadLists() {
    this.shoppingService.getShoppingLists(this.searchParams).subscribe({
      next: res => {
        this.lists = res;
        console.log(this.lists)
      },
      error: err => {
        console.error("Error fetching shopping lists", err);
        this.notification.error("Error loading shopping lists")
      }
    })
  }
}
