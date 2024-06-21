import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Var } from 'src/app/classes/Var';
import { Question } from 'src/app/classes/question';

@Component({
  selector: 'app-t-input',
  templateUrl: './t-input.component.html',
  styleUrls: ['./t-input.component.css']
})
export class TInputComponent {
  @Input() disabledAll: boolean = false;
  @Input() typeI: string | undefined;
  @Input() placeHolderText: string | undefined;
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
  @Input() value!: string;
  @Input() btnDisabled: boolean = false;
  @Input() disabled: boolean = false;
  @Output() answer: EventEmitter<Var> = new EventEmitter<Var>();
  @Output() cancel = new EventEmitter();
  
  var$: Var = {
    var: '',
    domi: '',
    parti: '',
    cert: '',
    value: '',
    index: '',
    items: [],
  }


  sendAnswer(): void{
    this.var$.value = String(this.value);
    this.var$.var = this.question.var,
    this.var$.domi = this.question.domi,
    this.var$.parti = this.question.parti,
  
    this.disabled = true;
    this.btnDisabled = true
    this.answer.emit(this.var$);
  }

  eventCancel(): void{
    this.disabled = false;
    this.cancel.emit();
  }

  isExistValue(){
    this.btnDisabled = String(this.value).length === 0 || Number(this.value) < 14 || Number(this.value) > 100 || /^[.]+$/.test(this.value);
  }
}
