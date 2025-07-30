import { DialogRef } from '@angular/cdk/dialog';
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { User } from '../../classes/User';
import { UserService } from '../../services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-dialog-user-data',
  templateUrl: './dialog-user-data.component.html',
  styleUrls: ['./dialog-user-data.component.css']
})
export class DialogUserDataComponent {
  validCi: boolean = false;
  validSeaaName: boolean = false;
  validSeaaOption: boolean = false;
  validSeaaProblem: boolean = false;

  errors: string[] = [];

  constructor(
    public dialogRef: DialogRef<DialogUserDataComponent>,
    @Inject(MAT_DIALOG_DATA) public user: {user: User, intro: boolean},
    private _snackBar: MatSnackBar,
    private userServ: UserService
  ){}

  validCiWithPattern(value: string){
    this.validCi = /^[0-9]{11}$/.test(value);
  }

  allowOnlyNumbers(event: KeyboardEvent) {
    if (/\D/.test(event.key) && event.key !== 'Backspace') {
      event.preventDefault();
    }
  }

  valid(i :number, valid: any){
    switch (i) {
      case 0:
        this.validSeaaName = valid;
        break;
      case 1:
        this.validSeaaOption = valid;
        break;
      case 2:
        this.validSeaaProblem = valid;
        break;
    }
  }

  addSeaa(){
    if(this.user.user.specialist!.seaaList.length < 1){
      this.user.user.specialist?.seaaList.push({
      name: '',
      problem: '',
      option: '',
      description: '',
      year: 0,
      version: ''
      });
    }
  }

  update(){
    if (this.user.intro) {
      this.userServ.putUpdateUser().subscribe({
        error: (err:HttpErrorResponse) => {
          this.errors = err.error.errors ? Object.values(err.error.errors) : [];
        },
        complete: () => this.dialogRef.close()
      });
    }
  }
}
