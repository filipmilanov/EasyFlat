import {Component, Input} from '@angular/core';

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
}
