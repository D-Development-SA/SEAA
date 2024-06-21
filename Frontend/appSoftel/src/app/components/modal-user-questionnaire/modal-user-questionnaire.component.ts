import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AnswerAndQuestion } from '../../classes/AnswerAndQuestion';
import { DialogRef } from '@angular/cdk/dialog';

@Component({
  selector: 'app-modal-user-questionnaire',
  templateUrl: './modal-user-questionnaire.component.html',
  styleUrls: ['./modal-user-questionnaire.component.css']
})
export class ModalUserQuestionnaireComponent {
  constructor(
    public dialogRef: DialogRef<ModalUserQuestionnaireComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Data
  ){}
}

export interface Data{
  userName: string;
  showQuestion: boolean;
  showConclusion: boolean;
  answerAndQuestion: AnswerAndQuestion[];
  date: Date[]
}
