import {Component, ElementRef, QueryList, ViewChildren} from '@angular/core';

@Component({
  selector: 'app-step-guide',
  templateUrl: './step-guide.component.html',
  styleUrls: ['./step-guide.component.css']
})
export class StepGuideComponent {
  @ViewChildren('element') myElements: QueryList<ElementRef> | undefined;
}
