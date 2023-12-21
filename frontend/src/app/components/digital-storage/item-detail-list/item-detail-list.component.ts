import {Component, ElementRef, HostListener, OnInit} from '@angular/core';
import {StorageService} from "../../../services/storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ItemDto} from "../../../dtos/item";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";
import {parseInt} from "lodash";
import {ShoppingListService} from "../../../services/shopping-list.service";

@Component({
  selector: 'app-item-detail-list',
  templateUrl: './item-detail-list.component.html',
  styleUrls: ['./item-detail-list.component.scss']
})
export class ItemDetailListComponent implements OnInit {
  itemGeneralName: string;
  items: ItemDto[];
  storId: string;
  hashMap = new Map<number, boolean[]>();

  constructor(private storageService: StorageService,
              private router: Router,
              private route: ActivatedRoute,
              private itemService: ItemService,
              private el: ElementRef,
              private notification: ToastrService,
              private shoppingService: ShoppingListService) {
  }

  ngOnInit() {
    this.route.params.subscribe({
      next: params => {
        this.storId = params.id;
        this.itemGeneralName = params.name;


        this.itemService.findByDigitalStorageAndGeneralName(this.itemGeneralName).subscribe({
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

  checkModal(id: number, mode: number): boolean {
    if (mode == 0) {
      return this.hashMap.get(id)[0];
    } else {
      return this.hashMap.get(id)[1];
    }
  }

  toggleCustomModalSubtract(id: number) {
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

  toggleCustomModalAdd(id: number) {
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

  onSave(id: number, quantity: number, mode: number) {

    if (quantity < 0) {
      console.error('Invalid input. Please enter a valid number.');
      return;
    }

    let item: ItemDto;
    this.itemService.getById(id).subscribe({
      next: res => {
        item = res;

        let quantityCurrent: number;
        let quantityTotal: number;
        if (mode == 0) { // Subtract
          quantityCurrent = item.quantityCurrent - quantity;
          quantityTotal = item.quantityTotal;

          this.hashMap.get(id)[0] = false;
        } else { // mode == 1, Add
          quantityCurrent = item.quantityCurrent + quantity;
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
            this.delete(id);
          }
        } else {
          item.quantityCurrent = quantityCurrent;
          item.quantityTotal = quantityTotal;
          console.log(item)
          this.itemService.updateItem(item).subscribe({
            next: res => {
              for (let i = 0; i < this.items.length; i++) {
                if (res.itemId == this.items[i].itemId) {
                  this.items[i].quantityCurrent = res.quantityCurrent;
                  this.items[i].quantityTotal = res.quantityTotal;
                  break;
                }
              }

            },
            error: error => {
              console.error(`Item's quantity could not be changed: ${error.error.message}`);
              this.notification.error(error.error.message);
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
        this.notification.success(`Item ${itemId} was successfully deleted`, "Success");

        if (this.items.length == 1) {
          this.router.navigate([`/digital-storage/${this.storId}`]);
        } else {
          let j = 0;
          let arr: ItemDto[] = new Array<ItemDto>(this.items.length - 1);
          for (let i = 0; i < this.items.length; i++) {
            if (itemId != this.items[i].itemId) {
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
        this.notification.error(`Item ${itemId} could not be deleted`, "Error");
      }
    });
  }

  addToShoppingList(itemId: number) {
    let item: ItemDto;
    this.itemService.getById(itemId).subscribe({
      next: res => {
        item = res;

        this.storageService.addItemToShoppingList(item).subscribe({
            next: data => {
              this.notification.success(`Item ${itemId} successfully added to the shopping list.`);
              this.shoppingService.getShoppingListByName('Default').subscribe({
                next: res => {
                  this.router.navigate([`/shopping-list/` + res.listName]);
                }
              })
            },
            error: error => {
              console.error(`Item could not be added to the shopping list: ${error.error.message}`);
              this.notification.error(`Item ${itemId} could not be added to the shopping list`, "Error");
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
