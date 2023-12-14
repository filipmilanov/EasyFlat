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
import {switchMap} from 'rxjs/operators';
import {Unit} from "../../../dtos/unit";
import {UnitService} from "../../../services/unit.service";
import {ShoppingListDto} from "../../../dtos/shoppingList";


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
  availableUnits: Unit[] = [];
  shoppingListName: string = '';

  constructor(
    private itemService: ItemService,
    private storageService: StorageService,
    private shoppingService: ShoppingListService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private unitService: UnitService
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
        return 'Add Item';
      case ItemCreateEditMode.edit:
        return 'Update Item';
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
    this.unitService.findAll().subscribe({
      next: res => {
        this.availableUnits = res;
        this.item.unit = this.availableUnits[0];
      },
      error: err => {
        this.notification.error('Failed to load Units', "Error");
      }
    });

    this.route.data.subscribe(data => {
      this.mode = data.mode;
      this.route.params.subscribe(params => {
        // Extract the 'id' parameter from the route
        const name = params['name'];
        this.shoppingService.getShoppingListByName(name).subscribe({
          next: res => {
            this.item.shoppingList = res;
            console.log(this.item.shoppingList)
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
              this.item.unit = res.unit;
              console.log(this.item.unit)
            },
            error: error => {
              console.error(`Item could not be retrieved from the backend: ${error}`);
              this.router.navigate(['shopping-list', this.item.shoppingList.listName]);
              this.notification.error('Item could not be retrieved', "Error");
            }
          })
        },
        error: error => {
          console.error(`Item could not be retrieved using the ID from the URL: ${error}`);
          this.router.navigate(['shopping-list', this.item.shoppingList.listName]);
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
          this.item.productName = this.item.generalName;
          observable = this.shoppingService.createItem(this.item);
          break;
        case ItemCreateEditMode.edit:
          observable = this.shoppingService.updateItem(this.item);
          break;
        default:
          console.error('Unknown ItemCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Item ${this.item.productName} successfully ${this.modeActionFinished}.`, "Success");
          this.router.navigate(['/shopping-list/' + this.item.shoppingList.listName]);
          console.log(this.shoppingListName)
        },
        error: error => {
          console.error(`Error item was not ${this.modeActionFinished}`);
        }
      });
    }
  }

  addLabel(label: string, selectedLabelColor: string): void {
    if (label == undefined || label.length == 0) {
      return
    }
    console.log(label, selectedLabelColor)
    if (this.item.labels === undefined) {
      this.item.labels = [{
        labelValue: label,
        labelColour: (selectedLabelColor != '#ffffff' ? selectedLabelColor : '#000000')
      }];
    } else {
      this.item.labels.push({
        labelValue: label,
        labelColour: (selectedLabelColor != '#ffffff' ? selectedLabelColor : '#000000')
      });
    }
  }

  removeLabel(i: number) {
    this.item.labels.splice(i, 1);
  }

  formatStorageName(storage: DigitalStorageDto | null): string {
    return storage ? storage.title : '';
  }

  formatUnitName(unit: Unit | null): string {
    return unit ? unit.name : '';
  }

}
