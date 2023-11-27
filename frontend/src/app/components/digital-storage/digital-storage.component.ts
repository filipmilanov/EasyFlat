import {Component} from '@angular/core';
import {StorageService} from "../../services/storage.service";
import {ItemSearchDto, StorageItemList} from "../../dtos/storageItemList";


@Component({
  selector: 'app-digital-storage',
  templateUrl: './digital-storage.component.html',
  styleUrls: ['./digital-storage.component.scss']
})

export class DigitalStorageComponent {
  items: StorageItemList[] = [];
  searchParameters:ItemSearchDto = {};


  constructor(private storageService: StorageService) {

  }

  ngOnInit() {
    this.loadStorage();
  }

  public loadStorage() {
    console.log(this.searchParameters)
    this.storageService.getItems("1",this.searchParameters).subscribe({

        next: res => {
          console.log(this.searchParameters)
          this.items = res;
        },
        error: err => {
          console.error("Error loading storage:", err);
        }
      }
    )
  }


}
