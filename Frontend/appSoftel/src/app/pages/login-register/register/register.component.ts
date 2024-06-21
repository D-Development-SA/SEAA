import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { firstValueFrom } from 'rxjs';
import { HomeService } from 'src/app/services/home.service';
import { UserService } from 'src/app/services/user.service';
import { AlertComponent } from 'src/app/components/alert/alert.component';
import { User } from 'src/app/classes/User';
import { ModalUserQueryErrorComponent } from 'src/app/components/modal-user-query-error/modal-user-query-error.component';
import {DialogConfirmComponent} from "../../../components/dialog-confirm/dialog-confirm.component";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  @Output() event: EventEmitter<boolean> = new EventEmitter();
  load: boolean = false;
  done: boolean = false;
  user: User = {
    name: '',
    lastName: '',
    password: '',
    date: undefined,
    enabled: false,
    roles: [],
    queries: [],
    phoneNumber: ''
  }

  confirmPassword: string = '';
  validName: boolean = false;
  validLastName: boolean = false;
  validPassword: boolean = false;
  validConfirmPassword: boolean = false;
  validPhoneNumber: boolean = false;
  togglePassword: boolean = false;
  toggleConfirmPassword: boolean = false;

  constructor(private userServ: UserService,
              private snackBar: MatSnackBar,
              private dialog: MatDialog,
              private homeServ: HomeService){}

  async register(){
    if (this.user.name.length <= 20 && this.user.lastName.length <= 100) {
      this.done = false;
      this.load = true;

      try {
        await firstValueFrom(this.userServ.postCreateUser(this.user));
        this.snackBar.openFromComponent(AlertComponent, {
          duration: 3000,
          data: 'Usuario creado'
        });
        this.event.emit(true);
        this.homeServ.getRegistered.next(true);

      } catch (error) {
        if (error instanceof HttpErrorResponse) {
          if (error.statusText == 'Unknown Error') {
            let snackRef = this.snackBar.open('Servidor desconectado', 'Reconectar', {duration: 4000})
            snackRef.onAction().subscribe(() => setTimeout(() => this.register()));
          } else {
            this.dialog.open(ModalUserQueryErrorComponent);
          }
        }

      } finally {
        this.load = false;
      }
    }else {
      this.dialog.open(DialogConfirmComponent, {data: {
          message: 'El nombre debe tener menos de 20 caracteres.\n Los apellidos debe tener menos de 100 caracteres.',
          oneButton: true
        }
      });
    }
  }

  validateName(){
    let name = this.user.name;
    this.validName = name.length != 0 && /^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+$/.test(name) && !name.toLocaleLowerCase().includes('admin');
  }
  validateLastName(){
    this.validLastName = this.user.lastName.length != 0 && /^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+(?:\s[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)+$/.test(this.user.lastName);
  }
  validatePassword(){
    this.validPassword = this.user.password.length != 0 && /^(?=(.*\d){2})(?=.*[A-Z])(?=.*[@#$%&*._+]+)(?=.*[0-9]).{8,}$/.test(this.user.password);
    this.validateConfirmPassword();
  }
  validateConfirmPassword(){
    this.validConfirmPassword = this.validPassword && this.user.password === this.confirmPassword;
  }
  validatePhoneNumber(){
    this.validPhoneNumber = /^[0-9]{8}$/.test(this.user.phoneNumber);
  }

}
