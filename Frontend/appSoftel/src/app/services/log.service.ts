import { Injectable } from '@angular/core';
import { BehaviorSubject, firstValueFrom } from 'rxjs';
import { AnswerAndQuestion } from '../classes/AnswerAndQuestion';
import { UserService } from './user.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogConfirmComponent } from '../components/dialog-confirm/dialog-confirm.component';
import { Question } from '../classes/question';

@Injectable({
  providedIn: 'root'
})
export class LogService {
  private questionObs: BehaviorSubject<AnswerAndQuestion>;
  private answerAndQuestion: AnswerAndQuestion = {
    answer: {
      problem: '',
      option: '',
      query: {
        vars: []
      }
    },
    uuid_queries: '',
    question: []
  }
  private question: Question | undefined;
  private readonly changePage: BehaviorSubject<boolean>;

  constructor(private userServ: UserService, private dialog: MatDialog) {
    this.questionObs = new BehaviorSubject<AnswerAndQuestion>(this.answerAndQuestion);
    this.changePage = new BehaviorSubject<boolean>(false);
  }

  async prepareEnvironment(){
    let dialogRef = this.dialog.open(DialogConfirmComponent, {data: {
      message: 'Para continuar la consulta debe realizar acciones',
      radio: true,
      seaas: await firstValueFrom(this.userServ.getAllSeaa())
    }, width: '38rem'});

    dialogRef.afterClosed().subscribe(async (val) => {
      if (val != undefined) {
        await this.startQuery(val.id!);
        let size = this.answerAndQuestion.question.length - 1;

        if (this.question != undefined && this.answerAndQuestion.question[size].text !== this.question?.text) {
          this.answerAndQuestion.question.push(this.question!);
        }

        this.changePage.next(true);
        this.questionObs.next(this.answerAndQuestion);
      }
    })
  }

  async emit(answerAndQuestion: AnswerAndQuestion){
    this.answerAndQuestion = answerAndQuestion;
    await this.prepareEnvironment();
  }

  get getAnswerAndQuestion(){
    return this.questionObs.asObservable();
  }

  async startQuery(id: number){
    try {
      await firstValueFrom(this.userServ.newQuery(id));

      for (const vars of this.answerAndQuestion.answer.query.vars){
        if ('prop' in vars) {
          this.question = await this.userServ.queryProp(id, vars);
        }else{
          this.question = await this.userServ.queryVar(id, vars);
        }

        await new Promise(resolve => setTimeout(resolve, 600));
      }
    } catch (error) {
      await this.startQuery(id);
    }
  }

  get changePageObs(){
    return this.changePage;
  }
}
