import {Component, OnInit} from '@angular/core';
import {ItemDto} from "../../../dtos/item";
import {NgForm} from "@angular/forms";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";
import {DigitalStorageDto} from "../../../dtos/digitalStorageDto";
import {Observable, of} from "rxjs";
import {StorageService} from "../../../services/storage.service";
import {ActivatedRoute, Router} from "@angular/router";

export enum ItemCreateEditMode {
  create,
  edit,
}

@Component({
  selector: 'app-item-create-edit',
  templateUrl: './item-create-edit.component.html',
  styleUrls: ['./item-create-edit.component.scss']
})
export class ItemCreateEditComponent implements OnInit{

  mode: ItemCreateEditMode = ItemCreateEditMode.create;
  item: ItemDto = {
    alwaysInStock: false,
    addToFiance: false
  }
  priceInEuro: number = 0.00;


  constructor(
    private itemService: ItemService,
    private storageService: StorageService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case ItemCreateEditMode.create:
        return 'Create New Item';
      case ItemCreateEditMode.edit:
        return 'Edit Item';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case ItemCreateEditMode.create:
        return 'Create';
      case ItemCreateEditMode.edit:
        return 'Update';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === ItemCreateEditMode.create;
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case ItemCreateEditMode.create:
        return 'created';
      case ItemCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    if (this.mode === ItemCreateEditMode.edit) {
      this.route.params.subscribe({
        next: params => {
          const itemId = params.id;
          this.itemService.getById(itemId).subscribe({
            next: res => {
              this.item = res;
            },
            error: error => {
              console.error(`Item could not be retrieved from the backend: ${error}`);
              this.router.navigate(['/digital-storage/1']);
              this.notification.error('Item could not be retrieved', "Error");
            }
          })
        },
        error: error => {
          console.error(`Item could not be retrieved using the ID from the URL: ${error}`);
          this.router.navigate(['/digital-storage/1']);
          this.notification.error('No item provided for editing', "Error");
        }
      })
    }
  }

  public onSubmit(form: NgForm): void {
    this.item.priceInCent = this.priceInEuro * 100;
    console.log('is form valid?', form.valid, this.item);

    if (form.valid) {
      let observable: Observable<ItemDto>;
      switch (this.mode) {
        case ItemCreateEditMode.create:
          this.item.quantityCurrent = this.item.quantityTotal;
          observable = this.itemService.createItem(this.item);
          break;
        case ItemCreateEditMode.edit:
          observable = this.itemService.updateItem(this.item);
          break;
        default:
          console.error('Unknown ItemCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Item ${this.item.productName} successfully ${this.modeActionFinished} and added to the storage.`, "Success");
          this.router.navigate(['/digital-storage']);
        },
        error: error => {
          console.error(`Error item was not ${this.modeActionFinished}: ${error}`);
          console.error(error);
          let firstBracket = error.error.indexOf('[');
          let lastBracket = error.error.indexOf(']');
          let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
          let errorDescription = error.error.substring(0, firstBracket);
          errorMessages.forEach(message => {
            this.notification.error(message, errorDescription);
          });
        }
      });
    }
  }

  addIngredient(ingredient: string): void {
    if (ingredient == undefined || ingredient.length == 0) {
      return
    }
    if (this.item.ingredients === undefined) {
      this.item.ingredients = [{name: ingredient}];
    } else {
      this.item.ingredients.push({name: ingredient});
    }
  }

  removeIngredient(i: number) {
    this.item.ingredients.splice(i, 1);
  }

  validatePriceInput(event: any): void {
    let inputValue = event.target.value.replace(/[^0-9.]/g, '');
    event.target.value = this.formatPriceInEuroInput(inputValue);
    this.priceInEuro = parseFloat(inputValue);
  }

  formatPriceInEuroInput(value: string): string {
    return  value ? `${value} € ` : '';
  }

  formatStorageName(storage: DigitalStorageDto | null): string {
    return storage ? storage.title : '';
  }

  storageSuggestions = (input: string) => (input === '')
    ? of([])
    : this.storageService.findAll(input, 5);
}
