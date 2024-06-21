import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { ChartConfiguration, ChartData, ChartEvent, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { UserService } from '../../services/user.service';
import { moveAnimation } from '../../animations/animations';
import { HomeService } from '../../services/home.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  animations: [
    moveAnimation
  ]
})
export class HomeComponent implements OnInit{
  quantityQuery: number = 0;
  results: Date[] = [];

  @ViewChild(BaseChartDirective) chart: BaseChartDirective | undefined;

  constructor(private userServ: UserService, public homeServ: HomeService){}

  async ngOnInit(): Promise<void> {
    let id: number = -1;
    this.userServ.userObserver.subscribe(value => {
      id = value.id!;
      this.results.splice(0);
      if (value.queries) {
        value.queries.reverse().forEach(query => this.results.push(query.date));
        this.quantityQuery = value.queries.length;
      }
    })
    this.userServ.graphObserver.subscribe(value => {
      if (value.length !== 0) {
        this.barChartData.datasets[0].data = value;
        this.chart?.update();
      }
    });
    await this.userServ.getDataset();
  }

  public barChartOptions: ChartConfiguration['options'] = {
    // We use these empty structures as placeholders for dynamic theming.
    scales: {
      x: {
        ticks:{
          color: 'black'
        }
      },
      y: {
        min: 0,
        ticks:{
          stepSize: 1,
          color: 'black'
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

  public barChartData: ChartData<'bar'> = {
    labels: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
    'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
    datasets: [
      { data: [], label: 'Cantidad de consultas', backgroundColor: '#006633 '},
    ],
  };

  emitChange(){
    this.homeServ.getChangePageToLog.next(true);
  }

}
