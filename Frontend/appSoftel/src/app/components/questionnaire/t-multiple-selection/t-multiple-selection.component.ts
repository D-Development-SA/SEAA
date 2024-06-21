import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { Item } from 'src/app/classes/Item';
import { User } from 'src/app/classes/User';
import { Var } from 'src/app/classes/Var';
import { Question } from 'src/app/classes/question';

@Component({
  selector: 'app-t-multiple-selection',
  templateUrl: './t-multiple-selection.component.html',
  styleUrls: ['./t-multiple-selection.component.css']
})
export class TMultipleSelectionComponent implements OnInit {
  @Input() disabledAll: boolean = false;
  @Input() disabled: boolean = false;
  @Input() btnDisabled: boolean = true;
  @Input() isUsers: boolean = false;
  @Input() question: Question | undefined;
  @Output() answer: EventEmitter<Var> = new EventEmitter<Var>();
  @Output() cancel = new EventEmitter();
  @Output() usersSelected: EventEmitter<User[]> = new EventEmitter<User[]>();
  usersSelect: User[] = [];
  var$: Var = {
    var: '',
    domi: '',
    parti: '',
    cert: '',
    value: '',
    index: '',
    items: [],
  };
  users: User[] = [];

  subtasks: Task[] = [];

  task: Task = {
    name: 'Todas las enfermedades',
    completed: false,
  };

  allComplete: boolean = false;
  neither: boolean = false;
  todosIndex: number = -1;

  constructor(private userServ: UserService){}

  async ngOnInit(): Promise<void> {
    if (this.isUsers) {
      this.users = await this.userServ.getFindAllUserSpecialist();
      this.task.name = 'Todos los usuarios';
      this.users.forEach(user => this.subtasks.push({name: user.name + ' ' + user.lastName, completed: false}))
    }else{
      this.question?.list.forEach((value, index) => {
        if (value.text !== '+ TODOS') {
          this.subtasks.push({name: value.text, completed: value.selected == '1'})
        }else{ this.todosIndex = index}
      })
    }
  }

  updateAllComplete() {
    this.allComplete = this.subtasks != null && this.subtasks.every(t => t.completed);
  }

  someComplete(): boolean {
    if (this.subtasks == null) {
      return false;
    }
    if (this.isUsers) {
      this.usersSelect = [];
      this.subtasks.forEach((task, index) => {
        if (task.completed) {
          this.usersSelect.push(this.users[index]);
        }
      })
      this.usersSelected.emit(this.usersSelect);
    }else{
      this.btnDisabled = this.subtasks.filter(t => t.completed).length === 0;
    }
    return this.subtasks.filter(t => t.completed).length > 0 && !this.allComplete;
  }

  setAll(completed: boolean) {
    this.allComplete = completed;
    if (this.subtasks == null) {
      return;
    }
    this.subtasks.forEach(t => (t.completed = completed));
  }

  sendAnswer():void{
    this.var$.var = this.question!.var;
    this.var$.domi = this.question!.domi;
    this.var$.parti = this.question!.parti;

    if (this.allComplete && this.todosIndex !== -1) {
      this.var$.items.push(this.question!.list[this.todosIndex]);
    }else{
      this.subtasks.forEach((val, index) => {
        if (val.completed || this.neither) {
          let item = this.question!.list[index];
          this.var$.items.push({
            index: item.index,
            cert: item.cert === '-1' ? '-1.00' : '1.00',
            text: '',
            indexVar: '',
            indexKB: '',
            selected: '',
            prop: '',
            var: '',
            domi: '',
            parti: ''
          });
        }
      });
    }

    this.question!.list.forEach(value => value.selected = '0');

    this.var$.items.forEach(item => {
      this.question!.list[parseInt(item.index)].selected = item.cert === '-1.00' ? '0' : '1';
    });

    this.disabled = true;
    this.btnDisabled = true;
    this.neither = false;
    this.answer.emit(this.var$);
  }

  sendAnswerAny(): void{
    this.neither = true;
    this.question!.list.forEach(val => val.cert='-1')
    this.sendAnswer();
  }

  sendCancel(): void{
    this.disabled = false;
    this.cancel.emit();
  }
}

export interface Task {
  name: string;
  completed: boolean;
}
