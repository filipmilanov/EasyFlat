import { Component, Input } from '@angular/core';
import {UserDetail} from "../../../dtos/auth-request";

@Component({
  selector: 'app-chore-card',
  templateUrl: './chore-card.component.html',
  styleUrls: ['./chore-card.component.scss']
})
export class ChoreCardComponent {
  @Input() choreName: string;
  @Input() description: string;
  @Input() endDate: Date;
  @Input() points: number;
  @Input() user: UserDetail;
}
