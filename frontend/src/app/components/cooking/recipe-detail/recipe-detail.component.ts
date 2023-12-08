import { Component } from '@angular/core';
import {RecipeDetailDto} from "../../../dtos/recipeSuggestion";

@Component({
  selector: 'app-recipe-detail',
  templateUrl: './recipe-detail.component.html',
  styleUrls: ['./recipe-detail.component.scss']
})
export class RecipeDetailComponent {
   recipe:RecipeDetailDto;
}
