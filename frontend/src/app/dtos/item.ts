import {DigitalStorageDto} from "./digitalStorageDto";
import {IngredientDto} from "./ingredientDto";
import {ShoppingLabelDto} from "./shoppingLabel";
import {ShoppingList} from "./shoppingList";
import {Unit} from "./unit";
import {ShoppingListDto} from "./shoppingList";

export class ItemDto {
  itemId?: number;
  ean?: string;
  generalName?: string;
  productName?: string;
  brand?: string;
  quantityCurrent?: number;
  quantityTotal?: number;
  unit?: string;
  expireDate?: Date;
  description?: string;
  boughtAt?: string;
  priceInCent?: number;
  alwaysInStock: boolean;
  minimumQuantity?: number;
  addToFiance: boolean;
  ingredients?: [IngredientDto];
  digitalStorage?: DigitalStorageDto;
}

export class ItemFieldSearchDto {
  generalName?: string;
  brand?: string;
  boughtAt?: string;
}

export class ShoppingItemDto extends ItemDto {
  shoppingList?: ShoppingList;
  labels?: [ShoppingLabelDto];
  check?: boolean;
}
