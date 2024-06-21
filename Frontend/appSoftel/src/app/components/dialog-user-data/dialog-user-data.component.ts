import { DialogRef } from '@angular/cdk/dialog';
import { Component, Inject, Input } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { User } from '../../classes/User';
import { UserService } from '../../services/user.service';

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
  constructor(
    public dialogRef: DialogRef<DialogUserDataComponent>,
    @Inject(MAT_DIALOG_DATA) public user: {user: User, intro: boolean},
    private userServ: UserService
  ){}

  validCiWithPattern(value: string){
    this.validCi = /^[0-9]{11}$/.test(value);
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
      this.userServ.putUpdateUser().subscribe();
    }
    this.dialogRef.close();
  }
}
