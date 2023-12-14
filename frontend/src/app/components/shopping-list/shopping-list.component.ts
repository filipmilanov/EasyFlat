import {Component, ElementRef, NgIterable, OnInit} from '@angular/core';
import {DefaultGlobalConfig, ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {ShoppingItemDto, ShoppingItemSearchDto} from "../../dtos/item";
import {ItemService} from "../../services/item.service";
import {ShoppingListService} from "../../services/shopping-list.service";
import {ShoppingListDto} from "../../dtos/shoppingList";
import {SharedFlat} from "../../dtos/sharedFlat";
import {Observable} from "rxjs";

@Component({
  selector: 'app-shopping-list',
  templateUrl: './shopping-list.component.html',
  styleUrls: ['./shopping-list.component.scss']
})
export class ShoppingListComponent implements OnInit {

  shoppingList: ShoppingListDto = {
    id: 0,
    listName: ''
  };
  items: ShoppingItemDto[] = [];
  shopId: string;
  checkedItems: ShoppingItemDto[] = this.getCheckedItems();
  selectedShoppingList: string = 'Default';
  shoppingLists: ShoppingListDto[] = [];
  searchParams: ShoppingItemSearchDto = {
    productName: '',
    label: '',
  }

  constructor(
    private shoppingListService: ShoppingListService,
    private itemService: ItemService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {

    this.checkedItems = this.getCheckedItems();
    console.log('Checked Items:', this.checkedItems);
    this.shoppingListService.getShoppingLists().subscribe({
        next: res => {
          this.shoppingLists = res
          if (this.selectedShoppingList == 'Default') {
            this.shoppingList = res[0];
          } else {
            for (let i = 0; i < this.shoppingLists.length; i++) {
              if (this.shoppingLists[i].listName === this.selectedShoppingList) {
                this.shoppingList = this.shoppingLists[i];
                break;
              }
            }
          }
          this.shopId = this.shoppingList.id + '';
          this.getItems();
        }
      }
    );

  }

  getItems() {
    this.shoppingListService.getItemsWithShopId(this.shopId, this.searchParams).subscribe({
      next: res => {
        this.items = res;
        console.log(this.items)
      },
      error: err => {
        console.error("Error finding items:", err);
      }
    });
  }

  navigateToCreateItem() {
    this.router.navigate(['shopping-list', this.shoppingList.listName, 'item', 'create']);
  }

  navigateToCreateList() {
    this.router.navigate(['shopping-list', this.shoppingList.listName, 'list', 'create']);
  }

  deleteItem(itemId: number) {
    if (confirm("Are you sure you want to delete this item?")) {
      this.shoppingListService.deleteItem(itemId).subscribe({
        next: (deletedItem: ShoppingItemDto) => {
          console.log(deletedItem.generalName, ' was deleted form the list');
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
          this.router.navigate(['shopping-list' + this.shopId]);
        },
        error: error => {
          console.error(error.message, error);
        }
      });
    }
  }

  updateCheckedItems(item: ShoppingItemDto) {
    item.check = !item.check;
    this.checkedItems = this.getCheckedItems();
    console.log('Checked Items:', this.checkedItems);
  }

  getCheckedItems(): ShoppingItemDto[] {
    return this.items.filter(item => item.check);
  }

  deleteCheckedItems() {
    const checkedItems = this.checkedItems.slice();

    if (checkedItems.length === 0) {
      this.notification.error('No items are checked for deletion.');
      return;
    }

    if (confirm("Are you sure you want to delete the checked items?")) {
      checkedItems.forEach(item => {
        this.shoppingListService.deleteItem(item.itemId).subscribe({
          next: (deletedItem: ShoppingItemDto) => {
            console.log(deletedItem.generalName, ' was deleted from the list');

            this.items = this.items.filter(listItem => listItem.itemId !== deletedItem.itemId);

            this.checkedItems = this.getCheckedItems();
            console.log('Checked Items:', this.checkedItems);
          },
          error: error => {
            console.error(error.message, error);
          }
        });
      });
    }
  }

  onShoppingListChange() {
    console.log('Selected Shopping List:', this.selectedShoppingList);
    if (this.selectedShoppingList) {
      this.router.navigate(['/shopping-list', this.selectedShoppingList]);
    }
  }

  transferToStorage() {
    this.shoppingListService.transferToStorage(this.checkedItems).subscribe({
        next: data => {
          this.router.navigate([`/digital-storage/1`])
        }
      }
    );
  }

  checkIsEmpty() {
    return this.checkedItems.length == 0;
  }

  checkId() {
    return this.shoppingList.listName != "Default";
  }

  navigateToEditItem(itemId: string) {
    this.router.navigate(['shopping-list', this.shopId, 'item', itemId, 'edit']);
  }

}
