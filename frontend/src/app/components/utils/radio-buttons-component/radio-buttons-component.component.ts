import {Component} from '@angular/core';

@Component({
  selector: 'app-radio-buttons-component',
  templateUrl: './radio-buttons-component.component.html',
  styleUrls: ['./radio-buttons-component.component.scss']
})
export class RadioButtonsComponentComponent {
  @Input() form: FormGroup;
  @Input() options: { value: any; label: string }[];
}
