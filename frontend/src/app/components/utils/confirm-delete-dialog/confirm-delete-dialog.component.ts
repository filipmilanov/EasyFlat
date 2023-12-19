import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-confirm-delete-dialog',
  templateUrl: './confirm-delete-dialog.component.html',
  styleUrls: ['./confirm-delete-dialog.component.scss'],
})
export class ConfirmDeleteDialogComponent implements OnInit {

    @Input() deleteInfo = '?';
    @Input() deleteName = '';

    @Output() confirm = new EventEmitter<void>();

    constructor() {
    }

    ngOnInit(): void {
    }

}
