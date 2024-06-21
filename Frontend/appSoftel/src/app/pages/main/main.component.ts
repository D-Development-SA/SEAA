import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import { MatDrawer } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { HomeService } from '../../services/home.service';
import { Notification } from '../../classes/Notification';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AlertComponent } from '../../components/alert/alert.component';
import { MatDialog } from '@angular/material/dialog';
import { DialogConfirmComponent } from '../../components/dialog-confirm/dialog-confirm.component';
import { LogService } from '../../services/log.service';
import { StorageService } from 'src/app/services/storage.service';
import {User} from "../../classes/User";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit, AfterViewInit{
  title = 'appSoftel';
  nameUser: string = 'Usuario Prueba';
  toggleSizeTextUp: boolean = true;
  forestOrWhiteIconButtonSideMenu: boolean = true;
  notifications: Notification[] = [];
  load: boolean = true;
  hidden: boolean = false;
  newNotification: number = 0;
  notifTotal: number = 0;
  notifStart: boolean = true;
  isAdmin: boolean = false;
  isExpert: boolean = false;
  regist: boolean = false;

  @ViewChild('drawerLeft') drawer: MatDrawer | undefined;

  buttons: Button[] = [
    {name: 'Inicio', visibleIcon: true, hovered: false, click: ()=>{this.root.navigate(['/']);}, show: true},
    {name: 'Comportamiento', visibleIcon: false, hovered: false, click: () => {this.root.navigate(['main/behavior'])}, show: this.userServ.containsRol('ROLE_EXPERT')},
    {name: 'Cuestionario', visibleIcon: false, hovered: false, click: ()=>{this.root.navigate(['main/questionnaire']);}, show: true},
    {name: 'Registro', visibleIcon: false, hovered: false, click: ()=>{this.root.navigate(['main/log']);}, show: true},
    {name: 'Avanzado', visibleIcon: false, hovered: false, click: ()=>{this.root.navigate(['main/advanced']);},show: this.userServ.containsRol('ROLE_ADMIN')},
    {name: 'Opciones', visibleIcon: false, hovered: false, click: ()=>{this.root.navigate(['main/options']);}, show: true},
    {name: 'Ayuda', visibleIcon: false, hovered: false, click: ()=>{this.root.navigate(['main/help']);}, show: true},
    {name: 'Acerca de nosotros', visibleIcon: false, hovered: false, click: ()=>{this.root.navigate(['main/about_us']);}, show: true}
  ];

  constructor(private root: Router,
              protected userServ: UserService,
              private homeServ: HomeService,
              private logServ: LogService,
              private snack: MatSnackBar,
              private dialog: MatDialog,
              private storage: StorageService){}

  ngOnInit(): void {
    this.loadBtnValueForestWhite();
    this.subscriptionsObservablesOfServices();
    this.detectFirstTime();
    this.navigateToLogPageInTheSubscriptionOfLogService();
    this.detectActivePage();
  }

  private detectActivePage() {
    let active: number = this.storage.getData('active');
    active !== null ? this.changePage(active) : undefined;
  }

  private navigateToLogPageInTheSubscriptionOfLogService() {
    this.logServ.changePageObs.asObservable().subscribe(val => {
      if (val) {
        this.changePage(2);
      }
    })
  }

  private detectFirstTime() {
    if (this.regist) {

      let dialogRef = this.dialog.open(DialogConfirmComponent, {
        data: {
          message: 'Hemos detectado su primera vez al sistema.Recomendamos que vea la sección de ayuda para mejor experiencia con la aplicación.'
            + '¿Deseas ir a la sección de AYUDA?'
        }, width: '50rem'
      });

      dialogRef.afterClosed().subscribe(confirm => {
        if (confirm) {
          this.root.navigateByUrl('/quick-start-guide');
        }

        this.regist = false;
        this.homeServ.getRegistered.next(false);
      })
    }
  }

  private subscriptionsObservablesOfServices() {
    this.userServ.userObserver.subscribe(value => {
      if (value.name) {
        this.userConfiguration(value);
      }
    })

    this.homeServ.doneHomeAnimation.subscribe(() => {
      this.drawer?.open();
    });

    this.homeServ.getChangePageToLog.asObservable().subscribe(change => change ? this.changePage(3) : undefined);

    this.homeServ.getRegistered.asObservable().subscribe(regist => this.regist = regist);
  }

  private userConfiguration(value: User) {
    this.nameUser = value.name + ' ' + value.lastName;
    this.isAdmin = value.roles.some((rol) => rol.name === 'ROLE_ADMIN');
    this.isExpert = value.roles.some((rol) => rol.name === 'ROLE_EXPERT');

    if (this.isAdmin) {
      this.userServ.notificationAux.subscribe((val) => (this.load = val));
      this.userServ.notification.subscribe((notif) => {
        this.notificationsConfiguration(notif);
      });
    }
  }

  private notificationsConfiguration(notif: Notification[]) {
    this.notifications = notif.reverse();

    if (this.notifTotal < notif.length && !this.notifStart) {
      this.newNotification += notif.length - this.notifTotal;
    }
    if (this.notifStart) {
      this.newNotification = this.notifications.filter(
        (n) => !n.view
      ).length;
      this.notifStart = false;
    }
    this.hidden = true;
    this.notifTotal = notif.length;
    setTimeout(() => (this.load = false), 2000);
  }

  private loadBtnValueForestWhite() {
    let btnValue: boolean = this.storage.getData('btntoggle');

    if (btnValue !== null) {
      this.forestOrWhiteIconButtonSideMenu = btnValue;
    } else {
      this.storage.setData('btntoggle', this.forestOrWhiteIconButtonSideMenu, false)
    }
  }

  changePage(index: number): void{
    this.toggleSizeTextUp = index == 0;

    for (let i = 0; i < this.buttons.length; i++) {
      this.buttons[i].visibleIcon = i == index;
    }

    this.storage.setData('active', index, false);
    this.buttons[index].click();
  }

  changeColorIconMouseEnter(index: number): void{
    this.buttons[index].hovered = true;
  }

  changeColorIconMouseExit(index: number): void{
    this.buttons[index].hovered = false;
  }

  actionsSideMenuLeft(drawer: MatDrawer): void{
      drawer.toggle();
      this.forestOrWhiteIconButtonSideMenu = !this.storage.getData('btntoggle');
      this.storage.setData('btntoggle', this.forestOrWhiteIconButtonSideMenu, false);
  }
  actionsSideMenuRight(drawer: MatDrawer): void{
      drawer.toggle();
      this.hidden = true;
      this.newNotification = 0;
  }

  confirmNotif(index: number){
    this.userServ.getAddRolSpecialist(this.notifications[index].iduser!).subscribe({
      next: () => {
        this.snack.openFromComponent(AlertComponent, {data: 'Confirmado', duration: 2000});
      },
      error: () => this.snack.open('Hubo un error al confirmar', 'Reintentar', {duration: 1000})
    });
  }

  clear(){
    this.storage.clear();
    this.userServ.logout();
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.storage.getData('btntoggle') ? this.drawer?.open() : this.drawer?.close();
    });
  }

}

export interface Button{
  name: string;
  visibleIcon: boolean;
  url?: string;
  hovered: boolean;
  click(): void;
  show: boolean;
}
