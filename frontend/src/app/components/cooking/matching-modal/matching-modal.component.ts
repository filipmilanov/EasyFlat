import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import {RecipeIngredient} from "../../../dtos/cookingDtos/recipeIngredient";
import {ItemDto} from "../../../dtos/item";
import {map, Observable, of} from "rxjs";
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
    return item ? item.productName : '';
  }

  nameSuggestions = (input: string): Observable<any[]> => {
    if (!input.trim()) {
      return of([]); // Return an empty array if the input is empty or contains only whitespaces
    }

    const suggestions$ = this.itemService.findByName(input);

    return suggestions$.pipe(
      map(suggestions => Array.isArray(suggestions) ? suggestions : [suggestions])
    );
  }

}
