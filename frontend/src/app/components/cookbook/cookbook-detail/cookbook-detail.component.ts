import {Component, OnInit} from '@angular/core';
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {CookingService} from "../../../services/cooking.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-cookbook-detail',
  templateUrl: './cookbook-detail.component.html',
  styleUrls: ['./cookbook-detail.component.scss']
})
export class CookbookDetailComponent implements OnInit{

  recipe: RecipeSuggestion;

  constructor(private cookingService: CookingService, private notification: ToastrService, private router: Router,
              private route: ActivatedRoute,) {
  }

  ngOnInit(): void {
    this.route.params.subscribe({
      next: params => {
        const itemId = params.id;
        this.cookingService.getCookbookRecipe(itemId).subscribe({
          next: res => {
            this.recipe = res;
            console.log(this.recipe)
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

  delete() {
    if(confirm("Are you sure you want to delete this recipe?")) {
      this.cookingService.deleteCookbookRecipe(this.recipe.id).subscribe({
        next: (deletedRecipe: RecipeSuggestion) => {
          console.log('Recipe deleted:', deletedRecipe);
        },
        error: error => {
          console.error(error.message, error);
        }
      });
    }
  }

  edit() {
    this.router.navigate(['cookbook/' + this.recipe.id + '/edit']);
  }

  cook() {

  }
}
