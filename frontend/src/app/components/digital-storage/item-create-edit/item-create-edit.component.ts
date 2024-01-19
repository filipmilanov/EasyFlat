import {Component, OnInit, ViewChild} from '@angular/core';
import {ItemDto} from "../../../dtos/item";
import {NgForm} from "@angular/forms";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";
import {DigitalStorageDto} from "../../../dtos/digitalStorageDto";
import {Observable, of} from "rxjs";
import {StorageService} from "../../../services/storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Unit} from "../../../dtos/unit";
import {UnitService} from "../../../services/unit.service";
import {NgxScannerQrcodeComponent, ScannerQRCodeResult} from "ngx-scanner-qrcode";
import {OpenFoodFactService} from "../../../services/open-food-fact.service";

export enum ItemCreateEditMode {
  create,
  edit,
}

@Component({
  selector: 'app-item-create-edit',
  templateUrl: './item-create-edit.component.html',
  styleUrls: ['./item-create-edit.component.scss']
})
export class ItemCreateEditComponent implements OnInit {

  @ViewChild('action')
  scanner: NgxScannerQrcodeComponent;

  mode: ItemCreateEditMode = ItemCreateEditMode.create;
  item: ItemDto = {
    alwaysInStock: false,
    addToFiance: false,
    boughtAt: '',
    unit: {
      name: ''
    }
  }
  priceInEuro: number = 0.00;
  availableUnits: Unit[] = [];


  constructor(
    private itemService: ItemService,
    private storageService: StorageService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private unitServ: UnitService,
    private openFoodFactService: OpenFoodFactService
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case ItemCreateEditMode.create:
        return 'Create a new item';
      case ItemCreateEditMode.edit:
        return 'Editing item';
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
    this.unitServ.findAll().subscribe({
      next: res => {
        this.availableUnits = res;
        this.item.unit = this.availableUnits[0];
      },
      error: () => {
        this.notification.error('Failed to load units.', "Error");
      }
    });

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
            error: () => {
              this.router.navigate(['/digital-storage/']);
              this.notification.error('Item could not be retrieved from the server.', "Error");
            }
          })
        },
        error: () => {
          this.router.navigate(['/digital-storage/']);
          this.notification.error('Item could not be loaded for editing.', "Error");
        }
      })
    }

    if (this.mode === ItemCreateEditMode.create) {
      this.storageService.findAll('', 1).subscribe({
        next: res => {
          this.item.digitalStorage = res[0];
        },
        error: () => {
          this.notification.error('Failed to load the storage.', "Error");
        }
      });
    }
  }

  public onSubmit(form: NgForm): void {
    this.item.priceInCent = this.priceInEuro * 100;
    if (this.item.ean == '') {
      this.item.ean = null;
    }

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
        next: () => {
          if(this.modeIsCreate){
            this.notification.success(`Item ${this.item.productName} successfully ${this.modeActionFinished} and added to the storage.`, "Success");
          } else {
            this.notification.success(`Item ${this.item.productName} successfully ${this.modeActionFinished}.`, "Success");
          }
          if( !this.modeIsCreate && this.item.quantityCurrent < this.item.minimumQuantity){
            this.notification.success(`The item was automatically added to the shopping list.`, "Success");
          }
          this.router.navigate(['/digital-storage']);
        },
        error: error => {
          if (error.status === 500) {
            this.notification.error(`The item could not be ${this.modeActionFinished} due to an issue with the server.`, "Error");
          } else {
            let firstBracket = error.error.indexOf('[');
            let lastBracket = error.error.indexOf(']');
            let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
            let errorDescription = error.error.substring(0, firstBracket);
            errorMessages.forEach((message: string) => {
              this.notification.error(message, errorDescription);
            });
            this.notification.error(`The item could not be ${this.modeActionFinished}.`, "Error");
          }
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

  formatStorageName(storage: DigitalStorageDto | null): string {
    return storage ? storage.title : '';
  }

  storageSuggestions = (input: string) => (input === '')
    ? of([])
    : this.storageService.findAll(input, 5);

  formatGeneralName(generalName: string | null): string {
    return generalName != null ? generalName : '';
  }

  generalNameSuggestions = (input: string) => (input === '')
    ? of([])
    : this.itemService.findByGeneralName(input);

  formatBrand(brand: string | null): string {
    return brand ? brand : '';
  }

  brandSuggestions = (input: string) => (input === '')
    ? of([])
    : this.itemService.findByBrand(input);

  formatBoughtAt(boughtAt: string | null): string {
    return boughtAt != null ? boughtAt : '';
  }

  boughtAtSuggestions = (input: string) => (input === '')
    ? of([])
    : this.itemService.findByBoughtAt(input);

  formatUnitName(unit: Unit | null): string {
    return unit ? unit.name : '';
  }

  toggleScanning() {
    this.scanner.isStart ? this.scanner.stop() : this.scanner.start()
  }

  updateEAN(ean: ScannerQRCodeResult[]) {
    this.scanner.pause();
    this.item.ean = this.scanner.data.value[0].value;

    this.notification.info("Fetching barcode data...", "Fetching data");
    this.searchForEan(this.item.ean, true);
  }

  searchForEan(ean: string, wasFromScanner: boolean) {
    let o = this.openFoodFactService.findByEan(ean);
    o.subscribe({
      next: data => {
        this.notification.success("EAN data successfully retrieved.", "Success");
        if (data != null) {
          this.item = {
            ...this.item,
            generalName: data.generalName,
            productName: data.productName,
            brand: data.brand,
            ingredients: data.ingredients,
            quantityTotal: data.quantityTotal,
            unit: (this.availableUnits[0] == null ? this.item.unit : this.availableUnits[0]),
            ean: ean
          };
        } else {
          this.notification.warning("No data found for EAN number.", "No Data");
        }
      },
      error: () => {
        if (wasFromScanner) {
          this.notification.error("An error occurred while fetching EAN data.", "Error");
        } else {
          this.notification.error("An error occurred while searching for EAN data.", "Error");
        }
      }
    })
  }

  togglePlayPause() {
    if (this.scanner.isPause) {
      this.scanner.play();
    } else {
      this.scanner.pause();
    }
  }

  public delete() {
    this.itemService.deleteItem(this.item.itemId).subscribe({
      next: () => {
        this.router.navigate(['/digital-storage/']);
        this.notification.success(`Item ${this.item.productName} was successfully deleted.`, "Success");
      },
      error: () => {
        this.notification.error(`Item ${this.item.productName} could not be deleted.`, "Error");
      }
    });
  }

  getIdFormatForDeleteModal(item: ItemDto): string {
    return `${item.productName}${item.itemId.toString()}`.replace(/[^a-zA-Z0-9]+/g, '');
  }

  public compareUnitObjects(itemUnit: Unit, availableUnit: Unit): boolean {
    return itemUnit && availableUnit ? itemUnit.name === availableUnit.name : itemUnit === availableUnit;
  }
}
