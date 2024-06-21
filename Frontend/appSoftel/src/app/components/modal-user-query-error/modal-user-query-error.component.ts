import { DialogRef } from '@angular/cdk/dialog';
import { Component } from '@angular/core';

@Component({
  selector: 'app-modal-user-query-error',
  templateUrl: './modal-user-query-error.component.html',
  styleUrls: ['./modal-user-query-error.component.css']
})
export class ModalUserQueryErrorComponent {
  constructor(
    public dialogRef: DialogRef<ModalUserQueryErrorComponent>
  ){}
}
