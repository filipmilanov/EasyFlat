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


}
