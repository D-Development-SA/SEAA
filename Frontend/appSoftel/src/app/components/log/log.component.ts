import { Component, OnInit } from '@angular/core';
import { Question } from '../../classes/question';
import { AnswerAndQuestion } from '../../classes/AnswerAndQuestion';
import { UserService } from '../../services/user.service';
import { User } from '../../classes/User';
import { LogService } from '../../services/log.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogConfirmComponent } from '../dialog-confirm/dialog-confirm.component';
import { firstValueFrom } from 'rxjs';
import { Seaa } from '../../classes/Seaa';

@Component({
  selector: 'app-log',
  templateUrl: './log.component.html',
  styleUrls: ['./log.component.css']
})
export class LogComponent implements OnInit{
  user: User | undefined;
  answerAndQuestions: AnswerAndQuestion[] = [];
  showQuestion: boolean = false;
  showConclusion: boolean = true;
  areThereQueries: boolean = false;
  seaas: Seaa[] = [];

  constructor(private userServ: UserService, private logServ: LogService,private dialog: MatDialog){}
  
  async ngOnInit(): Promise<void> {
    this.user = this.userServ.getUser;
    this.areThereQueries = this.user.queries.length != 0;
    if (this.areThereQueries) {
      this.answerAndQuestions = this.userServ.getQueriesSavedByUser(this.user.id!, this.user.queries.map(q => q.uuid));
    }
    this.seaas = await firstValueFrom(this.userServ.getAllSeaa());
    this.answerAndQuestions = this.answerAndQuestions.sort((a,b) => b.date_queries?.getTime()! - a.date_queries?.getTime()!);
  }

  continueQuery(answerAndQuestions: AnswerAndQuestion){
    this.logServ.emit(answerAndQuestions);
  }
}
