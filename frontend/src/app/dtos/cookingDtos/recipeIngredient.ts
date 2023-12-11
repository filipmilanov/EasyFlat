import {Unit} from "../unit";

export class RecipeIngredient {

  id?: number;
  name?: string;
  unit?: string;
  unitDto?:Unit;
  amount?:number;
}
