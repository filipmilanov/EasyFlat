import {Component, Input} from '@angular/core';
import {UserDetail} from "../../../dtos/auth-request";

@Component({
  selector: 'app-chore-preference-card',
  templateUrl: './chore-preference-card.component.html',
  styleUrls: ['./chore-preference-card.component.scss']
})
export class ChorePreferenceCardComponent {
  @Input() first: string;
  @Input() second: string;
  @Input() third: string;
  @Input() fourth: string;
}
