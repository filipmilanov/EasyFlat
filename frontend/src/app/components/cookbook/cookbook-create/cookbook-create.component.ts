import {Component, OnInit} from '@angular/core';
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {RecipeIngredient} from "../../../dtos/cookingDtos/recipeIngredient";
import {ItemService} from "../../../services/item.service";
import {StorageService} from "../../../services/storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {CookingService} from "../../../services/cooking.service";
import {ItemCreateEditMode} from "../../digital-storage/item-create-edit/item-create-edit.component";
import {NgForm} from "@angular/forms";
import {Observable} from "rxjs";
import {ItemDto} from "../../../dtos/item";


export enum CookbookMode {
  create,
  edit,
}
@Component({
  selector: 'app-cookbook-create',
  templateUrl: './cookbook-create.component.html',
  styleUrls: ['./cookbook-create.component.scss']
})
export class CookbookCreateComponent implements OnInit{

  mode: CookbookMode = CookbookMode.create;
  recipe: RecipeSuggestion = {
    title: '',
    summary: '',
    servings: 0,
    readyInMinutes: 0,
    extendedIngredients: [],
    missedIngredients:[]
  };

  constructor(
    private cookingService: CookingService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case CookbookMode.create:
        return 'Create Recipe';
      case CookbookMode.edit:
        return 'Edit Recipe';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case CookbookMode.create:
        return 'Create';
      case CookbookMode.edit:
        return 'Update';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === CookbookMode.create;
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case CookbookMode.create:
        return 'created';
      case CookbookMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  addIngredient(ingredient: string, amount: string, unit: string): void {
    if (ingredient == undefined || ingredient.length == 0 || amount == undefined || amount.length == 0 || unit == undefined || unit.length == 0) {
      return;
    }

    const parsedAmount: number = parseFloat(amount);

    if (isNaN(parsedAmount)) {
      console.error('Invalid amount');
      return;
    }

    const newIngredient: RecipeIngredient = {
      name: ingredient,
      unit: unit,
      amount: parsedAmount
    };

    if (!this.recipe.extendedIngredients) {
      this.recipe.extendedIngredients = [newIngredient];
    } else {
      this.recipe.extendedIngredients.push(newIngredient);
    }
  }

  removeIngredient(i: number) {
    this.recipe.extendedIngredients.splice(i, 1);
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.recipe);

    if (form.valid) {
      let observable: Observable<RecipeSuggestion>;
      switch (this.mode) {
        case CookbookMode.create:
          //this.recipe.quantityCurrent = this.item.quantityTotal;
          //observable = this.itemService.createItem(this.item);
          break;
        case CookbookMode.edit:
          //observable = this.itemService.updateItem(this.item);
          break;
        default:
          console.error('Unknown CookbookMode', this.mode);
          return;
      }
    }
  }

}
