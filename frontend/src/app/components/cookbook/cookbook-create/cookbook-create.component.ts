import {Component, OnInit} from '@angular/core';
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {RecipeIngredient} from "../../../dtos/cookingDtos/recipeIngredient";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {CookingService} from "../../../services/cooking.service";
import {NgForm} from "@angular/forms";
import {Observable} from "rxjs";
import {Unit} from "../../../dtos/unit";
import {UnitService} from "../../../services/unit.service";


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
  availableUnits: Unit[] = [];
  selectedUnit: Unit;

  constructor(
    private cookingService: CookingService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private unitService: UnitService
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

  addIngredient(ingredient: string, amount: string): void {
    if (ingredient == undefined || ingredient.length == 0 || amount == undefined || amount.length == 0 ) {
      return;
    }

    const parsedAmount: number = parseFloat(amount);

    if (isNaN(parsedAmount)) {
      console.error('Invalid amount');
      return;
    }

    const newIngredient: RecipeIngredient = {
      name: ingredient,
      unit: this.selectedUnit.name,
      unitEnum: this.selectedUnit,
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

    this.unitService.findAll().subscribe({
      next: res => {
        this.availableUnits = res;
      },
      error: err => {
        this.notification.error('Failed to load Units', "Error");
      }
    });

    if (this.mode === CookbookMode.edit) {
      this.route.params.subscribe({
        next: params => {
          const itemId = params.id;
          this.cookingService.getCookbookRecipe(itemId).subscribe({
            next: res => {
              this.recipe = res;
            },
            error: error => {
              console.error(`Recipe could not be retrieved from the backend: ${error}`);
              this.router.navigate(['/cookbook']);
              this.notification.error('Recipe could not be retrieved', "Error");
            }
          })
        },
        error: error => {
          console.error(`Recipe could not be retrieved using the ID from the URL: ${error}`);
          this.router.navigate(['cookbook']);
          this.notification.error('No recipe provided for editing', "Error");
        }
      })
    }
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.recipe);

    if (form.valid) {
      let observable: Observable<RecipeSuggestion>;
      switch (this.mode) {
        case CookbookMode.create:
          observable = this.cookingService.createCookbookRecipe(this.recipe);
          break;
        case CookbookMode.edit:
          observable = this.cookingService.updateCookbookRecipe(this.recipe);
          break;
        default:
          console.error('Unknown CookbookMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Recipe ${this.recipe.title} successfully ${this.modeActionFinished} and added to the cookbook.`, "Success");
          this.router.navigate(['/cookbook']);
        },
        error: error => {
          console.error(`Error cookbook was not ${this.modeActionFinished}: ${error}`);
          console.error(error);
          let firstBracket = error.error.indexOf('[');
          let lastBracket = error.error.indexOf(']');
          let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
          let errorDescription = error.error.substring(0, firstBracket);
          errorMessages.forEach(message => {
            this.notification.error(message, errorDescription);
          });
        }
      });
    }
  }

  formatUnitName(unit: Unit | null): string {
    return unit ? unit.name : '';
  }

  onUnitSelect(selectedUnit: Unit): void {
    this.selectedUnit = selectedUnit;
    console.log(selectedUnit)
  }


}
