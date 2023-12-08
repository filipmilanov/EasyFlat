import {Component, OnInit} from '@angular/core';
import {RecipeDetailDto} from "../../../dtos/cookingDtos/recipeSuggestion";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {CookingService} from "../../../services/cooking.service";

@Component({
  selector: 'app-recipe-detail',
  templateUrl: './recipe-detail.component.html',
  styleUrls: ['./recipe-detail.component.scss']
})
export class RecipeDetailComponent implements OnInit{
   recipe:RecipeDetailDto;

  constructor(
    private service: CookingService,
    private notification: ToastrService,
    private router: Router,
    private route: ActivatedRoute,
  ) {
  }

  ngOnInit(): void {
   this.load();
  }

  load(){
    this.route.params.subscribe({
      next: params => {
        const recipeId = params.id;
        this.service.getRecipeDetails(recipeId.toString()).subscribe({
          next: res => {
            this.recipe = res;
          },
          error: error => {

          }
        })
      },
      error: error => {

      }
    });
  }

  addTestItems(){

    const testRecipe: RecipeDetailDto = {
      id: 'test-id',
      title: 'Test Recipe',
      summary: 'This is a <b>test recipe</b> summary.',
      servings: 4,
      readyInMinutes: 30,
      extendedIngredients: [
        { id: 1, name: 'Ingredient 1', unit: 'g', amount: 100 },
        { id: 2, name: 'Ingredient 2', unit: 'ml', amount: 200 },
        { id: 3, name: 'Ingredient 3', unit: 'pieces', amount: 3 },
        { id: 4, name: 'Ingredient 4', unit: 'tsp', amount: 2 },
        // Add more ingredients as needed
      ],
      steps: {
        steps: [
          { number: 1, step: 'Step 1: Do something' },
          { number: 2, step: 'Step 2: Do something else' },
          { number: 3, step: 'Step 3: Do another thing' },
          { number: 4, step: 'Step 4: Final step' },
          // Add more steps as needed
        ]
      }
    };
this.recipe = testRecipe;
  }
}
