import {Component, ElementRef, HostListener, Input} from '@angular/core';
import {DigitalStorageComponent} from "../digital-storage.component";
import {StorageService} from "../../../services/storage.service";
import {ItemService} from "../../../services/item.service";
import {ItemDto} from "../../../dtos/item";
import {ItemSearchDto} from "../../../dtos/storageItem";

@Component({
  selector: 'app-item-card',
  templateUrl: './item-card.component.html',
  styleUrls: ['./item-card.component.scss']
})

export class ItemCardComponent {
  @Input() id: string;
  @Input() title: string;
  @Input() quantity: number;
  @Input() quantityTotal: number;
  @Input() unit: string;


  customModalOpen: boolean = false;
  customModalOpen1: boolean = false;

  constructor(private el: ElementRef, private digitalStorage: DigitalStorageComponent, private storageService: StorageService,
              private itemService: ItemService) {
  }

  getCardColor(): string {
    const ratio = this.quantity / this.quantityTotal;
    if (ratio < 0.2) return 'bg-danger'; // Low quantity
    if (ratio < 0.4) return 'bg-warning'; // Medium quantity
    return 'bg-primary'; // High quantity
  }


  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    // Check if the clicked element is outside the card
    if (!this.el.nativeElement.contains(event.target)) {
      this.customModalOpen = false;
      this.customModalOpen1 = false;
    }
  }

  onSave(value: string, mode: number) {
    const quantity = parseInt(value);

    if (isNaN(quantity) || quantity < 0) {
      console.error('Invalid input. Please enter a valid number.');
      return;
    }

    let item: ItemDto;
    this.itemService.getById(parseInt(this.id)).subscribe({
      next: res => {
        item = res;

        let quantityCurrent: number;
        if (mode == 0) { // Subtract
          quantityCurrent = item.quantityCurrent - parseInt(value);

          this.customModalOpen = false;
        } else { // mode == 1, Add
          quantityCurrent = item.quantityCurrent + parseInt(value);

          this.customModalOpen1 = false;
        }

        item.quantityCurrent = quantityCurrent;
        console.log(item)
        this.itemService.updateItem(item);
        this.storageService.getItems(new ItemSearchDto())
      },
      error: err => {
        console.error("Error finding item:", err);
      }
    });

  }

}
