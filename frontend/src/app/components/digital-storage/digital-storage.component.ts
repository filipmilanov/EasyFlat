import {Component, OnInit} from '@angular/core';
import {StorageService} from "../../services/storage.service";
import {ItemSearchDto, StorageItem, StorageItemListDto} from "../../dtos/storageItem";
import {OrderType} from "../../dtos/orderType";
import {ToastrService} from "ngx-toastr";


@Component({
  selector: 'app-digital-storage',
  templateUrl: './digital-storage.component.html',
  styleUrls: ['./digital-storage.component.scss']
})

export class DigitalStorageComponent implements OnInit {
  items: StorageItemListDto[] = [];
  itemsAIS: StorageItemListDto[] = [];
  searchParameters: ItemSearchDto = {alwaysInStock: false, orderBy: OrderType.PRODUCT_NAME, fillLevel: ''};


  constructor(private storageService: StorageService,
              private notification: ToastrService,) {

  }

  ngOnInit() {
    this.loadStorage();
  }

  public loadStorage() {
    this.searchParameters.alwaysInStock = false;
    this.storageService.getItems(this.searchParameters).subscribe({

        next: res => {
          this.items = res;
        },
        error: err => {
          console.error("Error loading storage:", err);
          this.notification.error("Error loading storage");
        }
      }
    )
    this.searchParameters.alwaysInStock = true;
    this.storageService.getItems(this.searchParameters).subscribe({

        next: res => {

          this.itemsAIS = res;
        },
        error: err => {
          console.error("Error loading storage:", err);
          this.notification.error("Error loading storage");
        }
      }
    )
  }

  protected readonly OrderType = OrderType;
}
