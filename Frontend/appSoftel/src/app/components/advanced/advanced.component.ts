import { Component, OnInit, ViewChild } from '@angular/core';
import { User } from '../../classes/User';
import { MatAccordion, MatExpansionPanel } from '@angular/material/expansion';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Rol } from '../../classes/Rol';
import { UserService } from '../../services/user.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogUserDataComponent } from '../dialog-user-data/dialog-user-data.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AlertComponent } from '../alert/alert.component';
import { DialogConfirmComponent } from '../dialog-confirm/dialog-confirm.component';
import { ModalUserQueryErrorComponent } from '../modal-user-query-error/modal-user-query-error.component';
import { moveAnimation, panelAnimation } from '../../animations/animations';


type filterUser = 'name' | 'lastName' | 'phoneNumber' | 'date';

@Component({
  selector: 'app-advanced',
  templateUrl: './advanced.component.html',
  styleUrls: ['./advanced.component.css'],
  animations: [panelAnimation, moveAnimation],
})
export class AdvancedComponent implements OnInit {
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;
  users: User[] = [];
  usersFiltered: User[] = [];
  oldUsers: User[] = [];
  disabledBtn: boolean[] = [];
  disabledBtnAll: boolean = false;
  filteredRoles: Rol[] = [];
  myControl = new FormControl('');
  roles: Rol[] = [];
  load: boolean = false;
  show: boolean[] = [];
  deleteExpansion: boolean[] = [];
  loadSpinnerADdSpecialist: boolean[] = [];
  confirmDialog: any = {
    data: {
      message: '¿Estás seguro de realizar esta acción?',
      oneButton: false,
      type: 1,
    },
  };

  filt: filterUser = 'name';
  range = new FormGroup({
    start: new FormControl<Date | null>(null),
    end: new FormControl<Date | null>(null),
  });

  validPattern: boolean[][] = [];

  constructor(
    private userServ: UserService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {
    this.filteredRoles = this.roles.slice();
    this.usersFiltered = this.users.slice();
  }

  async ngOnInit() {
    this.disabledBtn.splice(0);
    this.filteredRoles.splice(0);
    this.roles.splice(0);
    this.show.splice(0);
    this.deleteExpansion.splice(0);
    this.users.splice(0);
    this.oldUsers.splice(0);
    this.validPattern.splice(0);
    this.loadSpinnerADdSpecialist.splice(0);
    this.usersFiltered.splice(0);

    this.load = true;
    this.users = (await this.userServ.getFindAllUser());
    this.users = this.users.sort((a, b) => a.id! - b.id!);
    this.usersFiltered = [...this.users];
    this.oldUsers = JSON.parse(JSON.stringify(this.users));

    console.log(this.users);

    for (let index = 0; index < this.users.length; index++) {
      this.show.push(false);
      this.deleteExpansion.push(true);
      this.disabledBtn.push(false);
      this.loadSpinnerADdSpecialist.push(false);
      this.validPattern.push([true, true, true, true]);
    }

    this.userServ.getAllRoles().subscribe((roles) => (this.roles = roles));
    this.load = false;
  }

  toggleEnabledUser(expansion: MatExpansionPanel, index: number) {
    if (expansion.expanded) {
      expansion.close();
    } else {
      expansion.open();
    }

    if (this.users[index].id) {
      this.userServ
        .getEnabledUser(this.users[index].id!)
        .subscribe((val) => (this.users[index].enabled = val));
    } else {
      this.users[index].enabled = !this.users[index].enabled;
    }
  }

  addRol(user: User): void {
    if (!user.roles.find((value) => value.name == this.myControl.value)) {
      user.roles.push(
        this.roles.filter((value) => value.name == this.myControl.value)[0]
      );
    }
  }

  deleteRol(index: number, user: User): void {
    user.roles.splice(index, 1);
    if (user.roles.length == 0) {
      user.roles.push(this.roles.filter((r) => r.name.includes('USER'))[0]);
    }
  }

  filter(input: HTMLInputElement): void {
    const filterValue = input.value.toLowerCase();
    this.filteredRoles = this.roles.filter((r) =>
      r.name.toLowerCase().includes(filterValue)
    );
  }

  addUser(): void {
    const user: User = {
      name: '',
      lastName: '',
      password: '',
      enabled: false,
      roles: [],
      queries: [],
      phoneNumber: '',
    };
    this.show.push(false);
    this.deleteExpansion.push(true);
    this.loadSpinnerADdSpecialist.push(false);
    this.validPattern.push([false, false, false, false]);
    this.users.push(user);
    this.usersFiltered.push(user);
  }

  cancel(): void {
    this.users = JSON.parse(JSON.stringify(this.oldUsers));
  }

  cancelUser(user: User, index: number) {
    let userArray = this.oldUsers.filter((val) => user.id === val.id);
    console.log(userArray);

    if (userArray.length == 0) {
      this.users[index] = {
        name: '',
        lastName: '',
        password: '',
        enabled: false,
        roles: [],
        queries: [],
        phoneNumber: '',
      };
    } else if (userArray.length == 1) {
      this.users[index] = JSON.parse(JSON.stringify(userArray[0]));
    } else {
      throw Error('Many users with the same id');
    }
  }

  saveUser(i: number): void {
    this.disabledBtn[i] = true;
    let dialogRef = this.dialog.open(
      DialogConfirmComponent,
      this.confirmDialog
    );
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        if (this.users[i].id != undefined) {
          this.userServ.updateUser(this.users[i]).subscribe({
            next: (user) => {
              this.informateProcessSaveUser(user, i);
            },
            error: (err) => {
              this.infoProcessErrorUserAndSave(err, i);
            },
            complete: () => this.refresh(),
          });
        } else {
          this.userServ.postCreateUser(this.users[i]).subscribe({
            next: (user) => {
              this.informateProcessSaveUser(user, i);
            },
            error: (err) => {
              this.infoProcessErrorUserAndSave(err, i);
            },
            complete: () => this.refresh(),
          });
        }
      } else {
        this.disabledBtn[i] = false;
      }
    });
  }

  informateProcessSaveUser(user: User, i: number) {
    this.users[i] = user;
    this.disabledBtn[i] = false;
    this.snackBar.openFromComponent(AlertComponent, {
      data: 'Usuario ' + this.users[i].name + ' guardado correctamente',
      duration: 5000,
    });
  }

  infoProcessErrorUserAndSave(err: any, i: number) {
    console.log('error');

    console.log(err);

    this.dialog.open(ModalUserQueryErrorComponent, err);
    this.disabledBtn[i] = false;
    this.refresh();
  }

  saveAllUser(): void {
    this.disabledBtnAll = true;
    let dialogRef = this.dialog.open(
      DialogConfirmComponent,
      this.confirmDialog
    );
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.userServ.postSaveAllUser(this.users).subscribe({
          next: (val) => {
            this.users = val;
            this.oldUsers = this.users.slice();
            this.disabledBtnAll = false;
            this.snackBar.openFromComponent(AlertComponent, {
              data: 'Usuarios guardados correctamente',
              duration: 5000,
            });
          },
          error: (err) => {
            this.dialog.open(ModalUserQueryErrorComponent, err);
          },
        });
      }
    });
  }

  openDialog(indexUser: number) {
    this.dialog.open(DialogUserDataComponent, {
      data: { user: this.users[indexUser] },
      height: '33rem',
      width: '66rem',
    });
  }

  deleteUser(index: number, expansion: MatExpansionPanel) {
    expansion.open();

    let dialogRef = this.dialog.open(
      DialogConfirmComponent,
      this.confirmDialog
    );

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.userServ.deleteUser(this.users[index].id!).subscribe({
          next: () => {
            this.snackBar.openFromComponent(AlertComponent, {
              data: 'Usuario borrado correctamente',
              duration: 5000,
            });

            this.deleteExpansion[index] = false;
            this.users.splice(index, 1);
            this.usersFiltered.splice(index, 1);
            this.deleteExpansion.splice(index, 1);
            this.show.splice(index, 1);
            this.validPattern.splice(index, 1);
            this.loadSpinnerADdSpecialist.splice(index, 1);
          },
          error: (err) => {
            let dialogRef = this.dialog.open(ModalUserQueryErrorComponent, err);
            dialogRef.afterClosed().subscribe(() => this.refresh());
          },
          complete: () => this.refresh(),
        });
      }
    });
  }

  doneAnimationMain() {
    this.show.forEach((value) => (value = true));
  }

  doneAnimation(event: any, index: number) {
    if (event.toState != 'void') {
      this.show[index] = true;
    }
  }

  validWithPattern(index: number, name: string) {
    switch (name) {
      case 'name':
        this.validPattern[index][0] = /^[A-Z][a-z]+$/.test(
          this.users[index].name
        );
        break;
      case 'lastName':
        this.validPattern[index][1] = /^[A-Z][a-z]+(?:\s[A-Z][a-z]+)+$/.test(
          this.users[index].lastName
        );
        break;
      case 'password':
        this.validPattern[index][2] =
          /^(?=.*\d{2})(?=.*[A-Z])(?=.*[!@#$%&*()_+.]{2})(?=.*[0-9]).{8,}$/.test(
            this.users[index].password
          );
        break;
      case 'phoneNumber':
        this.validPattern[index][3] = /^[0-9]{8}$/.test(
          this.users[index].phoneNumber
        );
        break;
    }
  }

  isAdmin(user: User) {
    return user.roles.some((rol) => rol.name == 'ROLE_ADMIN');
  }
  isSpecialist(user: User) {
    return user.roles.some((rol) => rol.name == 'ROLE_EXPERT');
  }

  async refresh() {
    await this.ngOnInit();
  }

  assignedAsSpecialist(index: number) {
    this.loadSpinnerADdSpecialist[index] = true;
    if (this.users[index].id != undefined) {
      this.userServ.getAddRolSpecialist(this.users[index].id!).subscribe({
        next: (user) => {
          this.users[index] = user;
          this.snackBar.openFromComponent(AlertComponent, {
            data: 'Asignación realizada',
            duration: 2000,
          });
          this.loadSpinnerADdSpecialist[index] = false;
        },
        error: () => {
          this.snackBar.open('Hubo un error al confirmar', 'Reintentar', {
            duration: 5000,
          });
          this.loadSpinnerADdSpecialist[index] = false;
        },
      });
    } else {
      let dialogRef = this.dialog.open(DialogConfirmComponent, {
        data: {
          message:
            'Primero debe crear el usuario para asignarlo como especialista',
          oneButton: true,
          type: 1,
        },
      });
      dialogRef
        .afterClosed()
        .subscribe(() => (this.loadSpinnerADdSpecialist[index] = false));
    }
  }

  doFilter(input?: HTMLInputElement) {
    const inputValue: string | undefined = input?.value.toLocaleLowerCase();
    const startDate = this.range.value.start;
    const endDate = this.range.value.end;

    switch (this.filt) {
      case 'name':
        this.usersFiltered = this.users.filter((user) =>
          user.name.toLocaleLowerCase().includes(inputValue!)
        );
        break;
      case 'lastName':
        this.usersFiltered = this.users.filter((user) =>
          user.lastName.toLocaleLowerCase().includes(inputValue!)
        );
        break;
      case 'date':
        if (startDate && endDate) {
          this.usersFiltered = this.users.filter(
            (user) =>
              new Date(user.date!) >= startDate &&
              new Date(user.date!) <= endDate
          );
        } else this.usersFiltered = [...this.users];
        break;
      case 'phoneNumber':
        this.usersFiltered = this.users.filter((user) =>
          user.phoneNumber.toLocaleLowerCase().includes(inputValue!)
        );
        break;
    }
  }
}
