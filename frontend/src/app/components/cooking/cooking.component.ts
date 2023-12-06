import {Component} from '@angular/core';
import {Recipe} from "../../dtos/recipe";

@Component({
  selector: 'app-cooking',
  templateUrl: './cooking.component.html',
  styleUrls: ['./cooking.component.scss']
})
export class CookingComponent {
      recipes : Recipe[];


  constructor() {
    // Create test recipes and add them to the recipes array
    this.recipes = [
      {
        recipeId: '1',
        title: 'Spaghetti Bolognese',
        description: 'Classic Italian pasta dish with meat sauce.'
      },
      {
        recipeId: '2',
        title: 'Chicken Stir-Fry',
        description: 'Quick and delicious stir-fried chicken with vegetables.'
      },
      {
        recipeId: '3',
        title: 'Vegetarian Pizza',
        description: 'Homemade pizza topped with a variety of fresh vegetables.'
      }
    ];
  }
}
