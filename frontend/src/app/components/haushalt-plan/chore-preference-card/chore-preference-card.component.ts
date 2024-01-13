import {Component, Input} from '@angular/core';
import {UserDetail} from "../../../dtos/auth-request";

@Component({
  selector: 'app-chore-preference-card',
  templateUrl: './chore-preference-card.component.html',
  styleUrls: ['./chore-preference-card.component.scss']
})
export class ChorePreferenceCardComponent {
  @Input() firstName: string;
  @Input() secondName: string;
  @Input() thirdName: string;
  @Input() fourthName: string;
}
