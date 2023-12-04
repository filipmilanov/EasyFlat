import {Component, OnInit} from '@angular/core';
import {StorageItem, StorageItemListDto} from "../../../dtos/storageItem";
import {StorageService} from "../../../services/storage.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-item-detail-list',
  templateUrl: './item-detail-list.component.html',
  styleUrls: ['./item-detail-list.component.scss']
})
export class ItemDetailListComponent implements OnInit {
  item: StorageItemListDto;
  itemGeneralName: string;
  items: StorageItem[];


  constructor(private storageService: StorageService, private router: Router,
              private route: ActivatedRoute,) {

  }

  ngOnInit() {


    this.route.params.subscribe({
      next: params => {
        const storId = params.id;
        this.itemGeneralName = params.name;


        this.storageService.getItemsWithGenaralName(this.itemGeneralName, storId).subscribe({
          next: res => {
            this.items = res;
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
}
