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
  items?: ItemDto[];
  isRepeating?: boolean;
  interval?: Date;
}

export class DebitDto {
  user?: UserListDto;
  splitBy?: SplitBy;
  value?: number;
}

export class BalanceDebitDto {
  debtor?: UserListDto;
  creditor?: UserListDto;
  amountInCents?: number;
}


export enum SplitBy {
  EQUAL = "EQUAL",
  UNEQUAL = "UNEQUAL",
  PERCENTAGE = "PERCENTAGE",
  PROPORTIONAL = "PROPORTIONAL",
}


