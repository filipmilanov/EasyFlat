import {Component, Input} from '@angular/core';
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
        this.notification.success(`Recipe ${this.recipe.title} successfully added to the cookbook.`, "Success");
      },
      error: error => {
        console.error(`Error ${error}`);
      }
    });
  }



}
