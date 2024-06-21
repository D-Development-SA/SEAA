import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './pages/login-register/login/login.component';
import { LoginRegisterComponent } from './pages/login-register/login-register.component';
import { RegisterComponent } from './pages/login-register/register/register.component';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { MainModule } from './pages/main/main.module';
import { HttpClientModule } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from "@angular/material/dialog";
import { ModalUserQuestionnaireComponent } from './components/modal-user-questionnaire/modal-user-questionnaire.component';
import { ModalUserQueryErrorComponent } from './components/modal-user-query-error/modal-user-query-error.component';
import { AlertComponent } from './components/alert/alert.component';
import { MatRippleModule } from '@angular/material/core';
import { QuickStartGuideComponent } from './pages/quick-start-guide/quick-start-guide.component';
import { ContentComponent } from './pages/quick-start-guide/content/content.component';
import { InvalidRouteComponent } from './pages/invalid-route/invalid-route.component';
import { AboutUsComponent } from './components/about-us/about-us.component';
import {NgOptimizedImage} from "@angular/common";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    LoginRegisterComponent,
    RegisterComponent,
    ModalUserQuestionnaireComponent,
    ModalUserQueryErrorComponent,
    AlertComponent,
    QuickStartGuideComponent,
    ContentComponent,
    InvalidRouteComponent,
    AboutUsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MainModule,
    HttpClientModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatDialogModule,
    MatRippleModule,
    NgOptimizedImage
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
