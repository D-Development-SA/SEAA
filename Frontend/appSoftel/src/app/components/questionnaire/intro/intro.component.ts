import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from 'src/app/services/user.service';
import { Seaa } from 'src/app/classes/Seaa';
import { User } from 'src/app/classes/User';
import { DialogUserDataComponent } from 'src/app/components/dialog-user-data/dialog-user-data.component';

@Component({
  selector: 'app-intro',
  templateUrl: './intro.component.html',
  styleUrls: ['./intro.component.css']
})
export class IntroComponent implements OnInit{
  @Input() start: boolean | undefined;
  @Output() hideEvent: EventEmitter<any> = new EventEmitter();
  seaaSelected: Seaa = {
    name: '',
    problem: '',
    option: '',
    description: '',
    year: 0,
    version: ''
  };
  user: User = {
    name: '',
    lastName: '',
    password: '',
    enabled: false,
    roles: [],
    queries: [],
    phoneNumber: ''
  };
  checkRolSpecialist: boolean = false;
  seaas: Seaa[] = [];

  constructor(private userServ: UserService, private dialog: MatDialog){}

  ngOnInit(): void {
    this.userServ.userObserver.subscribe(val => this.user = val);
    this.userServ.getAllSeaa().subscribe({
      next: val => this.seaas = val,
      error: err => {
        if (err instanceof HttpErrorResponse) {
          console.log(err.error);
        }
      }
    });

    this.checkRolSpecialist = this.user.roles.some(rol => rol.name == 'ROLE_EXPERT');

    if (this.user.specialist != null && !(this.user.specialist.ci || this.user.specialist.seaaList.length) && this.checkRolSpecialist) {
      this.dialog.open(DialogUserDataComponent, {data: {user: this.user, intro: true}});
    }
  }

  startQuery(): void{
    this.hideEvent.emit({show: true, seaa: this.seaaSelected});
    this.start = true;
  }

}
