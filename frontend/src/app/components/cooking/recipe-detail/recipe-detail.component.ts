import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {RecipeDetailDto, RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {ItemService} from "../../../services/item.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {CookingService} from "../../../services/cooking.service";
import {NgbActiveModal, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {MatchingModalComponent} from "../matching-modal/matching-modal.component";
import {RecipeIngredient} from "../../../dtos/cookingDtos/recipeIngredient";

@Component({
  selector: 'app-recipe-detail',
  templateUrl: './recipe-detail.component.html',
  styleUrls: ['./recipe-detail.component.scss']
})
export class RecipeDetailComponent implements OnInit {
  @Input() recipeID: string;
  recipeDetail: RecipeDetailDto;
  @Output() matchingClicked: EventEmitter<string> = new EventEmitter<string>();

  constructor(
    private service: CookingService,
    private notification: ToastrService,
    private router: Router,
    private route: ActivatedRoute,
    public activeModal: NgbActiveModal,
    private modalService: NgbModal
  ) {
  }

  ngOnInit(): void {
    this.load()
  }

  load() {

    console.log("DEATILS" + this.recipeID);
    this.service.getRecipeDetails(this.recipeID).subscribe({
      next: res => {
        this.recipeDetail = res;
      },
      error: error => {

      }
    })

  }



  openMatchModal(ingredient: RecipeIngredient) {
    const modalRef = this.modalService.open(MatchingModalComponent, {size: 'lg'});
    modalRef.componentInstance.ingredient = ingredient;
  }



  addTestItems() {

    const testRecipe: RecipeDetailDto = {
      id: 'test-id',
      title: 'Test Recipe',
      summary: 'This is a <b>test recipe</b> summary.',
      servings: 4,
      readyInMinutes: 30,
      extendedIngredients: [
        {id: 1, name: 'Ingredient 1', unit: 'g', amount: 100},
        {id: 2, name: 'Ingredient 2', unit: 'ml', amount: 200},
        {id: 3, name: 'Ingredient 3', unit: 'pieces', amount: 3},
        {id: 4, name: 'Ingredient 4', unit: 'tsp', amount: 2},
        // Add more ingredients as needed
      ],
      steps: {
        steps: [
          {number: 1, step: 'Step 1: Do something'},
          {number: 2, step: 'Step 2: Do something else'},
          {number: 3, step: 'Step 3: Do another thing'},
          {number: 4, step: 'Step 4: Final step'},
          // Add more steps as needed
        ]
      },
      missedIngredients: [
        {id: 5, name: 'Ingredient 5', unit: 'tsp', amount: 2},
      ]
    };
    this.recipeDetail = testRecipe;
  }
}
