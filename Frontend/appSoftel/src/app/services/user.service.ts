import {Injectable} from '@angular/core';
import {User} from '../classes/User';
import {BehaviorSubject, Observable, catchError, firstValueFrom, map, of, Subscriber} from 'rxjs';
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {urls} from '../urls/urls';
import {Question} from '../classes/question';
import {Prop} from '../classes/Prop';
import {Var} from '../classes/Var';
import {Seaa} from '../classes/Seaa';
import {AnswerAndQuestion} from '../classes/AnswerAndQuestion';
import {Query} from '../classes/Query';
import {Notification} from '../classes/Notification';
import {Rol} from '../classes/Rol';
import {StorageService} from './storage.service';
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {DialogConfirmComponent} from "../components/dialog-confirm/dialog-confirm.component";
import {Log} from "../classes/Log";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  user: User = {
    name: '',
    lastName: '',
    password: '',
    enabled: false,
    roles: [],
    queries: [],
    phoneNumber: ''
  }
  private userReq: BehaviorSubject<User>;
  private graphSet: BehaviorSubject<number[]>;
  private readonly notificationObs: Observable<Notification[]>;
  private notificationObsAux: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  private first: boolean = true;

  constructor(
    private httpClient: HttpClient,
    private storage: StorageService,
    private router: Router,
    private dialog: MatDialog
  ) {
    this.userReq = new BehaviorSubject<User>(this.user);
    this.graphSet = new BehaviorSubject<number[]>([]);
    this.getUserInTheStorageAndEmitIfExist();

    this.notificationObs = new Observable<Notification[]>(observer => {
      this.buildNotifications(observer);
    })
  }

  private buildNotifications(observer: Subscriber<Notification[]>) {
    this.getNotifcationsFirstTime(observer); // Get first instance of notifications

    this.automatizedNotificationThroughRequest(observer); // Automation is only created and start to 20s
  }

  private getNotifcationsFirstTime(observer: Subscriber<Notification[]>) {
    if (this.first) {
      this.notificationObsAux.next(true);
      this.httpClient.get<Notification[]>(urls.notifications, {withCredentials: true})
        .subscribe({
          next: notif => observer.next(notif),
          error: err => this.forbidden(err)
        });
      this.first = false;
    }
  }

  private automatizedNotificationThroughRequest(observer: Subscriber<Notification[]>) {
    let wait = true;

    let interval = setInterval(() => {
      if (wait) {
        wait = false;
        this.notificationObsAux.next(true);
        this.httpClient.get<Notification[]>(urls.notifications, {withCredentials: true})
          .subscribe({
            next: notif => {
              observer.next(notif);
              wait = true;
            },
            error: err => this.forbidden(err)
          });
        if (!this.storage.getData('client')) {
          clearInterval(interval)
        }
      }
    }, 20000);
  }

  private getUserInTheStorageAndEmitIfExist() {
    let data = this.storage.getData('client');
    if (data) {
      this.user = data;
      this.userReq.next(this.user);
    }
  }

  async login(phoneNumber: string, password: string) {

    this.user = await firstValueFrom(this.httpClient
      .post(
        urls.login,
        {phoneNumber: phoneNumber, password: password},
        {observe: 'response', withCredentials: true})
      .pipe(map(val => {
        console.log(val);
        return val.body as User;
      }))
      .pipe(catchError(reason => {
        console.log('algo')
        throw reason
      }))
    );

    this.userReq.next(this.user);
    this.storage.setData('client', this.user, false);
  }

  logout(){
    this.httpClient.post(urls.logout, {}, {withCredentials: true}).subscribe(() => this.storage.clear());
  }

  getDataset() {
    return firstValueFrom(
      this.httpClient.get<number[]>(urls.quantityQueryByMonths, {withCredentials: true})
        .pipe(
          map(response => this.graphSet.next(response)),
          catchError(err => this.forbidden(err))
        ));
  }

  getEnabledUser(id: number) {
    return this.httpClient.get<boolean>(urls.enabledUser + id, {withCredentials: true})
      .pipe(catchError(err => this.forbidden(err)));
  }

  getAllSeaa() {
    return this.httpClient.get<Seaa[]>(urls.findAllSeaa, {withCredentials: true})
      .pipe(catchError(err => this.forbidden(err)));
  }

  getSeaaById(idSeaa: number) {
    return firstValueFrom(
      this.httpClient.get<Seaa>(urls.findSeaaById + idSeaa, {withCredentials: true})
        .pipe(catchError(err => this.forbidden(err))));
  }

  getSeaaGraph(idSeaa: number) {
    return firstValueFrom(this.httpClient.get(urls.seaaGraph + idSeaa, {withCredentials: true})
      .pipe(map(val => val as number[]), catchError(err => this.forbidden(err))));
  }

  getUserUsedSeaa(idSeaa: number) {
    return firstValueFrom(
      this.httpClient.get<User[]>(urls.relationUserSeaa + idSeaa, {withCredentials: true})
        .pipe(catchError(err => this.forbidden(err))));
  }

  getQueriesSavedByUser(idUser: number, queries: string[]) {
    const loadQ: AnswerAndQuestion[] = [];

    queries.forEach(async query => {
      loadQ.push(await firstValueFrom(this.httpClient.get(urls.loadQuerySaved + idUser + '+' + query, {withCredentials: true})
        .pipe(
          map(val => val as AnswerAndQuestion),
          catchError(err => this.forbidden(err, loadQ)))
      ));
    });

    return loadQ;
  }

  getAllLogs(){
    return firstValueFrom(this.httpClient.get<Log[]>(urls.allLogs, {withCredentials: true})
      .pipe(catchError(err => this.forbidden(err))));
  }

  getFindAllUser() {
    const obs$ = this.httpClient.get(urls.findAllUser, {withCredentials: true})
      .pipe(
        map(val => val as User[]),
        catchError(err => this.forbidden(err))
      );
    return firstValueFrom(obs$);
  }

  getFindAllUserSpecialist() {
    const obs$ = this.httpClient.get(urls.findAllUserSpecialist, {withCredentials: true})
      .pipe(
        map(val => val as User[]),
        catchError(err => this.forbidden(err))
      );
    return firstValueFrom(obs$);
  }

  getAddRolSpecialist(idUser: Number) {
    return this.httpClient.get<User>(urls.addRolSpecialist + idUser, {withCredentials: true})
      .pipe(catchError(err => this.forbidden(err)));
  }

  getAllRoles() {
    return this.httpClient.get<Rol[]>(urls.findAllRoles, {withCredentials: true})
      .pipe(catchError(err => this.forbidden(err)));
  }

  getRequestToBeSpecialist() {
    return this.httpClient.get(urls.requestToBeSpecialist, {withCredentials: true})
      .pipe(catchError(err => this.forbidden(err)));
  }

  getView(idUser: Number) {
    return this.httpClient.get(urls.getView + idUser, {withCredentials: true})
      .pipe(catchError(err => this.forbidden(err)));
  }



  postCreateUser(user: User) {
    return this.httpClient.post(urls.createUser, user, {observe: 'response', withCredentials: true})
      .pipe(
        map(val => val.body as User),
        catchError(err => this.forbidden(err))
      );
  }

  postCreateSeaa(seaa: Seaa) {
    return this.httpClient.post(urls.createSeaa, seaa, {observe: 'response', withCredentials: true})
      .pipe(
        map(val => val.body as Seaa),
        catchError(err => this.forbidden(err))
      );
  }

  postSaveAllUser(users: User[]) {
    return this.httpClient.post(urls.saveAllUser, users, {observe: 'response', withCredentials: true})
      .pipe(
        map(val => val.body as User[]),
        catchError(err => this.forbidden(err))
      );
  }

  postSaveAnswerAndQuestion(object: any, idSeaa: number) {
    return this.httpClient.post(urls.saveQuery + idSeaa, object, {observe: 'response', withCredentials: true})
      .pipe(
        map(val => {
          let query = val.body as Query;
          this.user.queries.push(query);
          this.userReq.next(this.user)
          return query;
        }),
        catchError(err => this.forbidden(err))
      );
  }

  postUploadSeaaRarOrZip(formData: any, nameSeaa: string) {
    return this.httpClient.post(urls.uploadSeaaRarOrZip + nameSeaa, formData, {withCredentials: true})
      .pipe(catchError(err => this.forbidden(err)));
  }

  postInformationError(notification: Notification) {
    return this.httpClient.post(urls.postInformationError, notification, {withCredentials: true})
      .pipe(catchError(err => this.forbidden(err)));
  }

  postShareSeaa(idSharedUsers: Number[], idUser: number, idSeaa: number) {
    return this.httpClient.post<Seaa>(urls.shareSeaa + idSeaa + "+" + idUser, idSharedUsers, {withCredentials: true})
      .pipe(catchError(err => this.forbidden(err)));
  }



  putQuestionOfQuestionnaire(object: AnswerAndQuestion[], idUser: number) {
    return this.httpClient.put(urls.updateQuestionOfQuestionnaire + idUser, object, {
      observe: 'response',
      withCredentials: true
    })
      .pipe(
        map(val => val.body as AnswerAndQuestion[]),
        catchError(err => this.forbidden(err))
      )
  }

  putUpdateUser() {
    return this.httpClient.put(urls.updateUser, this.user, {observe: 'response', withCredentials: true})
      .pipe(
        map(value => {
          this.user = value.body as User;
          this.userReq.next(this.user);
        }),
        catchError(err => this.forbidden(err))
      );
  }

  updateUser(user: User) {
    return this.httpClient.put(urls.update + user.id, user, {observe: 'response', withCredentials: true})
      .pipe(
        map(value => value.body as User),
        catchError(err => this.forbidden(err))
      );
  }

  putAnswersVar(indexArr: number, var$: Var, idSeaa: number) {
    return firstValueFrom(this.httpClient.post(urls.updateAnswerVar + indexArr + '+' + idSeaa, var$, {
      observe: 'response',
      withCredentials: true
    })
      .pipe(
        map(val => this.getQuestion(val)),
        catchError(err => this.forbidden(err))
      ));
  }

  putAnswersProp(indexArr: number, prop: Prop, idSeaa: number) {
    return firstValueFrom(this.httpClient.post(urls.updateAnswerProp + indexArr + '+' + idSeaa, prop, {
      observe: 'response',
      withCredentials: true
    })
      .pipe(
        map(val => this.getQuestion(val)),
        catchError(err => this.forbidden(err))
      ));
  }


  newQuery(idSeaa: number) {
    return this.httpClient.get(urls.newQuery + idSeaa, {withCredentials: true})
      .pipe(
        map(val => val as Question),
        catchError(err => this.forbidden(err))
      );
  }

  queryVar(idSeaa: number, var$: Var) {
    return firstValueFrom(this.httpClient.post(urls.queryVar + idSeaa, var$, {
      observe: 'response',
      withCredentials: true
    })
      .pipe(
        map(val => this.getQuestion(val)),
        catchError(err => this.forbidden(err))
      ));
  }

  queryProp(idSeaa: number, prop: Prop) {
    return firstValueFrom(this.httpClient.post(urls.queryProp + idSeaa, prop, {
      observe: 'response',
      withCredentials: true
    })
      .pipe(
        map(val => this.getQuestion(val)),
        catchError(err => this.forbidden(err))
      ));
  }


  deleteUser(idUser: number) {
    return this.httpClient.delete(urls.deleteUser + idUser, {withCredentials: true})
      .pipe(catchError(err => this.forbidden(err)));
  }


  get notification() {
    return this.notificationObs;
  }

  get notificationAux() {
    return this.notificationObsAux.asObservable();
  }

  public get userObserver() {
    return this.userReq.asObservable();
  }

  public get graphObserver() {
    return this.graphSet.asObservable();
  }

  public get getUser(): User {
    return this.user;
  }

  public containsRol(rol: string): boolean {
    return this.user
      .roles
      .find((value) => value.name === rol) != undefined;
  }


  private getQuestion(val: HttpResponse<Object>): Question {
    return val.body as Question
  }

  private forbidden(err: any, value?: any) {
    if (err instanceof HttpErrorResponse && err.status === 403 || err.status === 401) {
      let data = {
        message: 'No tiene acceso al recurso. Por favor intente iniciar sesión.',
        type: 1,
        oneButton: true
      }
      this.router.navigateByUrl('login')
        .then(() => this.dialog.open(DialogConfirmComponent, {data: data}))
      return of(value !== undefined ? value : 0);
    }
    throw err;
  }

}
