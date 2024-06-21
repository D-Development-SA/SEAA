import { HttpErrorResponse } from '@angular/common/http';
import {Component, EventEmitter, Output} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { UserService } from 'src/app/services/user.service';
import { DialogConfirmComponent } from 'src/app/components/dialog-confirm/dialog-confirm.component';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  @Output() register = new EventEmitter();
  phoneNumber: string = '';
  password:  string = '';
  validPhoneNumber: boolean = false;
  validPassword:  boolean = false;
  togglePassword:  boolean = false;
  load: boolean = false;

  constructor(private userServ: UserService,
    private root: Router,
    private snack: MatSnackBar,
    private dialog: MatDialog){}

  async login() {
    this.load = true;
    let password = this.password;

    try {
      await this.userServ.login(this.phoneNumber, password);

      if (this.userServ.getUser.id) {
        await this.userServ.getDataset();
      }

      await this.root.navigate(['main']);
    } catch (err) {
      if (err instanceof HttpErrorResponse) {
        if (err.statusText == 'Unknown Error') {
          let snackRef = this.snack.open('Servidor desconectado', 'Reconectar', {duration: 4000})
          snackRef.onAction().subscribe(() => setTimeout(() => this.login()));
        } else {
          this.dialog.open(DialogConfirmComponent, {
            data: {
              message: 'Número de teléfono o contraseña incorrecta.\n Tal vez usted no se encuentra en el sistema. \n¿Desea registrarse?',
              type: 'info'
            }
          }).afterClosed().subscribe(value => value ? this.register.emit() : null);

        }
      }
    } finally {
      this.load = false;
    }
  }

  validatePhoneNumber(){
    this.validPhoneNumber = /^[0-9]{8}$/.test(this.phoneNumber);
  }
  validatePassword(){
    this.validPassword = this.password.length != 0 && /^(?=(.*\d){2})(?=.*[A-Z])(?=.*[!@#$%&.*()_+]+)(?=.*[0-9]).{8,}$/.test(this.password);
  }
}
