import {Component, OnInit} from '@angular/core';
import {ItemDto} from "../../../dtos/item";

@Component({
  selector: 'app-item-create',
  templateUrl: './item-create.component.html',
  styleUrls: ['./item-create.component.scss']
})
export class ItemCreateComponent implements OnInit{

  item: ItemDto = {}
  isCreateMode: boolean;

  ngOnInit(): void {

  }

  submit(): void {

  }

  addIngredient(ingredient: string): void {
    if (ingredient == undefined || ingredient.length == 0) {
      return
    }
    if (this.item.ingredients === undefined) {
      this.item.ingredients = [ingredient];
    } else {
      this.item.ingredients.push(ingredient);
    }
  }





}
