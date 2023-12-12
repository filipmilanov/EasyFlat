import {Component, EventEmitter, Input, Output} from '@angular/core';
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {CookingService} from "../../../services/cooking.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";


@Component({
  selector: 'app-recipe-card',
  templateUrl: './recipe-card.component.html',
  styleUrls: ['./recipe-card.component.scss']
})
export class RecipeCardComponent {

  @Input() recipe: RecipeSuggestion;
  @Output() recipeAddedToCookbook: EventEmitter<string> = new EventEmitter();
  @Output() cookClicked: EventEmitter<RecipeSuggestion> = new EventEmitter<RecipeSuggestion>();

  isSaveButtonDisabled = false;
  constructor(
    private cookingService: CookingService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  getTruncatedSummary(): string {
    const maxLength = 100; // Adjust as needed
    return this.recipe.summary.length > maxLength ?
      this.recipe.summary.slice(0, maxLength) + '...' :
      this.recipe.summary;
  }

  addToCookBook(){

    this.cookingService.createCookbookRecipe(this.recipe).subscribe({
      next: data => {
        this.isSaveButtonDisabled = true;
        this.recipeAddedToCookbook.emit(this.recipe.title);
      },
      error: error => {
        console.error(`Error ${error}`);
      }
    });
  }

  cook() {
    this.cookingService.getMissingIngredients(this.recipe.id).subscribe({
      next: (missingIngredients: RecipeSuggestion) => {
        if (missingIngredients && missingIngredients.missedIngredients.length > 0) {
          this.cookClicked.emit(this.recipe);
        } else {
          // If no missing ingredients, proceed to cook directly
        }
      },
      error: error => {
        console.error('Error checking missing ingredients:', error);
      }
    });
  }



}
