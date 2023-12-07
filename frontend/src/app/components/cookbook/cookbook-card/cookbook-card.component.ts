import {Component, Input} from '@angular/core';
import {RecipeSuggestion} from "../../../dtos/recipeSuggestion";

@Component({
  selector: 'app-cookbook-card',
  templateUrl: './cookbook-card.component.html',
  styleUrls: ['./cookbook-card.component.scss']
})
export class CookbookCardComponent {

  @Input() recipe: RecipeSuggestion;

  getTruncatedSummary(): string {
    const maxLength = 100; // Adjust as needed
    return this.recipe.summary.length > maxLength ?
      this.recipe.summary.slice(0, maxLength) + '...' :
      this.recipe.summary;
  }

}
