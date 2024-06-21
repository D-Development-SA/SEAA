import {Component, OnInit} from '@angular/core';
import {Log, Method} from "../../classes/Log";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-table-to-logs',
  templateUrl: './table-to-logs.component.html',
  styleUrls: ['./table-to-logs.component.css']
})
export class TableToLogsComponent implements OnInit {
  displayedColumns: string[] = ['number', 'name', 'lastname', 'method', 'ci', 'date'];
  logs: Log[] | null = [];
  isLoading: boolean = false;

  constructor(private userServ: UserService) {
  }

  async ngOnInit(): Promise<void> {
    this.isLoading = true;
    console.log(this.isLoading)
    this.logs = await this.userServ.getAllLogs().then(value => {
        this.isLoading = false;
        console.log(this.isLoading)
        return value;
      }
    );
    console.log("final"+this.isLoading)
  }

  protected readonly Method = Method;
}
