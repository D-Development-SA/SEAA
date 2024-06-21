import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Prop } from 'src/app/classes/Prop';
import { Question } from 'src/app/classes/question';

@Component({
  selector: 'app-t-proposition',
  templateUrl: './t-proposition.component.html',
  styleUrls: ['./t-proposition.component.css']
})
export class TPropositionComponent {
  @Input() disabledAll: boolean = false;
  @Input() value: string = '';
  @Input() disabled: boolean = false;
  @Input() question: Question = {
    var: '',
    domi: '',
    parti: '',
    type: '',
    certi: '',
    neither: '',
    doubleClick: '',
    text: '',
    prop: '',
    list: [],
    concluF: [],
    concluP: [],
    returnVexMain: '',
    return0: '',
    note: ''
  };
  @Output() answer: EventEmitter<Prop> = new EventEmitter<Prop>();
  @Output() cancel: EventEmitter<Prop> = new EventEmitter<Prop>();
  prop: Prop = {
    prop: '',
    domi: '',
    parti: '',
    cert: '',
    text: '',
  }
  
  sendAnswer(value: string){
    this.prop.prop = this.question.prop;
    this.prop.domi = this.question.domi;
    this.prop.parti = this.question.parti;
    this.prop.cert = value;
    this.disabled = true;
    this.answer.emit(this.prop);
  }

  sendCancel(){
    this.disabled = false;
    this.cancel.emit();
  }
}
