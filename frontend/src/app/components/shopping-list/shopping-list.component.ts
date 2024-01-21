import {Component, ElementRef, NgIterable, OnInit} from '@angular/core';
import {DefaultGlobalConfig, ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {ItemDto, ShoppingItemDto, ShoppingItemSearchDto} from "../../dtos/item";
import {ItemService} from "../../services/item.service";
import {ShoppingListService} from "../../services/shopping-list.service";
import {ShoppingListDto} from "../../dtos/shoppingList";
import {SharedFlat} from "../../dtos/sharedFlat";
import {Observable} from "rxjs";
import {forEach} from "lodash";

@Component({
  selector: 'app-shopping-list',
  templateUrl: './shopping-list.component.html',
  styleUrls: ['./shopping-list.component.scss']
})
export class ShoppingListComponent implements OnInit {

  shoppingList: ShoppingListDto = {
    id: 0,
    name: '',
    itemsCount: 0
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
                let firstBracket = error.error.indexOf('[');
                let lastBracket = error.error.indexOf(']');
                let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
                let errorDescription = error.error.substring(0, firstBracket);
                errorMessages.forEach(message => {
                  this.notification.error(message, errorDescription);
                });
              }
            });
          },
          error: error => {
            let firstBracket = error.error.indexOf('[');
            let lastBracket = error.error.indexOf(']');
            let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
            let errorDescription = error.error.substring(0, firstBracket);
            errorMessages.forEach(message => {
              this.notification.error(message, errorDescription);
            });
          }
        });
      },
      error: error => {
        let firstBracket = error.error.indexOf('[');
        let lastBracket = error.error.indexOf(']');
        let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
        let errorDescription = error.error.substring(0, firstBracket);
        errorMessages.forEach(message => {
          this.notification.error(message, errorDescription);
        });
      }
    });

    this.checkedItems = this.getCheckedItems();
  }

  getItems() {
    this.shoppingListService.getItemsWithShopId(this.shopId, this.searchParams).subscribe({
      next: res => {
        this.items = res;
        console.log(this.items)
      },
      error: error => {
        let firstBracket = error.error.indexOf('[');
        let lastBracket = error.error.indexOf(']');
        let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
        let errorDescription = error.error.substring(0, firstBracket);
        errorMessages.forEach(message => {
          this.notification.error(message, errorDescription);
        });
      }
    });
  }

  navigateToCreateItem() {
    this.router.navigate([this.baseUri, this.shoppingList.id, 'item', 'create']);
  }


  deleteList() {
    if (confirm("Are you sure you want to delete this list?")) {
      console.log(this.shopId)
      this.shoppingListService.deleteList(this.shopId).subscribe({
        next: (deletedList: ShoppingListDto) => {
          this.router.navigate(['shopping-lists']);
          this.notification.success(deletedList.name + " was successfully deleted.", "Success");
        },
        error: error => {
          let firstBracket = error.error.indexOf('[');
          let lastBracket = error.error.indexOf(']');
          let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
          let errorDescription = error.error.substring(0, firstBracket);
          errorMessages.forEach(message => {
            this.notification.error(message, errorDescription);
          });
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
      this.notification.info('Please, mark the items you want to delete');
      return;
    }

    if (confirm("Are you sure you want to delete the checked items?")) {
      checkedItems.forEach(item => {
        this.shoppingListService.deleteItem(item.itemId).subscribe({
          next: (deletedItem: ShoppingItemDto) => {
            this.notification.success(deletedItem.productName + " was successfully deleted from the list", "Success");
            this.items = this.items.filter(listItem => listItem.itemId !== deletedItem.itemId);
            this.checkedItems = this.getCheckedItems();
            console.log('Checked Items:', this.checkedItems);
          },
          error: error => {
            let firstBracket = error.error.indexOf('[');
            let lastBracket = error.error.indexOf(']');
            let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
            let errorDescription = error.error.substring(0, firstBracket);
            errorMessages.forEach(message => {
              this.notification.error(message, errorDescription);
            });
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
        },
        error: err => {
          this.notification.error('Items were not added to the storage', err)
        }
      }
    );
  }

  checkIsEmpty() {
    return this.checkedItems.length == 0;
  }

  checkId() {
    return this.shoppingList.name == "Shopping List (Default)";
  }

  navigateToEditItem(itemId: string) {
    this.router.navigate([this.baseUri, this.shopId, 'item', itemId, 'edit']);
  }

  getIdFormatForDeleteModal(item: ItemDto): string {
    return `${item.productName}${item.itemId.toString()}`.replace(/[^a-zA-Z0-9]+/g, '');
  }

  getIdFormatForDeleteModalForList(shoppingList: ShoppingListDto) {
    return `${shoppingList.name}${shoppingList.id.toString()}`.replace(/[^a-zA-Z0-9]+/g, '');
  }

  getIdFormatForDeleteModalForChecked(checkedItems: ShoppingItemDto[]) {
    return checkedItems.toString().replace(/[^a-zA-Z0-9]+/g, '');
  }
}
