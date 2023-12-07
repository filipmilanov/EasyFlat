import {RecipeIngredient} from "./recipeIngredient";

export class RecipeSuggestion {
  recipeId: string;
  title: string;
  summary:string;
  servings:number;
  readyInMinutes:number;
  ingredients: RecipeIngredient[]

}
