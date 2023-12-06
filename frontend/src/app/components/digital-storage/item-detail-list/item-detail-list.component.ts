import {Component, ElementRef, HostListener, OnInit} from '@angular/core';
import {ItemSearchDto, StorageItem, StorageItemListDto} from "../../../dtos/storageItem";
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
  hashMap = new Map<string, boolean[]>();

  customModalOpen: boolean = false;
  customModalOpen1: boolean = false;

  constructor(private storageService: StorageService, private router: Router,
              private route: ActivatedRoute, private itemService: ItemService, private el: ElementRef,
              private notification: ToastrService) {
  }

  ngOnInit() {


    this.route.params.subscribe({
      next: params => {
        const storId = params.id;
        this.itemGeneralName = params.name;


        this.storageService.getItemsWithGenaralName(this.itemGeneralName, storId).subscribe({
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
    //this.customModalOpen = !this.customModalOpen;
    //if (this.customModalOpen == true) {
    //  this.customModalOpen1 = false;
    //}
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
    //this.customModalOpen1 = !this.customModalOpen1;
    //if (this.customModalOpen1 == true) {
    //  this.customModalOpen = false;
    //}
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    // Check if the clicked element is outside the card
    if (!this.el.nativeElement.contains(event.target)) {
      this.hashMap.forEach((value, key) => {
        this.hashMap.set(key, [false, false]);
      });
      //this.customModalOpen = false;
      //this.customModalOpen1 = false;
    }
  }

  onSave(id: string, value: string, mode: number) {
    const quantity = parseInt(value);

    if (isNaN(quantity) || quantity < 0) {
      console.error('Invalid input. Please enter a valid number.');
      return;
    }

    let currId: string;
    let currItem: ItemDto;
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
        if (mode == 0) { // Subtract
          quantityCurrent = item.quantityCurrent - parseInt(value);

          this.hashMap.get(id)[0] = false;
          //this.customModalOpen = false;
        } else { // mode == 1, Add
          quantityCurrent = item.quantityCurrent + parseInt(value);

          this.hashMap.get(id)[1] = false;
          //this.customModalOpen1 = false;
        }

        item.quantityCurrent = quantityCurrent;
        console.log(item)
        this.itemService.updateItem(item).subscribe({
          next: res => {
            let currId: string;
            for (let i = 0; i < this.items.length; i++) {
              currId = this.items[i].itemId;
              if (id == currId) {
                this.items[i].quantityCurrent = res.quantityCurrent;
                break;
              }
            }
          }
        });
      },
      error: err => {
        console.error("Error finding item:", err);
      }
    });

  }

  public delete(itemId: number) {
    this.itemService.deleteItem(itemId).subscribe({
      next: data => {
        this.router.navigate(['/digital-storage/1']);
        this.notification.success(`Item ${this.items[itemId]} was successfully deleted`, "Success");
      },
      error: error => {
        console.error(`Item could not be deleted: ${error.error.message}`);
        this.router.navigate(['/digital-storage/1']);
        this.notification.error(error.error.message);
        this.notification.error(`Item ${this.items[itemId]} could not be deleted`, "Error");
      }
    });
  }

  protected readonly parseInt = parseInt;
}
