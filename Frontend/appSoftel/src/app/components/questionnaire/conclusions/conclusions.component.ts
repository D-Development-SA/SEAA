import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Item } from 'src/app/classes/Item';
import { Question } from 'src/app/classes/question';

@Component({
  selector: 'app-conclusions',
  templateUrl: './conclusions.component.html',
  styleUrls: ['./conclusions.component.css']
})
export class ConclusionsComponent {
  @Input() question: Question | undefined;
  @Input() date: Date = new Date();
  @Input() showComplement: boolean = false;
  @Input() continueQuestionnarie: boolean = true;
  @Output() showQuestions: EventEmitter<boolean> = new EventEmitter<boolean>;
  @Output() clickNew = new EventEmitter();
  @Output() clickSave = new EventEmitter();
  @Output() continueQuery = new EventEmitter();

  show: boolean = false;

  showQ(): void{
    this.show = !this.show;
    this.showQuestions.emit(this.show);
  }
}
