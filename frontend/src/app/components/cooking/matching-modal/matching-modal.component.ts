import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {RecipeIngredient} from "../../../dtos/cookingDtos/recipeIngredient";
import {ItemDto} from "../../../dtos/item";
import {of} from "rxjs";
import {CookingService} from "../../../services/cooking.service";
import {ItemService} from "../../../services/item.service";

@Component({
  selector: 'app-matching-modal',
  templateUrl: './matching-modal.component.html',
  styleUrls: ['./matching-modal.component.scss']
})
export class MatchingModalComponent implements OnInit {
  @Input() ingredient: RecipeIngredient;
  availableItems: ItemDto[];

  constructor(public activeModal: NgbActiveModal, private itemService: ItemService) {
  }

  ngOnInit(): void {
  }

  formatGeneralName(item: ItemDto | null): string {
    return item ? item as any as string : '';
  }

  generalNameSuggestions = (input: string) => (input === '')
    ? of([])
    : this.itemService.findByGeneralName(input);


}
