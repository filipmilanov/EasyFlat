import {Component, ElementRef, OnInit} from '@angular/core';
import {ShoppingList} from "../../dtos/shoppingList";
import {ToastrService} from "ngx-toastr";
import {ShoppingListService} from "../../services/shoppingList.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ShoppingItemDto} from "../../dtos/item";
import {ItemService} from "../../services/item.service";
import {StorageService} from "../../services/storage.service";

@Component({
  selector: 'app-shopping-list',
  templateUrl: './shopping-list.component.html',
  styleUrls: ['./shopping-list.component.scss']
})
export class ShoppingListComponent implements OnInit{

  items: ShoppingItemDto[];
  shopId: string;

  constructor(
    private shoppingListService: ShoppingListService,
    private itemService: ItemService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe({
      next: params => {
        this.shopId = params.id;

        this.shoppingListService.getItemsWithShopId( this.shopId).subscribe({
          next: res => {
            this.items = res;
          },
          error: err => {
            console.error("Error finding items:", err);
          }
        });
      },
      error: error => {
        console.error("Error fetching parameters:", error);
      }
    });
  }

  details() {

  }

  deleteList() {
  }
}
