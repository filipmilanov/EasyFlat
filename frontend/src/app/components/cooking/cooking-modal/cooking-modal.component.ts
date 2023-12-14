import {Component, Input, OnInit} from '@angular/core';
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Router} from "@angular/router";
import {CookingService} from "../../../services/cooking.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-cooking-modal',
  templateUrl: './cooking-modal.component.html',
  styleUrls: ['./cooking-modal.component.scss']
})
export class CookingModalComponent {

  @Input() recipe: RecipeSuggestion;

  recipeWithMissing: RecipeSuggestion;

  constructor(public activeModal: NgbActiveModal, private router: Router, public cookingService: CookingService, private notification: ToastrService) {

  }

  cook() {
    this.cookingService.cookRecipe(this.recipe).subscribe({
      next: res => {
        console.log("cooked");

      },
      error: err => {
        console.error("Error loading recipes:", err);
        this.notification.error("Error loading recipes");
      }
    })
  }


}
