import {OrderType} from "./orderType";

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
  alwaysInStock:boolean;
  productName?:string;
  brand?:string;
  fillLevel?:string;
  expireDateStart?:string;
  expireDateEnd?:string;
  orderBy: OrderType;
}

export class StorageItemListDto {
  generalName?:string;
  quantityCurrent:number;
  storId:string;
}
