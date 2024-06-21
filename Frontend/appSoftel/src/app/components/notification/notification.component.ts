import { Component, EventEmitter, Input, Output } from '@angular/core';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent {
  @Input() idNotif: number | undefined ;
  @Input() icon: boolean = true;
  @Input() type: notificationType = 'info';
  @Input() title: string = 'Nueva notificación';
  @Input() content: string = 'Ha obtenido una nueva notificación';
  @Input() date: boolean = true;
  @Input() dateTime: Date | undefined;
  @Input() view: boolean = false;
  @Input() showAction: boolean | undefined;
  @Output() confirm = new EventEmitter();

  constructor(private userSev: UserService){}

  changeView(){
    if (this.idNotif != undefined) {
      if (!this.view) {
        this.userSev.getView(this.idNotif).subscribe(); 
        this.view = true;
      }
    }
  }
}

export type notificationType = 'info' | 'create' | 'error' | 'request';