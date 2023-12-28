export class EventDto {
  id?: number;
  title: string;
  description?: string;
  date: string;
  labels?: [EventLabel];
}

export class EventLabel {
  id?: number;
  labelName?: string;
  labelColour?: string;
}
