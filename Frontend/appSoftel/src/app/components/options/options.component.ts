import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Seaa } from '../../classes/Seaa';
import { UserService } from '../../services/user.service';
import { User } from '../../classes/User';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { moveAnimation, expandHeight, moveForm, moveInfoPerfil, opacity } from '../../animations/animations';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AlertComponent } from '../alert/alert.component';
import { MatDialog } from '@angular/material/dialog';
import { ModalUserQueryErrorComponent } from '../modal-user-query-error/modal-user-query-error.component';
import { DialogConfirmComponent } from '../dialog-confirm/dialog-confirm.component';


@Component({
  selector: 'app-options',
  templateUrl: './options.component.html',
  styleUrls: ['./options.component.css'],
  animations: [moveAnimation, expandHeight, moveForm, moveInfoPerfil, opacity],
})
export class OptionsComponent implements OnInit {
  @ViewChild('hiddenForm') form: ElementRef | undefined;
  seaa: Seaa = {
    name: '',
    problem: '',
    option: '',
    description: '',
    year: 0,
    version: '',
  };

  user: User | undefined;

  usersSelected: User[] = [];
  seaaSelected: Seaa = {
    name: '',
    problem: '',
    option: '',
    description: '',
    year: 0,
    version: '',
  };
  isCompletedStep1: boolean = false;
  isCompletedStep2: boolean = false;

  show: boolean = false;

  loadSpinner: boolean = false;
  loadSpinnerStepper: boolean = false;

  eventFile: Event | undefined;
  isFileSelected: boolean = false;

  textInfoLoading: string = ''
  countPts: number = 0;

  formUserUpdate: FormGroup | undefined;

  hide: boolean = true;
  hideC: boolean = true;

  showForm: boolean = false;

  constructor(
    protected userServ: UserService,
    private snack: MatSnackBar,
    private dialog: MatDialog
  ) {
  }

  async ngOnInit() {
    this.userServ.userObserver.subscribe((val) => this.user = val);
    this.formUserUpdate = new FormGroup({
      'name': new FormControl(this.user?.name, [Validators.required, Validators.pattern('^[A-Z][a-z]+')]),
      'lastName': new FormControl(this.user?.lastName, [Validators.required, Validators.pattern(/^[A-Z][a-z]+(?:\s[A-Z][a-z]+)+$/)]),
      'phoneNumber': new FormControl(this.user?.phoneNumber, [Validators.required, Validators.pattern('^[0-9]{8}$')]),
      'password': new FormControl(this.user?.password, [Validators.required, Validators.minLength(8), Validators.pattern(/^(?=(.*\d){2})(?=.*[A-Z])(?=.*[!@#$%&*()_+]+)(?=.*[0-9]).{8,}$/)]),
      'confirmPassword': new FormControl('', Validators.required)
    }, {validators: this.passwordMatchValidator('password', 'confirmPassword')});
  }

  passwordMatchValidator(controlName: string, matchingControlName: string): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      const input = control.get(controlName);
      const matchingInput = control.get(matchingControlName);

      if (input && matchingInput && input.value !== matchingInput.value) {
        return { 'mismatch': true };
      }

      return null;
    };
  }

  submit() {
    if (this.formUserUpdate?.valid) {
      let dialogRef = this.dialog.open(DialogConfirmComponent, {data: {
        message: '¿ Desea realizar esta acción ?'
      }});

      dialogRef.afterClosed().subscribe(val => {
        if(val){
          let {confirmPassword, ...otherFields} = this.formUserUpdate!.value;
          Object.assign(this.user!, otherFields);
          this.userServ.putUpdateUser().subscribe({
            error: () => this.snack.open('No se guardaron los datos en el sistema',undefined, {duration: 1000}),
            complete: () => this.snack.openFromComponent(AlertComponent, {data: 'Datos guardados correctamente',duration: 1000})
          });
        }
      })
    }
  }

  send() {
    if(this.user?.specialist){
      console.log(this.user);
      this.loadSpinner = true;

      const interval = this.animatedTextInfo('Guardando los datos del Sistema Experto');

      this.userServ.postCreateSeaa(this.seaa).subscribe({
        next: (s) => console.log(s),
        error: () => this.errorSavedSeaa(interval),
        complete: () => {

          clearInterval(interval);

          this.sendFile(this.eventFile!, () => {

            this.loadSpinner = false;
            let snackRef = this.snack.openFromComponent(AlertComponent, {
              data: 'Seaa creado y asociado',
              duration: 2000,
            });
            snackRef.afterDismissed().subscribe(() => {
              while(this.textInfoLoading){
                this.textInfoLoading = ''
              }
            });
          })
        },
      });
    }else{
      this.snack.open('No eres Especialista para realizar esta opración');
    }
  }

  animatedTextInfo(text:string){
    const interval = setInterval(() => this.addPtsAndDeletePts(), 800);
    this.textInfoLoading = text;
    return interval;
  }

  addPtsAndDeletePts() {
    if (this.countPts === 3) {
      this.textInfoLoading = this.textInfoLoading.replace(/\.{3}/g, "");
      this.countPts = 0;
    } else {
      this.textInfoLoading = "." + this.textInfoLoading;
      this.countPts++;
    }
  }

  errorSavedSeaa(interval: ReturnType<typeof setInterval>){
    clearInterval(interval);
    this.dialog
          .open(ModalUserQueryErrorComponent)
          .afterClosed()
          .subscribe(() => {
            this.loadSpinner = false;
            this.snack.open('No fue realizada la acción correctamente', '', {
              duration: 2000,
            });
            this.textInfoLoading = ''
          })
  }

  save() {
    this.loadSpinnerStepper = true;
    this.userServ
      .postShareSeaa(
        this.usersSelected.map((user) => user.id!),
        this.seaaSelected?.id!,
        this.user?.id!
      )
      .subscribe({
        next: (s) => {
          this.seaaSelected = s;
          console.log(s);
        },
        error: () =>
          this.dialog
            .open(ModalUserQueryErrorComponent)
            .afterClosed()
            .subscribe(() => {
              this.loadSpinnerStepper = false;
              this.snack.open('No fue realizada la acción correctamente', '', {
                duration: 2000,
              });
            }),
        complete: () => {
          this.loadSpinnerStepper = false;
          this.snack.openFromComponent(AlertComponent, {
            data: 'Seaa compartido a los usuarios seleccionados',
            duration: 2000,
          });
        },
      });
  }

  getUsersSelected(users: User[]) {
    if (users) {
      this.usersSelected = users;
      this.isCompletedStep2 = this.usersSelected.length != 0;
    }
  }

  isSpecialist() {
    return this.user?.roles.some((rol) => rol.name == 'ROLE_EXPERT');
  }

  doneAnimation() {
    this.show = true;
  }

  requestSpecialist() {
    this.userServ.getRequestToBeSpecialist().subscribe({
      complete: () =>
        this.snack.openFromComponent(AlertComponent, {
          data: 'Solicitud enviada',
          duration: 1500,
        }),
      error: () =>
        this.snack.open(
          'No se realizó la solicitud correctamente',
          'Reintentar',
          { duration: 2000 }
        ),
    });
  }

  sendFile(event: Event, success: ()=> void) {
    const interval = this.animatedTextInfo('Descomprimiendo el Sistema Experto');

    const inputElement = event.target as HTMLInputElement;
    if (inputElement.files != null) {
      const file: File = inputElement.files[0];

      const formData = new FormData();
      formData.append('file', file, file.name);

      this.userServ.postUploadSeaaRarOrZip(formData, this.seaa.name).subscribe({
        error: () => this.errorSavedSeaa(interval),
        complete: () => {
          clearInterval(interval);
          success();
        }
      });
    }
  }

  loadFile(event: Event){
    const inputElement = event.target as HTMLInputElement;
    if (inputElement.files != null) {
      this.eventFile = event;
      this.isFileSelected = true;
    }else this.isFileSelected = false;
  }
}
