import {OrderType} from "./OrderType";

export class StorageItemList {
  itemId?: string;
  ean?:string;
  generalName?:string;
  productName?:string;
  brand?:string;
  quantityCurrent:number;
  quantityTotal?:number;
  unit?:number;
  expireDate?:string;
  description?:string;
  priceInCent?:number;
}

export class ItemSearchDto {
  itemId?: string;
  productName?:string;
  brand?:string;
  fillLevel?:number;
  expireDateStart?:string;
  expireDateEnd?:string;
  orderBy: OrderType;
}

