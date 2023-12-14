import {Component, Input, OnInit} from '@angular/core';
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {CookingService} from "../../../services/cooking.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-cookbook-detail',
  templateUrl: './cookbook-detail.component.html',
  styleUrls: ['./cookbook-detail.component.scss']
})
export class CookbookDetailComponent implements OnInit{


  @Input() recipe: RecipeSuggestion;

  constructor(public activeModal: NgbActiveModal, private cookingService: CookingService, private notification: ToastrService, private router: Router,
              private route: ActivatedRoute,) {
  }

  ngOnInit(): void {
    console.log(this.recipe)
    this.cookingService.getCookbookRecipe(this.recipe.id.toString()).subscribe({
      next: res => {
        this.recipe = res;
      },
      error: error => {

      }
    })
  }
}
