import {Component, OnInit} from '@angular/core';
import {ItemDto} from "../../../dtos/item";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-item-detail',
  templateUrl: './item-detail.component.html',
  styleUrls: ['./item-detail.component.scss']
})
export class ItemDetailComponent implements OnInit {
  item: ItemDto = {
    itemId: '0',
    ean: "",
    generalName: "",
    productName: "",
    brand: "",
    quantityCurrent: 0,
    quantityTotal: 0,
    unit: "",
    expireDate: new Date(),
    description: "",
    priceInCent: 0,
    alwaysInStock: false,
    addToFiance: false,
    ingredients: [{}],
  }

  constructor(
    private service: ItemService,
    private notification: ToastrService,
    private router: Router,
    private route: ActivatedRoute,
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe({
      next: params => {
        const itemId = params.id;
        this.service.getById(itemId).subscribe({
          next: res => {
            this.item = res;
          },
          error: error => {
            console.error(`Item could not be retrieved from the backend: ${error.error.message}`);
            this.router.navigate(['/digital-storage/1']);
            this.notification.error('Item could not be retrieved', "Error");
          }
        })
      },
      error: error => {
        console.error(`Item could not be retrieved using the ID from the URL: ${error.error.message}`);
        this.router.navigate(['/digital-storage/1']);
        this.notification.error('Item could not be retrieved using ID from URL', "Error");
      }
    });
  }

  get itemPrice(): string {
    return "€ " + (this.item.priceInCent / 100).toFixed(2).toString();
  }

  public delete() {
    this.service.deleteItem(parseInt(this.item.itemId)).subscribe({
      next: data => {
        this.router.navigate(['/digital-storage/1']);
        this.notification.success(`Item ${this.item.itemId} was successfully deleted`, "Success");
      },
      error: error => {
        console.error(`Item could not be deleted: ${error.error.message}`);
        this.router.navigate(['/digital-storage/1']);
        this.notification.error(error.error.message);
        this.notification.error(`Item ${this.item.itemId} could not be deleted`, "Error");
      }
    });
  }

}
