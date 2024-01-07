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

    modalRef.componentInstance.matchingDone.subscribe(() => {
      this.load();
    });
  }


}
