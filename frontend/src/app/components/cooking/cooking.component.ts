import {Component, OnInit} from '@angular/core';
import {Recipe} from "../../dtos/recipe";
import {CookingService} from "../../services/cooking.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-cooking',
  templateUrl: './cooking.component.html',
  styleUrls: ['./cooking.component.scss']
})
export class CookingComponent implements OnInit{
      recipes : Recipe[];


  constructor(private cookingService: CookingService,
              private notification: ToastrService) {


  }
  ngOnInit(): void {
    this.addTestData();
  }

  reloadRecipes() {
    this.cookingService.loadRecipes().subscribe({

      next: res => {
        this.recipes = res;
      },
      error: err => {
        console.error("Error loading recipes:", err);
        this.notification.error("Error loading recipes");
      }
    })

  }



 private  addTestData(){
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
