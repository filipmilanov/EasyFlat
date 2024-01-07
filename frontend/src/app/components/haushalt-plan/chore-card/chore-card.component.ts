import {Component, Input, OnInit} from '@angular/core';
import {UserDetail} from "../../../dtos/auth-request";

@Component({
  selector: 'app-chore-card',
  templateUrl: './chore-card.component.html',
  styleUrls: ['./chore-card.component.scss']
})
export class ChoreCardComponent implements OnInit{
  @Input() choreName: string;
  @Input() description: string;
  @Input() endDate: Date;
  @Input() points: number;
  @Input() user: String;

  ngOnInit() {
    console.log('Chore Name:', this.choreName);
    console.log('Description:', this.description);
    console.log('End Date:', this.endDate);
    console.log('Points:', this.points);
    console.log('User:', this.user);
  }

}
