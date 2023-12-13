import {DigitalStorageDto} from "./digitalStorageDto";
import {IngredientDto} from "./ingredientDto";
import {Unit} from "./unit";

export class ItemDto {
  itemId?: number;
  ean?: string;
  generalName?: string;
  productName?: string;
  brand?: string;
  quantityCurrent?: number;
  quantityTotal?: number;
  unit?: Unit;
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
