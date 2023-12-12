import {Component, ElementRef, OnInit} from '@angular/core';
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {ShoppingItemDto} from "../../dtos/item";
import {ItemService} from "../../services/item.service";
import {ShoppingListService} from "../../services/shopping-list.service";
import {ShoppingListDto} from "../../dtos/shoppingList";
import {SharedFlat} from "../../dtos/sharedFlat";

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

  allLists: ShoppingListDto[];

  location: Location;


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

  deleteItem(itemId: number) {
    if (confirm("Are you sure you want to delete this item?")) {
      this.shoppingListService.deleteItem(itemId).subscribe({
        next: (deletedItem: ShoppingItemDto) => {
          console.log( deletedItem.generalName, ' was deleted form the list');
          this.ngOnInit();
        },
        error: error => {
          console.error(error.message, error);
        }
      });
    }
  }

  deleteList() {
    if (confirm("Are you sure you want to delete this list?")) {
      this.shoppingListService.deleteList(this.shopId).subscribe({
        next: (deletedList: ShoppingListDto) => {
          console.log(deletedList.listName, ' was deleted successfully');
          this.router.navigate(['shopping-list/2']);
        },
        error: error => {
          console.error(error.message, error);
        }
      });
    }
  }

}
