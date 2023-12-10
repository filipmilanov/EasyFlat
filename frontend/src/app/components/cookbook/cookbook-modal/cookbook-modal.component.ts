import {Component, Input} from '@angular/core';
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {Router} from "@angular/router";

@Component({
  selector: 'app-cookbook-modal',
  templateUrl: './cookbook-modal.component.html',
  styleUrls: ['./cookbook-modal.component.scss']
})
export class CookbookModalComponent {

  @Input() recipe: RecipeSuggestion;

  constructor(public activeModal: NgbActiveModal, private router: Router) { }

  edit() {
    this.activeModal.dismiss();
    this.router.navigate(['cookbook/' + this.recipe.id + '/edit']);
  }

}
