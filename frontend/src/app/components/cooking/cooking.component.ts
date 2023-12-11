import {Component, EventEmitter, OnInit, Output} from '@angular/core';

import {CookingService} from "../../services/cooking.service";
import {ToastrService} from "ngx-toastr";
import {RecipeSuggestion} from "../../dtos/cookingDtos/recipeSuggestion";
import {CookbookModalComponent} from "../cookbook/cookbook-modal/cookbook-modal.component";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {CookingModalComponent} from "./cooking-modal/cooking-modal.component";

@Component({
  selector: 'app-cooking',
  templateUrl: './cooking.component.html',
  styleUrls: ['./cooking.component.scss']
})
export class CookingComponent implements OnInit {
  recipes: RecipeSuggestion[];
  empty: boolean = true;
  type:string;
  @Output() cookClicked: EventEmitter<RecipeSuggestion> = new EventEmitter<RecipeSuggestion>();

  constructor(private cookingService: CookingService,
              private notification: ToastrService,
              private modalService: NgbModal) {


  }

  ngOnInit(): void {

  }

  onTypeChange(): void {
    console.log(`Type changed to: ${this.type}`);
  }
  reloadRecipes() {
    this.cookingService.loadRecipes(this.type).subscribe({

      next: res => {
        console.log(this.type)
        this.recipes = res;
        this.empty = false;
      },
      error: err => {
        console.error("Error loading recipes:", err);
        this.notification.error("Error loading recipes");
      }
    })

  }
  openRecipeModal(recipe: RecipeSuggestion) {
    const modalRef = this.modalService.open(CookingModalComponent, { size: 'lg' });
    console.log(recipe + "from Modal");
    modalRef.componentInstance.recipe = recipe;
  }

  handleRecipeAddedToCookbook(recipeTitle: string) {
    this.notification.success(`Recipe ${recipeTitle} successfully added to the cookbook.`, "Success");
  }

  handleRecipeAddedToCookbook(recipeTitle: string) {
    this.notification.success(`Recipe ${recipeTitle} successfully added to the cookbook.`, "Success");
  }

  public addTestData() {
    this.empty = false;
    this.recipes = [
      {
        id: '1',
        title: 'Spaghetti Bolognese',
        summary: 'Pasta with Garlic, Scallions, Cauliflower & Breadcrumbs might be a good recipe to expand your main course repertoire. One portion of this dish contains approximately <b>19g of protein </b>,  <b>20g of fat </b>, and a total of  <b>584 calories </b>. For  <b>$1.63 per serving </b>, this recipe  <b>covers 23% </b> of your daily requirements of vitamins and minerals. This recipe serves 2. It is brought to you by fullbellysisters.blogspot.com. 209 people were glad they tried this recipe. A mixture of scallions, salt and pepper, white wine, and a handful of other ingredients are all it takes to make this recipe so scrumptious. From preparation to the plate, this recipe takes approximately  <b>45 minutes </b>. All things considered, we decided this recipe  <b>deserves a spoonacular score of 83% </b>. This score is awesome. If you like this recipe, take a look at these similar recipes: <a href=\\"https://spoonacular.com/recipes/cauliflower-gratin-with-garlic-breadcrumbs-318375\\">Cauliflower Gratin with Garlic Breadcrumbs</a>, < href=\\"https://spoonacular.com/recipes/pasta-with-cauliflower-sausage-breadcrumbs-30437\\">Pasta With Cauliflower, Sausage, & Breadcrumbs</a>, and <a href=\\"https://spoonacular.com/recipes/pasta-with-roasted-cauliflower-parsley-and-breadcrumbs-30738\\">Pasta With Roasted Cauliflower, Parsley, And Breadcrumbs</a>.',
        servings: 4,
        readyInMinutes: 30,
        extendedIngredients: [
          { id: 1, name: 'Ground beef', unit: 'g', amount: 500 },
          { id: 2, name: 'Tomato sauce', unit: 'ml', amount: 400 },
          // Add more ingredients as needed
        ],
        missedIngredients:[
          { id: 5, name: 'Ingredient 5', unit: 'tsp', amount: 2 },
        ]
      },
      {
        id: '2',
        title: 'Chicken Stir-Fry',
        summary: 'Quick and delicious stir-fried chicken with vegetables.',
        servings: 3,
        readyInMinutes: 20,
        extendedIngredients: [
          { id: 3, name: 'Chicken breast', unit: 'g', amount: 300 },
          { id: 4, name: 'Broccoli', unit: 'g', amount: 200 },
          // Add more ingredients as needed
        ],
        missedIngredients:[
          { id: 5, name: 'Ingredient 5', unit: 'tsp', amount: 2 },
        ]
      },
      // Add more recipes as needed
    ];
  }



}
