import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-actions-buttons',
  templateUrl: './actions-buttons.component.html',
  styleUrls: ['./actions-buttons.component.css']
})
export class ActionsButtonsComponent {
  @Input() anotherButton: boolean | undefined;
  @Input() col: boolean = false;
  @Input() start: boolean = false;
  @Input() textPrimary: string = 'Siguiente';
  @Input() textTerciary: string = 'Ninguna';
  @Output() clickAcept = new EventEmitter();
  @Output() clickCancel = new EventEmitter();
  @Output() clickNeither = new EventEmitter();
  @Input() disabled: boolean = false;
  @Input() spinner: boolean = false;

  emitAndDeactivate(emitter : EventEmitter<any>){
    this.disabled = true;
    emitter.emit();
  }
  emitAndActivate(){
    this.disabled = false;
    this.clickCancel.emit();
  }
}
