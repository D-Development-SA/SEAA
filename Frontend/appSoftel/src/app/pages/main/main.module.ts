import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MainRoutingModule } from './main-routing.module';
import { MainComponent } from './main.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatDividerModule } from '@angular/material/divider';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { LogComponent } from '../../components/log/log.component';
import { HomeComponent } from '../../components/home/home.component';
import { AdvancedComponent } from '../../components/advanced/advanced.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { ActionsButtonsComponent } from '../../components/questionnaire/actions-buttons/actions-buttons.component';
import { ConclusionsComponent } from '../../components/questionnaire/conclusions/conclusions.component';
import { IntroComponent } from '../../components/questionnaire/intro/intro.component';
import { QuestionnaireComponent } from '../../components/questionnaire/questionnaire.component';
import { TInputComponent } from '../../components/questionnaire/t-input/t-input.component';
import { TMultipleSelectionComponent } from '../../components/questionnaire/t-multiple-selection/t-multiple-selection.component';
import { TPropositionComponent } from '../../components/questionnaire/t-proposition/t-proposition.component';
import { TSimpleSelectionComponent } from '../../components/questionnaire/t-simple-selection/t-simple-selection.component';
import { MatCardModule } from '@angular/material/card';
import { NgChartsModule } from 'ng2-charts';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {MatRadioModule} from '@angular/material/radio';
import { OptionsComponent } from '../../components/options/options.component';
import { BehaviorComponent } from '../../components/behavior/behavior.component';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { DialogUserDataComponent } from '../../components/dialog-user-data/dialog-user-data.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatStepperModule } from '@angular/material/stepper';
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { NotificationComponent } from '../../components/notification/notification.component';
import { HelpUsComponent } from '../../components/help-us/help-us.component';
import { MatRippleModule } from '@angular/material/core';
import { MatBadgeModule } from '@angular/material/badge';
import { MatListModule } from '@angular/material/list'
import { DialogConfirmComponent } from '../../components/dialog-confirm/dialog-confirm.component';
import {MatSelectModule} from '@angular/material/select';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {StepGuideComponent} from "../../components/step-guide/step-guide.component";
import {NoteComponent} from "../../components/note/note.component";
import {TableToLogsComponent} from "../../components/table-to-logs/table-to-logs.component";
import {MatTableModule} from "@angular/material/table";
import {MatSortModule} from "@angular/material/sort";

@NgModule({
  declarations: [
    MainComponent,
    AdvancedComponent,
    ActionsButtonsComponent,
    HomeComponent,
    QuestionnaireComponent,
    IntroComponent,
    TSimpleSelectionComponent,
    TMultipleSelectionComponent,
    TPropositionComponent,
    TInputComponent,
    ActionsButtonsComponent,
    ConclusionsComponent,
    LogComponent,
    BehaviorComponent,
    OptionsComponent,
    DialogUserDataComponent,
    NotificationComponent,
    HelpUsComponent,
    DialogConfirmComponent,
    StepGuideComponent,
    NoteComponent,
    TableToLogsComponent
  ],
  imports: [
    CommonModule,
    BrowserModule,
    FormsModule,
    MainRoutingModule,
    MatExpansionModule,
    MatDividerModule,
    MatSidenavModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatAutocompleteModule,
    ReactiveFormsModule,
    MatCardModule,
    NgChartsModule,
    MatCheckboxModule,
    MatChipsModule,
    MatRadioModule,
    MatTabsModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDialogModule,
    MatStepperModule,
    MatRippleModule,
    MatBadgeModule,
    MatListModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTableModule,
    MatSortModule
  ],
  exports: [
    QuestionnaireComponent,
    ActionsButtonsComponent
  ],
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: {displayDefaultIndicatorType: false},
    }
  ]
})
export class MainModule { }
