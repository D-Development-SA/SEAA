import { NgModule } from '@angular/core';
import {PreloadAllModules, RouterModule, Routes} from '@angular/router';
import { LoginRegisterComponent } from './pages/login-register/login-register.component';
import { QuickStartGuideComponent } from './pages/quick-start-guide/quick-start-guide.component';
import {authGuard} from "./guardians/auth.guard";

const routes: Routes = [
  {path: '', redirectTo: 'main', pathMatch: 'full'},
  {path: 'login', component: LoginRegisterComponent},
  {path: 'quick-start-guide', component: QuickStartGuideComponent, canActivate: [authGuard]},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
