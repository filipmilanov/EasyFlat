import {Component, Input} from '@angular/core';
import * as events from "events";
import {EventDto} from "../../../dtos/event";

@Component({
  selector: 'app-event-card',
  templateUrl: './event-card.component.html',
  styleUrls: ['./event-card.component.scss']
})
export class EventCardComponent {
@Input() event:EventDto;

}
