import {DigitalStorageDto} from "./digitalStorageDto";

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
  ingredients?: [string];
  digitalStorage?: DigitalStorageDto;
}
