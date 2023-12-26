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


  truncateString(input: string, maxLength: number): string {
    if (input.length <= maxLength) {
      return input;
    }

    // Truncate the string and append ellipsis
    const truncated = input.substring(0, maxLength - 3);
    return truncated + '...';
  }
}
