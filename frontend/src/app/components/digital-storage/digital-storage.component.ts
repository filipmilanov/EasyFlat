import { Component } from '@angular/core';
import {storageItemList} from "../../dtos/storageItem";

@Component({
  selector: 'app-digital-storage',
  templateUrl: './digital-storage.component.html',
  styleUrls: ['./digital-storage.component.scss']
})

export class DigitalStorageComponent {
  title = "Test Title"
  itemQuantity = 50
  itemExpirationDate  = "Test Date"
  items: storageItemList[] = [];


  constructor() {
    // Initialize the items array with test items
    this.initializeTestItems();
  }


  private initializeTestItems() {
    // Add test items to the items array
    this.items.push({
      title: 'Item 1',
      quantity: 10,
      maxQuantity: 100,
      expirationDate: '2023-12-31'
    });

    this.items.push({
      title: 'Item 2',
      quantity: 20,
      maxQuantity: 200,
      expirationDate: '2023-11-30'
    });

    // Add more test items as needed
  }
}
