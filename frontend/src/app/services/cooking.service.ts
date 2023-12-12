import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Globals} from "../global/globals";
import {Observable} from "rxjs";
import {RecipeDetailDto, RecipeSuggestion} from "../dtos/cookingDtos/recipeSuggestion";

@Injectable({
  providedIn: 'root'
})
export class CookingService {
  baseUri = environment.backendUrl + '/cooking';
  cookbookUri = this.baseUri + '/cookbook';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  loadRecipes(type: string): Observable<RecipeSuggestion[]> {
    console.log(type + '  service')
    let params = new HttpParams();
    if (type) {
      params = params.append('type', type);
    }
    return this.httpClient.get<RecipeSuggestion[]>(this.baseUri, {params});
  }

  getCookbook(): Observable<RecipeSuggestion[]> {
    return this.httpClient.get<RecipeSuggestion[]>(this.cookbookUri);
  }

  createCookbookRecipe(recipe: RecipeSuggestion): Observable<RecipeSuggestion> {
    if(recipe.id != null){
      recipe.id = null;
    }
    if (recipe.missedIngredients) {
      recipe.missedIngredients.forEach(ingredient => {
        if (ingredient.id != null) {
          ingredient.id = null;
        }
      });
    }

    return this.httpClient.post<RecipeSuggestion>(this.cookbookUri, recipe);
  }

  updateCookbookRecipe(recipe: RecipeSuggestion): Observable<RecipeSuggestion> {
    return this.httpClient.put<RecipeSuggestion>(this.cookbookUri + '/' + recipe.id, recipe);
  }

  getCookbookRecipe(id: string): Observable<RecipeSuggestion> {
    return this.httpClient.get<RecipeSuggestion>(this.cookbookUri + '/' + id);
  }

  deleteCookbookRecipe(id: string): Observable<RecipeSuggestion> {
    return this.httpClient.delete<RecipeSuggestion>(this.cookbookUri + '/' + id);
  }

  getRecipeDetails(id: string): Observable<RecipeDetailDto> {
    return this.httpClient.get<RecipeDetailDto>(this.baseUri + '/detail/' + id);
  }

  getMissingIngredients(id:string): Observable<RecipeSuggestion> {
    return this.httpClient.get<RecipeSuggestion>(this.cookbookUri + '/missing/' + id);
  }

  cookRecipe(recipe:RecipeSuggestion):Observable<RecipeSuggestion>{
    return this.httpClient.put<RecipeSuggestion>(this.baseUri + "/cook",recipe)
  }
}
