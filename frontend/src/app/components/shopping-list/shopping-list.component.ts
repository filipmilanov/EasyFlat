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
    name: ''
  };
  items: ShoppingItemDto[] = [];
  shopId: string;
  checkedItems: ShoppingItemDto[] = this.getCheckedItems();
  selectedShoppingListId: number;
  shoppingLists: ShoppingListDto[] = [];
  searchParams: ShoppingItemSearchDto = {
    productName: '',
    label: '',
  }
  baseUri: string = 'shopping-lists/list';

  constructor(
    private shoppingListService: ShoppingListService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.shoppingListService.getShoppingLists('').subscribe({
      next: res => {
          this.shoppingLists = res;
        this.route.params.subscribe({
          next: params => {
            this.shopId = params.id;
            this.shoppingListService.getShoppingListById(this.shopId).subscribe({
              next: (res: ShoppingListDto) => {
                this.shoppingList = res;
                this.selectedShoppingListId = res.id;
                this.getItems();
              },
              error: (error: any) => {
                console.error('Error fetching shopping list:', error);
              }
            });
          },
          error: error => {
            console.error("Error fetching parameters:", error);
          }
        });
      },
      error: err => {
        console.error('Error fetching shopping lists');
      }
    });

    this.checkedItems = this.getCheckedItems();
  }

  getItems() {
    this.shoppingListService.getItemsWithShopId(this.shopId, this.searchParams).subscribe({
      next: res => {
        this.items = res;
      },
      error: err => {
        console.error("Error finding items:", err);
      }
    });
  }

  navigateToCreateItem() {
    this.router.navigate([this.baseUri, this.shoppingList.id, 'item', 'create']);
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
          console.log(deletedList.name, ' was deleted successfully');
          this.router.navigate(['shopping-lists', 'list' + this.shoppingList.id]);
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
    if (this.selectedShoppingListId) {
      this.shoppingListService.getShoppingListById(this.selectedShoppingListId + '').subscribe({
        next: res => {
          this.shoppingList = res;
          this.shopId = res.id + '';
          this.getItems();
          this.router.navigate([this.baseUri, this.shopId]);
        },
        error: err => {
          console.error('Error fetching shopping list:', err);
        }
      });
    }
  }

  transferToStorage() {
    this.shoppingListService.transferToStorage(this.checkedItems).subscribe({
        next: data => {
          this.notification.success(`Items successfully added to the storage.`);
          this.router.navigate([`/digital-storage`])
        }
      }
    );
  }

  checkIsEmpty() {
    return this.checkedItems.length == 0;
  }

  checkId() {
    return this.shoppingList.name == "Default";
  }

  navigateToEditItem(itemId: string) {
    this.router.navigate([this.baseUri, this.shopId, 'item', itemId, 'edit']);
  }

}
