import {Component, OnInit} from '@angular/core';
import {EventDto} from "../../dtos/event";
import {add} from "lodash";
import {EventsService} from "../../services/events.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss']
})
export class EventsComponent implements OnInit {
 events: EventDto[];

  constructor(
    private eventService: EventsService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

ngOnInit() {
  this.loadEvents();
}


loadEvents(){
  this.eventService.getEvents().subscribe({
    next: res => {
      this.events = res;
    },
    error: err => {
      console.error("Error loading events:", err);
      this.notification.error("Error loading events");
    }
  })
}

  addTestData() {
    // Create some test events and push them to the events array
    const event1: EventDto = {
      title: 'Event 1',
      description: 'Description for Event 1',
      date: '2023-01-01'
    };

    const event2: EventDto = {
      title: 'Event 2',
      description: 'Description for Event 2',
      date: '2023-02-01'
    };

    // Add more test events if needed

    this.events = [event1, event2];
  }
}
