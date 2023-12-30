import {Component, OnInit} from '@angular/core';
import {StorageService} from "../../../services/storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ItemDto} from "../../../dtos/item";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";
import {ShoppingListService} from "../../../services/shopping-list.service";

export enum QuantityChange {
  INCREASE = "increased",
  DECREASE = "decreased"
}

@Component({
  selector: 'app-item-detail-list',
  templateUrl: './item-detail-list.component.html',
  styleUrls: ['./item-detail-list.component.scss']
})
export class ItemDetailListComponent implements OnInit {
  itemGeneralName: string;
  items: ItemDto[];
  quantityInputs: { [itemId: number]: number } = {};

  constructor(private storageService: StorageService,
              private router: Router,
              private route: ActivatedRoute,
              private itemService: ItemService,
              private notification: ToastrService,
              private shoppingService: ShoppingListService) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe({
      next: paramMap => {
        const generalName = paramMap.get('name');
        this.itemGeneralName = generalName
        this.itemService.findByGeneralName(generalName).subscribe({
          next: res => {
            if (res.length === 0) {
              this.router.navigate(['/digital-storage/']);
              this.notification.error(`Items of type ${generalName} could not be loaded`, "Error");
            } else {
              this.items = res;
            }
          },
          error: error => {
            console.error(`Items of type ${generalName} could not be loaded: ${error}`);
            this.router.navigate(['/digital-storage/']);
            this.notification.error(`Items of type ${generalName} could not be loaded`, "Error");
          }
        })
      },
      error: error => {
        console.error(`Item could not be retrieved using the ID from the URL: ${error.error.message}`);
        this.router.navigate(['/digital-storage/']);
        this.notification.error(`Items could not be loaded`, "Error");
      }
    });
  }

  public changeItemQuantity(item: ItemDto, quantityInput: number, mode: QuantityChange): void {
    const previousCurrentQuantity: number = item.quantityCurrent;
    const previousTotalQuantity: number = item.quantityTotal;

    if (quantityInput < 0.01 ) {
      this.notification.error("Only numbers greater than 0 can be entered", "Error");

      return;
    }

    if (quantityInput != null) {

      const numberInputAsString: string = quantityInput.toString();
      const decimalIndex: number = numberInputAsString.indexOf('.');

      // check if number has at most 2 decimal places
      if (decimalIndex !== -1 && numberInputAsString.length - decimalIndex - 1 > 2) {
        this.notification.error("Only numbers with at most 2 decimal places can be entered", "Error");
        this.quantityInputs[item.itemId] = null;
        return;
      }

      // necessary to ensure that we only work with 2 decimal places
      quantityInput = parseFloat(quantityInput.toFixed(2));

      let isQuantityUpdated: boolean = false;

      if (mode === QuantityChange.INCREASE) {
        const newQuantity: number = item.quantityCurrent + quantityInput;
        if (item.quantityCurrent !== newQuantity) {
          item.quantityCurrent = parseFloat(newQuantity.toFixed(2));
          isQuantityUpdated = true;
        }

      } else if (mode === QuantityChange.DECREASE) {
        const newQuantity: number = Math.max(0, item.quantityCurrent - quantityInput);
        if (item.quantityCurrent !== newQuantity) {
          item.quantityCurrent = parseFloat(newQuantity.toFixed(2));
          isQuantityUpdated = true;
        }
      }

      if (item.quantityCurrent > item.quantityTotal) {
        item.quantityTotal = item.quantityCurrent;
      }

      if (isQuantityUpdated) {
        this.itemService.updateItem(item).subscribe({
          next: () => {
            this.notification.success(`Item ${item.productName} was successfully ${mode} by ${quantityInput}`, "Success");
          },
          error: error => {
            item.quantityCurrent = previousCurrentQuantity;
            item.quantityTotal = previousTotalQuantity;
            this.quantityInputs[item.itemId] = null;
            console.error(`Item could not be deleted: ${error}`);
            this.notification.error(`Item ${item.productName} could not be ${mode} by ${quantityInput}`, "Error");

          }
        });
      } else {
        this.notification.info("The quantity is the same as before", "Info")
      }
    } else {
      this.notification.error("The quantity you provided is not valid!", "Error");
    }
  }

  public delete(item: ItemDto): void {
    this.itemService.deleteItem(item.itemId).subscribe({
      next: () => {
        this.router.navigate(['/digital-storage/']);
        this.notification.success(`Item ${item.generalName} was successfully deleted`, "Success");
      },
      error: error => {
        console.error(`Item could not be deleted: ${error}`);
        this.router.navigate(['/digital-storage/']);
        this.notification.error(`Item ${item.generalName} could not be deleted`, "Error");
      }
    });
  }

  public showExpiryStatus(expireDate: Date): string {
    let today: Date = new Date();
    let expiry: Date = new Date(expireDate);
    let differenceInDays: number = (expiry.getTime() - today.getTime()) / (1000 * 3600 * 24);

    if (differenceInDays < 0) {
      return 'bi bi-x-circle-fill text-danger'; // Has already expired
    } else if (differenceInDays < 3) {
      return 'bi bi-exclamation-triangle-fill text-warning'; // Expiring soon
    }
    return '';
  }

  public addToShoppingList(item: ItemDto): void {
    this.storageService.addItemToShoppingList(item).subscribe({
      next: () => {
        this.notification.success(`Item ${item.productName} successfully added to the shopping list.`, "Success");
        this.shoppingService.getShoppingListByName('Default').subscribe({
          next: res => {
            this.router.navigate([`/shopping-list/default`]);
          }
        })
      },
      error: error => {
        console.error(`Item could not be added to the shopping list: ${error}`);
        this.notification.error(`Item ${item.productName} could not be added to the shopping list`, "Error");
      }
    });
  }

  protected readonly QuantityChange = QuantityChange;
}
