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

}
