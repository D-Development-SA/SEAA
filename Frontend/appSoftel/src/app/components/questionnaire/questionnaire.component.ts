import { AfterViewChecked, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { Question } from '../../classes/question';
import { UserService } from '../../services/user.service';
import { firstValueFrom } from 'rxjs';
import { Answer } from '../../classes/Answer';
import { Var } from '../../classes/Var';
import { Seaa } from '../../classes/Seaa';
import { Prop } from '../../classes/Prop';
import { HttpErrorResponse } from '@angular/common/http';
import { ModalUserQueryErrorComponent } from '../modal-user-query-error/modal-user-query-error.component';
import { Errors } from '../../classes/Error';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AlertComponent } from '../alert/alert.component';
import { Router } from '@angular/router';
import { LogService } from '../../services/log.service';

@Component({
  selector: 'app-questionnaire',
  templateUrl: './questionnaire.component.html',
  styleUrls: ['./questionnaire.component.css']
})
export class QuestionnaireComponent implements AfterViewChecked, OnInit{
  @ViewChild('scrollMarker') scrollMarker: ElementRef | undefined;

  @Input() showIntro:boolean = false;
  buttonsTool: ButtonTool[] = [
    {nameIcon: 'insert_drive_file', class: 'btn-primary', toolTip: 'Crear nueva consulta', click: this.newQuery.bind(this)},
    {nameIcon: 'save', class: 'btn-success', toolTip: 'Guardar consulta', click: () => this.save()},
    {nameIcon: 'settings', class: 'btn-secondary', toolTip: 'Ir a opciones', click: () => this.router.navigate(['main/options'])},
    {nameIcon: 'cancel', class: 'btn-danger', toolTip: 'Resetear consulta', click: () => this.cancel(0)},
  ];

  seaa: Seaa = {
    name: '',
    problem: '',
    option: '',
    description: '',
    year: 0,
    version: ''
  }

  @Input() answers: Answer = {
    problem: '',
    option: '',
    query: {
      vars: []
    }
  };
  @Input() questions: Question[] = [];
  @Input() showQuestion: boolean = true;
  @Input() showConclusions: boolean = false;
  @Input() position: Position = 'end';
  @Input() showComplement: boolean = true;
  @Input() readOnly: boolean = false;
  @Input() continueQuestionnarie: boolean = true;
  @Input() showNoteQuestion: boolean = false;
  @Input() dateConclusion: Date = new Date();
  @Output() continueQuery = new EventEmitter();


  addNote: boolean = false;
  cancelTouch: boolean = false;
  idUser: number = -1;
  currentLenghtArray: number = -1;
  oldLenghtArray: number = -1;
  startSpinnerQuery: boolean = false;
  activateBtn: boolean[] = [];

  constructor(private userServ: UserService,
              private errorDialog: MatDialog,
              private snackBar: MatSnackBar,
              private router: Router,
              private logServ: LogService){}

  ngOnInit(): void {
    this.logServ.getAnswerAndQuestion.subscribe(result => {
      console.log(result);

      if (result.answer.problem !== '') {
        this.answers = result.answer;
        this.questions = result.question;
        this.showIntro = true;
        this.activateBtn.splice(0);
        this.questions.forEach(() => this.activateBtn.push(true));
        this.activateBtn[this.questions.length-1] = false;
      }
    });
    this.userServ.userObserver.subscribe(val => this.idUser = val.id!);
    this.questions.forEach(() => this.activateBtn.push(true));
  }

  ngAfterViewChecked(): void {
    if(this.position === 'end' && this.currentLenghtArray != this.oldLenghtArray){
      this.scrollToBottom();
      this.oldLenghtArray = this.currentLenghtArray;
    }
  }

  private scrollToBottom(): void{
    this.scrollMarker?.nativeElement.scrollIntoView({ behavior: 'smooth' });
  }

  showQ(view: boolean): void{
    this.showQuestion = view;
  }

  async startQuestionnaire(h: any){
    this.questions.splice(0);
    this.answers.query.vars.splice(0);
    this.seaa = h.seaa;
    try {
      let q = await firstValueFrom(this.userServ.newQuery(this.seaa.id!));
      console.log(q);

      this.activateBtn.push(false);
      this.questions.push(q);
      this.answers.option = this.seaa.option;
      this.answers.problem = this.seaa.problem;
      this.showIntro = h.show;
      this.startSpinnerQuery = false;
    } catch (err) {
      setTimeout(() => this.startQuestionnaire(h), 1500);
    }
  }

  async sendAnswerVar(var$: Var, index: number){
    try {
      if (this.cancelTouch) {
        this.cancelTouch = false;
        this.checkPropOrConclusion(await this.userServ.putAnswersVar(index, var$, this.seaa.id!));
      }else{
        this.checkPropOrConclusion(await this.userServ.queryVar(this.seaa.id!, var$))
      }
    } catch (error) {
      this.catchErrorShowErrorDialog(error, index);
    }
    this.answers.query.vars.push(var$);
    this.activateBtn[index] = true;
    console.log('Question btn sigt var');
    console.log(this.questions);
    console.log('Answer btn sigt var');
    console.log(this.answers.query.vars);
  }

  private catchErrorShowErrorDialog(error: unknown, index: number) {
    if (error instanceof HttpErrorResponse) {
      let err: Errors = error.error as Errors;

      const dialogRef = this.errorDialog.open(ModalUserQueryErrorComponent, {
        data: err
      });

      dialogRef.afterClosed().subscribe(() => this.activateBtn[index] = false);
    }
  }

  async sendAnswerProp(prop: Prop, index: number){
    try {
      if (this.cancelTouch) {
        this.cancelTouch = false;
        this.checkPropOrConclusion( await this.userServ.putAnswersProp(index, prop, this.seaa.id!));
      }else{
        this.checkPropOrConclusion( await this.userServ.queryProp(this.seaa.id!, prop));
      }
    } catch (error) {
      this.catchErrorShowErrorDialog(error, index);
    }
    this.answers.query.vars.push(prop);
    console.log('Question btn sigt prop');
    console.log(this.questions);
    console.log('Answer btn sigt prop');
    console.log(this.answers.query.vars);
  }

  checkPropOrConclusion(quest: Question){
    if(quest.prop){
      quest.type = 'P';
    }

    if (quest.type === 'CONCLU') {
      this.showConclusions = true;
      this.showQ(false);
    }
    this.questions.push(quest);
    this.activateBtn.push(false);

    this.currentLenghtArray = this.questions.length;
  }

  cancel(indexQuestion: number): void{
    this.cancelTouch = true;
    this.questions.splice(indexQuestion+1);
    this.answers.query.vars.splice(indexQuestion);
    console.log('Question btn cancel');
    console.log(this.questions);
    console.log('Answer btn cancel');
    console.log(this.answers.query.vars);
  }

  newQuery(){
    this.showIntro = false;
    this.showQ(true);
    this.showConclusions = false;
    this.questions.splice(0);
    this.activateBtn.splice(0);
    this.answers.query.vars.splice(0);
  }

  save(){
    this.userServ.postSaveAnswerAndQuestion({
      answer: this.answers,
      question: this.questions,
      date_queries: new Date()
    }, this.seaa.id!)
    .subscribe(val => console.log(val));

    this.snackBar.openFromComponent(AlertComponent,{
      duration: 1000,
      data: 'Consulta guardada satisfactoriamente!!!'
    })
  }

  getValueAnswer(index: number){
    if(this.answers.query.vars)
      return '14';
    return (this.answers.query.vars[index] as Var).value;
  }
}

export interface ButtonTool{
  class: string;
  nameIcon: string;
  toolTip: string;
  click?(): void;
}
 export type Position = 'end' | 'start';
