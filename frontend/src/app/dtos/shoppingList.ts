import {ShoppingItemDto} from "./item";

export class ShoppingListDto{
  constructor(
    public id: number,
    public listName: string,
    public items?: ShoppingItemDto[],
  ) {}
}




