import {DigitalStorageDto} from "./digitalStorageDto";
import {IngredientDto} from "./ingredientDto";

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
