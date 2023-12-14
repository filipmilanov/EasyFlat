import {SharedFlat} from "./sharedFlat";
import {UserDetail} from "./auth-request";

export class ExpenseDto {
  id?: number;
  title?: string;
  description?: string;
  amountInCents?: number;
  splitBy?: SplitBy;
  createdAt?: Date;
  paidBy?: UserDetail;
  debitUsers?: DebitDto[];
  sharedFlat?: SharedFlat;
}

export class DebitDto {
  isTarget: boolean;
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

