import { Component } from '@angular/core';
import {StorageService} from "../../services/storage.service";
import {StorageItemList} from "../../dtos/storageItemList";


@Component({
  selector: 'app-digital-storage',
  templateUrl: './digital-storage.component.html',
  styleUrls: ['./digital-storage.component.scss']
})

export class DigitalStorageComponent {
  title = "Test Title"
  itemQuantity = 50
  itemExpirationDate  = "Test Date"
  items: StorageItemList[] = [];


  constructor(private storageService:StorageService) {

    this.initializeTestItems();
  }


  public loadStorage(){
    console.log("loadStorage")
  this.storageService.getItems("1").subscribe({

    next: res => {
      this.items = res;
    },
    error: err => {

    }
    }

  )
  }

  private initializeTestItems() {
    // Add test items to the items array
    this.items.push({
      description: "", ean: "", generalName: "", priceInCent: 0, unit: "",
      brand: "", expireDate: "", itemId: 0, productName: "", quantityCurrent: 0, quantityTotal: 0

    });

    this.items.push({
      description: "", ean: "", generalName: "", priceInCent: 0, unit: "",
      brand: "", expireDate: "", itemId: 0, productName: "", quantityCurrent: 0, quantityTotal: 0

    });

    // Add more test items as needed
  }


}
