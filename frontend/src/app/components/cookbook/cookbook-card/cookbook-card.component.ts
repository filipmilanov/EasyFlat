import {Component, Input} from '@angular/core';
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {CookingService} from "../../../services/cooking.service";
import {UserDetail} from "../../../dtos/auth-request";
import {Router} from "@angular/router";

@Component({
  selector: 'app-cookbook-card',
  templateUrl: './cookbook-card.component.html',
  styleUrls: ['./cookbook-card.component.scss']
})
export class CookbookCardComponent {

  @Input() recipe: RecipeSuggestion;

  constructor(private cookingService: CookingService,private router: Router) {
  }

  getTruncatedSummary(): string {
    const maxLength = 100; // Adjust as needed
    return this.recipe.summary.length > maxLength ?
      this.recipe.summary.slice(0, maxLength) + '...' :
      this.recipe.summary;
  }
  delete() {
    if(confirm("Are you sure you want to delete your account?")) {
      this.cookingService.deleteCookbookRecipe(this.recipe.id).subscribe({
        next: (deletedRecipe: RecipeSuggestion) => {
          console.log('Recipe deleted:', deletedRecipe);
        },
        error: error => {
          console.error(error.message, error);
        }
      });
    }
  }


}
