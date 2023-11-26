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
  priceInCent?: number;
  alwaysInStock: boolean;
  ingredients: [string];
}
