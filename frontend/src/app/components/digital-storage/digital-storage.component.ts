import {Component} from '@angular/core';
import {StorageService} from "../../services/storage.service";
import {ItemSearchDto, StorageItemList} from "../../dtos/storageItemList";
import {OrderType} from "../../dtos/OrderType";


@Component({
  selector: 'app-digital-storage',
  templateUrl: './digital-storage.component.html',
  styleUrls: ['./digital-storage.component.scss']
})

export class DigitalStorageComponent {
  items: StorageItemList[] = [];
  itemsAIS: StorageItemList[] = [];
  searchParameters: ItemSearchDto = {alwaysInStock: false, orderBy: OrderType.PRODUCT_NAME};


  constructor(private storageService: StorageService) {

  }

  ngOnInit() {
    this.loadStorage();
  }

  public loadStorage() {

    this.storageService.getItems("1",this.searchParameters).subscribe({

        next: res => {
          console.log(this.searchParameters)
          console.log(res);
          this.items = res;
        },
        error: err => {
          console.error("Error loading storage:", err);
        }
      }
    )
    this.searchParameters.alwaysInStock = true;
    this.storageService.getItems("1",this.searchParameters).subscribe({

        next: res => {
          console.log(this.searchParameters)
          console.log(res);
          this.itemsAIS = res;
        },
        error: err => {
          console.error("Error loading storage:", err);
        }
      }
    )
  }


  protected readonly OrderType = OrderType;
}
