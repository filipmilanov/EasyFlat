import {Injectable} from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {ItemDto} from "../dtos/item";
import {Observable} from "rxjs";
import {EventDto} from "../dtos/event";

@Injectable({
  providedIn: 'root'
})
export class EventsService {


  baseUri = "http://localhost:8080/api/v1/events"

  constructor(
    private http: HttpClient,
  ) {
  }


  /**
   * Persists Events
   *
   * @param event to persist
   */
  createEvent(event: EventDto): Observable<EventDto> {
    console.log('Create event with content ' + event);
    return this.http.post<EventDto>(this.baseUri, event);
  }


}
