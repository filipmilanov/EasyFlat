import {SharedFlat} from "./sharedFlat";
import {UserDetail} from "./auth-request";
import {ItemDto} from "./item";

export class ExpenseDto {
  id?: number;
  title?: string;
  description?: string;
  amountInCents?: number;
  createdAt?: Date;
  paidBy?: UserDetail;
  debitUsers?: DebitDto[];
  sharedFlat?: SharedFlat;
  items?: ItemDto[];
  isRepeating?: boolean;
  interval?: Date;
}

export class DebitDto {
  user?: UserDetail;
  splitBy?: SplitBy;
  value?: number;
}

export enum SplitBy {
  EQUAL = "EQUAL",
  UNEQUAL = "UNEQUAL",
  PERCENTAGE = "PERCENTAGE",
  PROPORTIONAL = "PROPORTIONAL",
}

