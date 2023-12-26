import {Component, EventEmitter, Input, Output} from '@angular/core';
import * as events from "events";
import {EventDto} from "../../../dtos/event";
import {EventsService} from "../../../services/events.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-event-card',
  templateUrl: './event-card.component.html',
  styleUrls: ['./event-card.component.scss']
})
export class EventCardComponent {
  @Input() event: EventDto;
  @Output() eventDeleted: EventEmitter<void> = new EventEmitter<void>();

  constructor(
    private eventService: EventsService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  truncateString(input: string, maxLength: number): string {
    if (input.length <= maxLength) {
      return input;
    }

    // Truncate the string and append ellipsis
    const truncated = input.substring(0, maxLength - 3);
    return truncated + '...';
  }

  deleteEvent() {
    this.eventService.deleteEvent(this.event.id.toString()).subscribe({
      next: data => {
        this.notification.success(`Event ${this.event.title} successfully deleted`, "Success");
        this.eventDeleted.emit();
      },
      error: error => {
        console.log(error);
      }
    });
  }
}
