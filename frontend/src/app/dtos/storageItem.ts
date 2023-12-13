import {OrderType} from "./orderType";
import {Unit} from "./unit";

export class StorageItem {
  itemId?: string;
  ean?: string;
  generalName?: string;
  productName?: string;
  brand?: string;
  quantityCurrent: number;
  quantityTotal?: number;
  unit?: string;
  expireDate?: Date;
  description?: string;
  priceInCent?: number;
  ean?:string;
  generalName?:string;
  productName?:string;
  brand?:string;
  quantityCurrent:number;
  quantityTotal?:number;
  unit?: Unit;
  expireDate?:string;
  description?:string;
  priceInCent?:number;
}

export class ItemSearchDto {
  itemId?: string;
  alwaysInStock: boolean;
  productName?: string;
  fillLevel?: string;
  orderBy: OrderType;
}

export class StorageItemListDto {
  generalName?: string;
  quantityCurrent: number;
  quantityTotal: number;
  storId: string;
  unit: string;
  generalName?:string;
  quantityCurrent:number;
  quantityTotal:number;
  storId:string;
  unit: Unit;
}
