import {Component, EventEmitter, Input, Output} from '@angular/core';


@Component({
  selector: 'app-confirm-delete-dialog',
  templateUrl: './confirm-delete-dialog.component.html',
  styleUrls: ['./confirm-delete-dialog.component.scss'],
})
export class ConfirmDeleteDialogComponent {

  @Input() deleteInfo = '?';
  @Input() deleteName = '';
  @Input() deleteId = '';

  @Output() confirm = new EventEmitter<void>();

  constructor() {
  }

  getIdFormatForDeleteModal(): string {
    console.log(`${this.deleteName}${this.deleteId}`.replace(/[^a-zA-Z0-9]+/g, '') + "FROM DELETE-MODAL")
    return `${this.deleteName}${this.deleteId}`.replace(/[^a-zA-Z0-9]+/g, '');
  }

}
