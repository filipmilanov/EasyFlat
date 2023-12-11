import {Component, ElementRef, OnInit} from '@angular/core';
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {ShoppingItemDto} from "../../dtos/item";
import {ItemService} from "../../services/item.service";
import {ShoppingListService} from "../../services/shopping-list.service";
import {ShoppingListDto} from "../../dtos/shoppingList";

@Component({
  selector: 'app-shopping-list',
  templateUrl: './shopping-list.component.html',
  styleUrls: ['./shopping-list.component.scss']
})
export class ShoppingListComponent implements OnInit {

  shoppingList: ShoppingListDto = {
    id: 0,
    listName: '',
    items: []};
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

        this.shoppingListService.getItemsWithShopId(this.shopId).subscribe({
          next: res => {
            this.shoppingList.items = res;
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

  navigateToCreateItem() {
    this.router.navigate(['shopping-list', this.shopId, 'item', 'create']);
  }

  navigateToCreateList() {
    this.router.navigate(['shopping-list', this.shopId, 'list', 'create']);
  }

  deleteItem() {
    this.shoppingListService.deleteItem();
  }
}
