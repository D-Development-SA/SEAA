import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Var } from 'src/app/classes/Var';
import { Question } from 'src/app/classes/question';

@Component({
  selector: 'app-t-simple-selection',
  templateUrl: './t-simple-selection.component.html',
  styleUrls: ['./t-simple-selection.component.css']
})
export class TSimpleSelectionComponent implements OnInit {
  @Input() disabledAll: boolean = false;
  @Input() question: Question | undefined;
  @Input() disabled: boolean = false;
  @Input() btnDisabled: boolean = true;
  @Output() answer:EventEmitter<Var> = new EventEmitter<Var>();
  @Output() cancel = new EventEmitter();
  chip: Chip[] = [];
  index: number = -1;

  ngOnInit(): void {
    this.question?.list.forEach(value =>{
      this.chip.push({name: value.text, selected: value.selected == '1'})
    });
  }

  slnchange(ch: Chip): void{
    this.chip?.forEach((value, index) => {
      if (value === ch) {
        value.selected = true;
        this.index = index;
      }else value.selected = false;
    });
    if (!this.disabled) {
      this.btnDisabled = false;
    }
  }

  sendAnswer(): void{
    let var$: Var = {
      var: this.question!.var,
      domi: this.question!.domi,
      parti: this.question!.parti,
      cert: '1.00',
      value: '',
      index: this.question!.list[this.index].index,
      items: [],
    };

    this.question!.list[this.index].selected = '1';
    this.disabled = true;
    this.answer.emit(var$);
  }

  sendCancel(): void{
    this.disabled = false;
    this.cancel.emit();
  }
}

export interface Chip{
  selected: boolean;
  name: string;
}