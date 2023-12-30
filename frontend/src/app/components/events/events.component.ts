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
 label: string = '';

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

findEventsByLabel(){
    this.eventService.findEventsByLabel(this.label).subscribe({
      next: res => {
        this.events = res;
      },
      error: err => {
        console.error("Error finding events:", err);
        this.notification.error("Error finding events");
      }
    });
}

exportAll() {

}


}
