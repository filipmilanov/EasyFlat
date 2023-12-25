import {Component, OnInit} from '@angular/core';
import {EventDto} from "../../../dtos/event";
import {CookbookMode} from "../../cookbook/cookbook-create/cookbook-create.component";
import {NgForm} from "@angular/forms";
import {Observable} from "rxjs";
import {RecipeSuggestion} from "../../../dtos/cookingDtos/recipeSuggestion";
import {CookingService} from "../../../services/cooking.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {UnitService} from "../../../services/unit.service";
import {EventsService} from "../../../services/events.service";


export enum EventsMode {
  create,
  edit
}

@Component({
  selector: 'app-events-create',
  templateUrl: './events-create.component.html',
  styleUrls: ['./events-create.component.scss']
})
export class EventsCreateComponent implements OnInit {
  event: EventDto = {
    title: '',
    description: '',
    date: '',
  };
  mode: EventsMode = EventsMode.create;

  constructor(
    private eventService: EventsService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }


  public get submitButtonText(): string {
    switch (this.mode) {
      case EventsMode.create:
        return 'Create';
      case EventsMode.edit:
        return 'Update';
      default:
        return '?';
    }
  }

  ngOnInit(): void {

  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case EventsMode.create:
        return 'created';
      case EventsMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  public get heading(): string {
    switch (this.mode) {
      case EventsMode.create:
        return 'Create Recipe';
      case EventsMode.edit:
        return 'Edit Recipe';
      default:
        return '?';
    }
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.event);

    if (form.valid) {
      let observable: Observable<EventDto>;
      switch (this.mode) {
        case EventsMode.create:
          observable = this.eventService.createEvent(this.event);
          break;
        case EventsMode.edit:
          observable = this.eventService.createEvent(this.event);
          break;
        default:
          console.error('Unknown EventMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Event ${this.event.title} successfully ${this.modeActionFinished}`, "Success");
          this.router.navigate(['/events']);
        },
        error: error => {
          console.error(error);
        }
      });
    }

  }
}
