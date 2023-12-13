import {Component, ElementRef, HostListener, OnInit} from '@angular/core';
import {StorageItem} from "../../../dtos/storageItem";
import {StorageService} from "../../../services/storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ItemDto} from "../../../dtos/item";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-item-detail-list',
  templateUrl: './item-detail-list.component.html',
  styleUrls: ['./item-detail-list.component.scss']
})
export class ItemDetailListComponent implements OnInit {
  itemGeneralName: string;
  items: StorageItem[];
  storId: string;
  hashMap= new Map<string, boolean[]>();

  constructor(private storageService: StorageService, private router: Router,
              private route: ActivatedRoute, private itemService: ItemService, private el: ElementRef,
              private notification: ToastrService) {
  }

  ngOnInit() {
    this.route.params.subscribe({
      next: params => {
        this.storId = params.id;
        this.itemGeneralName = params.name;


        this.storageService.getItemsWithGenaralName(this.itemGeneralName).subscribe({
          next: res => {
            this.items = res;

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

  toggleCustomModal(id: string) {
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

  toggleCustomModal1(id: string) {
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
    // Check if the clicked element is outside the card
    if (!this.el.nativeElement.contains(event.target)) {
      this.hashMap.forEach((value, key) => {
        this.hashMap.set(key, [false, false]);
      });
    }
  }

  onSave(id: string, value: string, mode: number) {
    const quantity = parseInt(value);

    if (isNaN(quantity) || quantity < 0) {
      console.error('Invalid input. Please enter a valid number.');
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
          quantityCurrent = item.quantityCurrent - parseInt(value);
          quantityTotal = item.quantityTotal;

          this.hashMap.get(id)[0] = false;
        } else { // mode == 1, Add
          quantityCurrent = item.quantityCurrent + parseInt(value);
          if (quantityCurrent > item.quantityTotal) {
            quantityTotal = quantityCurrent;
          } else {
            quantityTotal = item.quantityTotal;
          }

          this.hashMap.get(id)[1] = false;
        }

        if (quantityCurrent < 1) {
          console.log(item)
          this.delete(parseInt(id));

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
        this.notification.error(`Item ${itemId} could not be deleted`, "Error");
      }
    });
  }

  protected readonly parseInt = parseInt;
}
