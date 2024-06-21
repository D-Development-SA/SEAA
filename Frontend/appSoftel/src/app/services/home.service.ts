import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  private doneAnimation: BehaviorSubject<void>;
  private registered: BehaviorSubject<boolean>;
  private changePageToLog: BehaviorSubject<boolean>;

  constructor() {
    this.doneAnimation = new BehaviorSubject<void>(undefined);
    this.registered = new BehaviorSubject<boolean>(false);
    this.changePageToLog = new BehaviorSubject<boolean>(false);
   }

  get doneHomeAnimation(){
    return this.doneAnimation.asObservable();
  }

  get getRegistered(){
    return this.registered;
  }

  get getChangePageToLog(){
    return this.changePageToLog;
  }

  doneAnimationEmit(){
    this.doneAnimation.next();
  }
}
