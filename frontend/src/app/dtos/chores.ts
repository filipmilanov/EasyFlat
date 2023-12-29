import {UserDetail} from "./auth-request";
import {SharedFlat} from "./sharedFlat";

export class ChoresDto {
  choreName: string;
  description: string;
  endDate?: Date;
  points: number;
  user: UserDetail;
  sharedFlat: SharedFlat;

}
