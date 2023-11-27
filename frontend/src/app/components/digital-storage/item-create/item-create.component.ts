import {Component, OnInit} from '@angular/core';
import {ItemDto} from "../../../dtos/item";
import {NgForm} from "@angular/forms";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";

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


  constructor(
    private itemService: ItemService,
    private notification: ToastrService
  ) {
  }

  ngOnInit(): void {

  }

  submit(form: NgForm): void {
    this.item.priceInCent = this.priceInEuro * 100;
    this.item.quantityCurrent = this.item.quantityTotal;

    let o = this.itemService.createItem(this.item);

    o.subscribe({
      next: res => {
        this.notification.success(this.item.productName + " was added to your storage", "Success");
        form.reset();
      },
      error: err => {
        console.error("Error adding item to storage:", err);
        this.notification.error(err.error, "Error");
      }
    })

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

  removeIngredient(i: number) {
    this.item.ingredients.splice(i, 1);
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
