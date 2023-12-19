import {Component, ElementRef, HostListener, OnInit} from '@angular/core';
import {StorageItem} from "../../../dtos/storageItem";
import {StorageService} from "../../../services/storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ItemDto, ShoppingItemDto} from "../../../dtos/item";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";
import {parseInt} from "lodash";
import {Observable} from "rxjs";
import {dateComparator} from "@ng-bootstrap/ng-bootstrap/datepicker/datepicker-tools";
import {ShoppingListService} from "../../../services/shopping-list.service";

@Component({
  selector: 'app-item-detail-list',
  templateUrl: './item-detail-list.component.html',
  styleUrls: ['./item-detail-list.component.scss']
})
export class ItemDetailListComponent implements OnInit {
  itemGeneralName: string;
  items: StorageItem[];
  storId: string;
  hashMap = new Map<string, boolean[]>();

  constructor(private storageService: StorageService, private router: Router,
              private route: ActivatedRoute, private itemService: ItemService, private el: ElementRef,
              private notification: ToastrService, private shoppingService: ShoppingListService) {
  }

  ngOnInit() {
    this.route.params.subscribe({
      next: params => {
        this.storId = params.id;
        this.itemGeneralName = params.name;


        this.storageService.getItemsWithGenaralName(this.itemGeneralName).subscribe({
          next: res => {
            this.items = res;
            console.log(this.items)
            for (let i = 0; i < this.items.length; i++) {
              let modalArr: boolean[] = [false, false];
              this.hashMap.set(this.items[i].itemId, modalArr)
            }
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

  checkModal(id: string, mode: number): boolean {
    if (mode == 0) {
      return this.hashMap.get(id)[0];
    } else {
      return this.hashMap.get(id)[1];
    }
  }

  toggleCustomModalSubtract(id: string) {
    this.hashMap.get(id)[0] = !this.hashMap.get(id)[0];
    if (this.hashMap.get(id)[0] == true) {
      this.hashMap.get(id)[1] = false;
    }
    this.hashMap.forEach((value, key) => {
      if (key != id) {
        this.hashMap.set(key, [false, false]);
      }
    });
  }

  toggleCustomModalAdd(id: string) {
    this.hashMap.get(id)[1] = !this.hashMap.get(id)[1];
    if (this.hashMap.get(id)[1] == true) {
      this.hashMap.get(id)[0] = false;
    }
    this.hashMap.forEach((value, key) => {
      if (key != id) {
        this.hashMap.set(key, [false, false]);
      }
    });
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.el.nativeElement.contains(event.target)) {
      this.hashMap.forEach((value, key) => {
        this.hashMap.set(key, [false, false]);
      });
    }
  }

  onSave(id: string, value: string, mode: number) {
    const quantity = parseFloat(value);

    if (isNaN(quantity)) {
      console.error('Invalid input. Input should be of type number');
      this.notification.error('Please enter a valid number.');
      return;
    } else if (quantity < 0) {
      console.error('Invalid input. Input should be positive number');
      this.notification.error('Please enter a positive number.');
      return;
    } else if (quantity > 10000) {
      console.error('Invalid input. Number can not be larger than 10.000');
      this.notification.error('Please enter a positive number less than 10.000.');
      return;
    }

    let currId: string;
    for (let i = 0; i < this.items.length; i++) {
      currId = this.items[i].itemId;
      if (id == currId) {

        break;
      }
    }

    let item: ItemDto;
    this.itemService.getById(parseInt(id)).subscribe({
      next: res => {
        item = res;

        let quantityCurrent: number;
        let quantityTotal: number;
        if (mode == 0) { // Subtract
          quantityCurrent = item.quantityCurrent - parseFloat(value);
          quantityTotal = item.quantityTotal;

          this.hashMap.get(id)[0] = false;
        } else { // mode == 1, Add
          quantityCurrent = item.quantityCurrent + parseFloat(value);
          if (quantityCurrent > item.quantityTotal) {
            quantityTotal = quantityCurrent;
          } else {
            quantityTotal = item.quantityTotal;
          }

          this.hashMap.get(id)[1] = false;
        }

        if (quantityCurrent < 1) {
          console.log(item)
          if (confirm("The item will be deleted from the storage. Are you sure you want to proceed?")) {
            this.delete(parseInt(id));
          }
        } else {
          item.quantityCurrent = quantityCurrent;
          item.quantityTotal = quantityTotal;
          console.log(item)
          this.itemService.updateItem(item).subscribe({
            next: res => {
              let currId: string;
              for (let i = 0; i < this.items.length; i++) {
                currId = this.items[i].itemId;
                if (id == currId) {
                  this.items[i].quantityCurrent = res.quantityCurrent;
                  this.items[i].quantityTotal = res.quantityTotal;
                  break;
                }
              }

            },
            error: error => {
              console.error(`Item's quantity could not be changed: ${error.error.message}`);
              this.notification.error(`Item could not be deleted`, "Error");
            }
          });
        }

      },
      error: err => {
        console.error("Error finding item:", err);
      }
    });
  }

  public delete(itemId: number) {
    this.itemService.deleteItem(itemId).subscribe({
      next: data => {
        this.notification.success(`Item was successfully deleted`, "Success");

        if (this.items.length == 1) {
          this.router.navigate([`/digital-storage`]);
        } else {
          let j = 0;
          let arr: StorageItem[] = new Array<StorageItem>(this.items.length - 1);
          let currId: string;
          for (let i = 0; i < this.items.length; i++) {
            currId = this.items[i].itemId;
            if (itemId != parseInt(currId)) {
              arr[j] = this.items[i];
              j++;
            }
          }
          this.items = arr;
        }

      },
      error: error => {
        console.error(`Item could not be deleted: ${error.error.message}`);
        this.notification.error(error.error.message);
        this.notification.error(`Item could not be deleted`, "Error");
      }
    });
  }

  addToShoppingList(itemId: string) {
    let item: ItemDto;
    this.itemService.getById(parseInt(itemId)).subscribe({
      next: res => {
        item = res;

        this.storageService.addItemToShoppingList(item).subscribe({
            next: data => {
              this.notification.success(`Item successfully added to the shopping list.`);
              this.shoppingService.getShoppingListByName('Default').subscribe({
                next: res => {
                  this.router.navigate([`/shopping-list/` + res.id]);
                }
              })
            },
            error: error => {
              console.error(`Item could not be added to the shopping list: ${error.error.message}`);
              this.notification.error(`Item could not be added to the shopping list`, "Error");
            }

          },
        );
      },
      error: error => {
        console.error(`Error finding item: ${error.error.message}`);
        this.notification.error(error.error.message);
        this.notification.error(`Item with ID: ${itemId} could not be found`, "Error");
      }
    });


  }

  protected readonly parseInt = parseInt;
}
