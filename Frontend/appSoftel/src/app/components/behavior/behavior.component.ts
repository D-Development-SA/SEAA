import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Seaa } from '../../classes/Seaa';
import { User } from '../../classes/User';
import { ChartConfiguration, ChartType, ChartData } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { Question } from '../../classes/question';
import { UserService } from '../../services/user.service';
import { AnswerAndQuestion } from '../../classes/AnswerAndQuestion';
import { MatDialog } from '@angular/material/dialog';
import { ModalUserQuestionnaireComponent } from '../modal-user-questionnaire/modal-user-questionnaire.component';

@Component({
  selector: 'app-behavior',
  templateUrl: './behavior.component.html',
  styleUrls: ['./behavior.component.css']
})
export class BehaviorComponent implements OnInit{
  user: User | undefined;
  seaas: Seaa[] = [];
  dataQuery: number[][] = [];
  total: number[] = [];
  userWithLink: User[][] = [];
  answerAndQuestion: AnswerAndQuestion [][][] = [];
  showQuestion: boolean = false;
  showConclusion: boolean = true;
  ready: boolean = false;

  @ViewChild(BaseChartDirective) chart: BaseChartDirective | undefined;

  public barChartOptions: ChartConfiguration['options'] = {
    // We use these empty structures as placeholders for dynamic theming.
    scales: {
      x: {
        ticks:{
          color: 'black'
        }
      },
      y: {
        min: 0 ,
        ticks:{
          color: 'black',
          stepSize: 1.5
        }
      },
    },
    plugins: {
      legend: {
        display: true,
      }
    },
  };
  public barChartType: ChartType = 'bar';

  public barChartData: ChartData<'bar'>[] = [];

  constructor(private userServ: UserService, private dialog: MatDialog){}

  async ngOnInit(): Promise<void> {
    this.userServ.userObserver.subscribe(val => {
      this.user = val;
      console.log(this.user);
    });
    this.seaas = this.user?.specialist?.seaaList!;
    console.log(this.seaas);

    if (this.user?.specialist?.seaaShared) {
      for (const idSeaa of this.user?.specialist?.seaaShared) {

        if (!this.seaas.find(seaa => seaa.id == idSeaa)) {
          this.seaas.push(await this.userServ.getSeaaById(idSeaa));
        }
      }
    }

    try {
      for (let i = 0; i < this.seaas.length; i++) {
        this.dataQuery[i] = await this.userServ.getSeaaGraph(this.seaas[i].id!);

        this.barChartData.push({
          labels: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
          'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
          datasets: [
            { data: this.dataQuery[i], label: 'Cantidad de consultas', backgroundColor: '#006633'},
          ],
        });

        console.log(this.dataQuery);

        this.total.push(this.dataQuery[i].reduce((a,b) => a+b));

        console.log('total' + this.total );

        this.userWithLink.push(await this.userServ.getUserUsedSeaa(this.seaas[i].id!));

        console.log(this.userWithLink);

        var showQAux: boolean[] = [];

        this.userWithLink[i].forEach(() => showQAux.push(false));

        console.log('questions');

        this.userWithLink[i].forEach(userQ =>{
          let answerAndQuestionAux = [];
          answerAndQuestionAux.push(this.userServ.getQueriesSavedByUser(userQ.id!, userQ.queries.map(q => q.uuid)));
          this.answerAndQuestion.push(answerAndQuestionAux);
        });

        console.log(this.answerAndQuestion);
      }

    } finally {
      this.ready = true;
    }

  }

  openDialog(i: number, j: number, userName: string){
    let dialogRef = this.dialog.open(ModalUserQuestionnaireComponent,
      {
        data: {
          userName: userName,
          answerAndQuestion: this.answerAndQuestion[i][j],
          showQuestion: this.showQuestion,
          showConclusion: this.showConclusion,
          date: this.userWithLink[i][j].queries.map(quer => quer.date)
        },
        width: '60rem'
      },
    );

    dialogRef.afterClosed()
        .subscribe(result =>
        {
          if (result) {
            let answerAndQuestionAux = this.answerAndQuestion[i][j];
            let uuid = this.userWithLink[i][j].queries.map(quer => quer.uuid);

            answerAndQuestionAux.forEach((val, i) => val.uuid_queries = uuid[i]);

            this.userServ.putQuestionOfQuestionnaire(answerAndQuestionAux,this.userWithLink[i][j].id!)
                          .subscribe(val => {
                            this.answerAndQuestion[i][j] = val;
                            console.log(this.answerAndQuestion[i][j]);
                          })
                      }
        });

  }
}
