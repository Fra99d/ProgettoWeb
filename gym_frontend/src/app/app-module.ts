import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';

import { AbbonamentiListComponent } from './pagine/abbonamenti-list/abbonamenti-list.component';
import { CorsiListComponent } from './pagine/corsi-list/corsi-list.component';
import { AlimentazioneListComponent } from './pagine/alimentazione-list/alimentazione-list.component';
import { AdminDashboardComponent } from './pagine/admin-dashboard/admin-dashboard.component';
import { ClienteDashboardComponent } from './pagine/cliente-dashboard/cliente-dashboard.component';
import { NavbarComponent } from './componenti/navbar/navbar.component';
import { AuthComponent } from './pagine/auth/auth.component';
import { CredentialsInterceptor } from './services/credentials.interceptor';
import { AccountComponent } from './pagine/account/account.component';
import { AuthErrorInterceptor } from './services/auth-error.interceptor';



@NgModule({
  declarations: [
    App,
    NavbarComponent,
    AbbonamentiListComponent,
    CorsiListComponent,
    AlimentazioneListComponent,
    AdminDashboardComponent,
    ClienteDashboardComponent,
    AuthComponent,
    AccountComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: CredentialsInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: AuthErrorInterceptor, multi: true }
  ],
  bootstrap: [App]
})
export class AppModule {}

