import {Component, ElementRef, HostListener, Input} from '@angular/core';
import {DigitalStorageComponent} from "../digital-storage.component";

@Component({
  selector: 'app-item-card',
  templateUrl: './item-card.component.html',
  styleUrls: ['./item-card.component.scss']
})

export class ItemCardComponent {
  @Input() title: string;
  @Input() quantity: number;
  @Input() maxQuantity: number;
  @Input() expirationDate: string;

  customModalOpen: boolean = false;
  customModalOpen1: boolean = false;
  private popupTimeout: any;

  constructor(private el: ElementRef, private digitalStorage: DigitalStorageComponent) {}

  getCardColor(): string {
    const ratio = this.quantity / this.maxQuantity;
    if (ratio < 0.2) return 'bg-danger'; // Low quantity
    if (ratio < 0.4) return 'bg-warning'; // Medium quantity
    return 'bg-primary'; // High quantity
  }

  // Method to open/close the custom modal
  toggleCustomModal() {
    this.customModalOpen = !this.customModalOpen;
    if (this.customModalOpen == true) {
      this.customModalOpen1 = false;
    }
  }

  toggleCustomModal1() {
    this.customModalOpen1 = !this.customModalOpen1;
    if (this.customModalOpen1 == true) {
      this.customModalOpen = false;
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    // Check if the clicked element is outside the popup
    if (!this.el.nativeElement.contains(event.target)) {
      this.customModalOpen = false;
      this.customModalOpen1 = false;
    }
  }

  onSave(value: string, mode: number) {
    const quantity = parseInt(value);

    if (isNaN(quantity)) {
      console.error('Invalid input. Please enter a valid number.');
      return;
    }

    if (mode == 0) {

    } else {

    }
    this.digitalStorage.loadStorage();
  }
}
