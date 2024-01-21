import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-my-chore-card',
  templateUrl: './my-chore-card.component.html',
  styleUrls: ['./my-chore-card.component.scss']
})
export class MyChoreCardComponent {
  @Input() choreName: string;
  @Input() description: string;
  @Input() endDate: Date;
  @Input() points: number;
  @Input() user: string;
  @Input() completed: boolean;
}
