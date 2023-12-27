import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-shopping-list-card',
  templateUrl: './shopping-list-card.component.html',
  styleUrls: ['./shopping-list-card.component.scss']
})
export class ShoppingListCardComponent {
  @Input() id: string;
  @Input() listName: string

}
