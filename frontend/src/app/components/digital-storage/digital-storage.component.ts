import {Component} from '@angular/core';
import {StorageService} from "../../services/storage.service";
import {StorageItemList} from "../../dtos/storageItemList";


@Component({
  selector: 'app-digital-storage',
  templateUrl: './digital-storage.component.html',
  styleUrls: ['./digital-storage.component.scss']
})

export class DigitalStorageComponent {
  items: StorageItemList[] = [];


  constructor(private storageService: StorageService) {

  }

  ngOnInit() {
    this.loadStorage();
  }

  public loadStorage() {
    console.log("loadStorage")
    this.storageService.getItems("1").subscribe({

        next: res => {
          this.items = res;
        },
        error: err => {
          console.error("Error loading storage:", err);
        }
      }
    )
  }


}
