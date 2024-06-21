import { Component, ViewChild } from '@angular/core';
import { fadeIn, loginMove } from '../../animations/animations';
import { MatDialog } from '@angular/material/dialog';
import { DialogConfirmComponent } from '../../components/dialog-confirm/dialog-confirm.component';
import { MatRipple } from '@angular/material/core';

@Component({
  selector: 'app-login-register',
  templateUrl: './login-register.component.html',
  styleUrls: ['./login-register.component.css'],
  animations: [
    fadeIn,
    loginMove
  ]
})
export class LoginRegisterComponent{
  @ViewChild(MatRipple) ripple: MatRipple | undefined;
  
  title: String[] = ['¡Bienvenido!', 'Hola'];
  parraf: String[] = 
  ['Usted va a acceder a un sistema experto \n desarrollado por Softel', 
  'Usted ingresará en el sistema \n desarrollado por Softel'];
  textButton: String[] = ['Registrarse', 'Iniciar sesión'];
  loginOrRegister: Boolean = true;
  disabledRipple: boolean = true;
  registered: boolean = false;

  constructor(private dialog: MatDialog){}

  showModalIfRegisterSuccessfully(event: boolean){
    this.loginOrRegister = event;
    this.registered = event;
  }

  doneAnimationMoveLeft(){

    if(this.loginOrRegister && this.registered){
      this.disabledRipple = false;

      let dialogRef = this.dialog.open(DialogConfirmComponent, {data: {
        message: 'Ya puedes iniciar sesión con la nueva cuenta',
        oneButton: true
      }});
  
      dialogRef.afterClosed().subscribe(() => {
          this.launchRipple();
          
          setTimeout(() => {
            this.launchRipple();
            this.disabledRipple = true;
            this.registered = false;
          }, 200);
        })
    }
  }

  private launchRipple() {
    const rippleRef = this.ripple!.launch({
      persistent: true,
      centered: true
    });

    rippleRef.fadeOut();
  }
}
