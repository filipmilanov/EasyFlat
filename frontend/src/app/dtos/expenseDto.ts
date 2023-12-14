import {SharedFlat} from "./sharedFlat";
import {ItemDto} from "./item";
import {UserListDto} from "./user";

export class ExpenseDto {
  id?: number;
  title?: string;
  description?: string;
  amountInCents?: number;
  createdAt?: Date;
  paidBy?: UserListDto;
  debitUsers?: DebitDto[];
  sharedFlat?: SharedFlat;
  items?: ItemDto[];
  isRepeating?: boolean;
  interval?: Date;
}

export class DebitDto {
  user?: UserListDto;
  splitBy?: SplitBy;
  value?: number;
}

export enum SplitBy {
  EQUAL = "EQUAL",
  UNEQUAL = "UNEQUAL",
  PERCENTAGE = "PERCENTAGE",
  PROPORTIONAL = "PROPORTIONAL",
}

