import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Seaa } from '../../classes/Seaa';

@Component({
  selector: 'app-dialog-confirm',
  templateUrl: './dialog-confirm.component.html',
  styleUrls: ['./dialog-confirm.component.css']
})
export class DialogConfirmComponent {
  seaaSelected: Seaa | undefined;
  constructor(
    public dialogRef: MatDialogRef<DialogConfirmComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      message: string, 
      oneButton: boolean, 
      type: number, 
      radio?: boolean,
      seaas?: Seaa[]}
  ) {}
}
