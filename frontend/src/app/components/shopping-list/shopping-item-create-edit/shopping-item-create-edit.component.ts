import {Component, OnInit} from '@angular/core';
import {NgForm} from "@angular/forms";
import {Observable, of} from "rxjs";
import {ItemDto, ShoppingItemDto} from "../../../dtos/item";
import {DigitalStorageDto} from "../../../dtos/digitalStorageDto";
import {ItemCreateEditMode} from "../../digital-storage/item-create-edit/item-create-edit.component";
import {ItemService} from "../../../services/item.service";
import {StorageService} from "../../../services/storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {ShoppingListService} from "../../../services/shopping-list.service";
import {ShoppingLabelDto} from "../../../dtos/shoppingLabel";

@Component({
  selector: 'app-item-create-edit',
  templateUrl: './shopping-item-create-edit.component.html',
  styleUrls: ['./shopping-item-create-edit.component.scss']
})
export class ShoppingItemCreateEditComponent implements OnInit {

  mode: ItemCreateEditMode = ItemCreateEditMode.create;
  item: ShoppingItemDto = {
    alwaysInStock: false,
    addToFiance: false
  }
  selectedLabelColor = '#ffffff';

  constructor(
    private itemService: ItemService,
    private storageService: StorageService,
    private shoppingService: ShoppingListService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case ItemCreateEditMode.create:
        return 'Add New Item';
      case ItemCreateEditMode.edit:
        return 'Add Additional Info';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case ItemCreateEditMode.create:
        return 'Add';
      case ItemCreateEditMode.edit:
        return 'Update';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === ItemCreateEditMode.create;
  }

  get modeIsEdit(): boolean {
    return this.mode === ItemCreateEditMode.edit;
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case ItemCreateEditMode.create:
        return 'added';
      case ItemCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
      this.route.params.subscribe(params => {
        // Extract the 'id' parameter from the route
        const shoppingListId = params['id'];
        this.shoppingService.getShoppingListById(shoppingListId).subscribe({
          next: res => {
            this.item.shoppingList = res;
          }
        });

      });
    });

    if (this.mode === ItemCreateEditMode.edit) {
      this.route.params.subscribe({
        next: params => {
          const itemId = params.id;
          this.shoppingService.getById(itemId).subscribe({
            next: res => {
              this.item = res;
            },
            error: error => {
              console.error(`Item could not be retrieved from the backend: ${error}`);
              this.router.navigate(['/shopping-list/1']);
              this.notification.error('Item could not be retrieved', "Error");
            }
          })
        },
        error: error => {
          console.error(`Item could not be retrieved using the ID from the URL: ${error}`);
          this.router.navigate(['/shopping-list/1']);
          this.notification.error('No item provided for editing', "Error");
        }
      })
    }
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.item);

    if (form.valid) {
      let observable: Observable<ItemDto>;
      switch (this.mode) {
        case ItemCreateEditMode.create:
          this.item.quantityCurrent = this.item.quantityTotal;
          observable = this.shoppingService.createItem(this.item);
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
          this.router.navigate(['/shopping-list/1']);
        },
        error: error => {
          console.error(`Error item was not ${this.modeActionFinished}`);
        }
      });
    }
  }

  addLabel(label: string): void {
    if (label == undefined || label.length == 0) {
      return
    }
    if (this.item.labels === undefined) {
      this.item.labels = [{labelValue: label, labelColour: null}];
    } else {
      this.item.labels.push({labelValue: label, labelColour: null});
    }
  }

  removeLabel(i: number) {
    this.item.labels.splice(i, 1);
  }

  formatStorageName(storage: DigitalStorageDto | null): string {
    return storage ? storage.title : '';
  }

  storageSuggestions = (input: string) => (input === '')
    ? of([])
    : this.storageService.findAll(input, 5);
}
