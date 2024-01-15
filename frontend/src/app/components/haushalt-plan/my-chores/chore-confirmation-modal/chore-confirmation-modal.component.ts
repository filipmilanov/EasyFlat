// chore-confirmation-modal.component.ts
import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-chore-confirmation-modal',
  templateUrl: './chore-confirmation-modal.component.html',
  styleUrls: ['./chore-confirmation-modal.component.scss']
})
export class ChoreConfirmationModalComponent {
  @Input() choreName: string;
  repeatDate: string;

  constructor(public activeModal: NgbActiveModal) {}

  closeModal() {
    this.activeModal.close(false); // No
  }

  confirmRepeat() {
    this.activeModal.close({ repeat: true, date: this.repeatDate }); // Yes
  }
}
