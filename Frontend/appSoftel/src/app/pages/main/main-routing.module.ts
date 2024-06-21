import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import { HomeComponent } from '../../components/home/home.component';
import { QuestionnaireComponent } from '../../components/questionnaire/questionnaire.component';
import { LogComponent } from '../../components/log/log.component';
import { AdvancedComponent } from '../../components/advanced/advanced.component';
import { MainComponent } from './main.component';
import { BehaviorComponent } from '../../components/behavior/behavior.component';
import { OptionsComponent } from '../../components/options/options.component';
import { HelpUsComponent } from '../../components/help-us/help-us.component';
import {InvalidRouteComponent} from "../invalid-route/invalid-route.component";
import {AboutUsComponent} from "../../components/about-us/about-us.component";
import {rolesGuard} from "../../guardians/roles.guard";
import {authGuard} from "../../guardians/auth.guard";

const routes: Routes = [
  {path:'main', component: MainComponent, canActivate: [authGuard], canActivateChild: [rolesGuard], children:[
    {path: '', component: HomeComponent, data: {role: 'ROLE_USER'}},
    {path: 'questionnaire', component: QuestionnaireComponent, data: {role: 'ROLE_USER'}},
    {path: 'log', component: LogComponent, data: {role: 'ROLE_USER'}},
    {path: 'advanced', component: AdvancedComponent, data: {role: 'ROLE_ADMIN'}},
    {path: 'behavior', component: BehaviorComponent, data: {role: 'ROLE_EXPERT'}},
    {path: 'options', component: OptionsComponent, data: {role: 'ROLE_USER'}},
    {path: 'help', component: HelpUsComponent, data: {role: 'ROLE_USER'}},
    {path: 'about_us', component: AboutUsComponent, data: {role: 'ROLE_USER'}},
  ]},
  {path: '**', component: InvalidRouteComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MainRoutingModule { }
