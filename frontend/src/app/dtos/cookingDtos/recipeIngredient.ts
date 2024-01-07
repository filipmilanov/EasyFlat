import {Unit} from "../unit";

export class RecipeIngredient {

  id?: number;
  name?: string;
  unit?: string;
  unitEnum?:Unit;
  amount?:number;
  matched:boolean;
}
