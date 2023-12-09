import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
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

  loadRecipes(): Observable<RecipeSuggestion[]> {
    return this.httpClient.get<RecipeSuggestion[]>(this.baseUri)
  }

  getCookbook(): Observable<RecipeSuggestion[]> {
    return this.httpClient.get<RecipeSuggestion[]>(this.cookbookUri);
  }

  createCookbookRecipe(recipe: RecipeSuggestion): Observable<RecipeSuggestion> {
    return this.httpClient.post<RecipeSuggestion>(this.cookbookUri, recipe);
  }

  updateCookbookRecipe(recipe: RecipeSuggestion): Observable<RecipeSuggestion> {
    return this.httpClient.put<RecipeSuggestion>(this.cookbookUri + '/' + recipe.id, recipe)
  }

  getCookbookRecipe(id:string): Observable<RecipeSuggestion> {
    return this.httpClient.get<RecipeSuggestion>(this.cookbookUri+ '/' + id)
  }

  getRecipeDetails(id:string): Observable<RecipeDetailDto>{
    return this.httpClient.get<RecipeDetailDto>(this.baseUri + '/detail/' + id);
  }

}
