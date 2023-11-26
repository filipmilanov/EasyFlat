import {Component, OnInit} from '@angular/core';
import {ItemDto} from "../../../dtos/item";
import {update} from "lodash";
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-item-create',
  templateUrl: './item-create.component.html',
  styleUrls: ['./item-create.component.scss']
})
export class ItemCreateComponent implements OnInit{

  item: ItemDto = {
    alwaysInStock: false,
    addToFiance: false
  }
  isCreateMode: boolean;
  priceInEuro: number = 0.00;
  addToFiance: boolean = false;

  ngOnInit(): void {

  }

  submit(form: NgForm): void {
    this.item.priceInCent = this.priceInEuro * 100;

  }

  addIngredient(ingredient: string): void {
    if (ingredient == undefined || ingredient.length == 0) {
      console.log("sdfa");
      return
    }
    if (this.item.ingredients === undefined) {
      this.item.ingredients = [ingredient];
    } else {
      this.item.ingredients.push(ingredient);
    }
  }

  validatePriceInput(event: any): void {
    let inputValue = event.target.value.replace(/[^0-9.]/g, '');
    event.target.value = this.formatPriceInEuroInput(inputValue);
    this.priceInEuro = parseFloat(inputValue);
  }

  formatPriceInEuroInput(value: string): string {
    return  value ? `${value} € ` : '';
  }



}
