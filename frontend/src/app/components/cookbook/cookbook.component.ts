import {Component, OnInit} from '@angular/core';
import {RecipeSuggestion} from "../../dtos/cookingDtos/recipeSuggestion";
import {CookingService} from "../../services/cooking.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-cookbook',
  templateUrl: './cookbook.component.html',
  styleUrls: ['./cookbook.component.scss']
})
export class CookbookComponent implements OnInit{
  recipes: RecipeSuggestion[];

  constructor(private cookingService: CookingService,
              private notification: ToastrService) {
 }

  ngOnInit(): void {
    this.cookingService.getCookbook().subscribe({
      next: res => {
      this.recipes = res;
    },
      error: err => {
      console.error("Error loading recipes:", err);
      this.notification.error("Error loading recipes");
    }
  })
  }
}
