import {RecipeIngredient} from "./recipeIngredient";

export class RecipeSuggestion {
  recipeId: string;
  title: string;
  summary:string;
  servings:number;
  readyInMinutes:number;
  ingredients: RecipeIngredient[]

}


export class RecipeDetailDto {
  recipeId: string;
  title: string;
  summary:string;
  servings:number;
  readyInMinutes:number;
  ingredients: RecipeIngredient[]

}
