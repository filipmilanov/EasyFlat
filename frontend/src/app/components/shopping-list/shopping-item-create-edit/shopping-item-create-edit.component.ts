import {Component, OnInit, SecurityContext} from '@angular/core';
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
import {DomSanitizer} from "@angular/platform-browser";

export enum ShoppingItemCreateEditMode {
  create,
  edit,
}

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
  unitName: string;

  constructor(
    private shoppingService: ShoppingListService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private unitService: UnitService,
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
        const id = params['id'];
        this.shoppingService.getShoppingListById(id).subscribe({
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
              this.unitName = res.unit.name;
              console.log(this.item.unit)
            },
            error: error => {
              console.error(`Item could not be retrieved from the backend: ${error}`);
              this.router.navigate(['shopping-lists', 'list' + this.item.shoppingList.id]);
              this.notification.error('Item could not be retrieved', "Error");
            }
          })
        },
        error: error => {
          console.error(`Item could not be retrieved using the ID from the URL: ${error}`);
          this.router.navigate(['shopping-lists', 'list' + this.item.shoppingList.id]);
          this.notification.error('No item provided for editing', "Error");
        }
      })
    }
  }


  public onSubmit(form: NgForm): void {

    if (form.valid) {
      let observable: Observable<ShoppingItemDto>;
      this.item.quantityCurrent = this.item.quantityTotal;
      switch (this.mode) {
        case ItemCreateEditMode.create:
          if (this.item.generalName == null) {
            this.item.generalName = this.item.productName;
          }
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
          this.router.navigate(['shopping-lists', 'list', this.item.shoppingList.id]);
        },
        error: error => {
          console.log(error)
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

  addLabel(label: string, selectedLabelColor: string): void {
    if (label == undefined || label.length == 0) {
      return
    }
    if (this.item.labels === undefined) {
      this.item.labels = [{
        labelValue: label,
        labelColour: (selectedLabelColor != '#ffffff' ? selectedLabelColor : '#000000'),
      }];
    } else {
      this.item.labels.push({
        labelValue: label,
        labelColour: (selectedLabelColor != '#ffffff' ? selectedLabelColor : '#000000'),
      });
    }
  }

  removeLabel(i: number) {
    this.item.labels.splice(i, 1);
  }


  formatUnitName(unit: Unit | null): string {
    return unit ? unit.name : '';
  }

  public compareUnitObjects(itemUnit: Unit, availableUnit: Unit): boolean {
    return itemUnit && availableUnit ? itemUnit.name === availableUnit.name : itemUnit === availableUnit;
  }
}
