import {DigitalStorageDto} from "./digitalStorageDto";
import {IngredientDto} from "./ingredientDto";
import {ShoppingLabelDto} from "./shoppingLabel";
import {ShoppingListDto} from "./shoppingList";
import {OrderType} from "./orderType";

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

export class ShoppingItemDto extends ItemDto {
  shoppingList?: ShoppingListDto;
  labels?: [ShoppingLabelDto];
  check?: boolean;
}

export class ShoppingItemSearchDto {
  itemId?: string;
  productName?: string;
  label?: string;
}
