import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AdminDashboardComponent } from './pagine/admin-dashboard/admin-dashboard.component';
import { CorsiListComponent } from './pagine/corsi-list/corsi-list.component';
import { AlimentazioneListComponent } from './pagine/alimentazione-list/alimentazione-list.component';
import { AbbonamentiListComponent } from './pagine/abbonamenti-list/abbonamenti-list.component';
import { ClienteDashboardComponent } from './pagine/cliente-dashboard/cliente-dashboard.component';
import { AuthComponent } from './pagine/auth/auth.component';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';
import { GuestGuard } from './guards/guest.guard';
import { AccountComponent } from './pagine/account/account.component';


const routes: Routes = [
  { path: '', redirectTo: 'auth', pathMatch: 'full' },

  { path: 'auth', component: AuthComponent, canActivate: [GuestGuard] },
  { path: 'cliente-dashboard', component: ClienteDashboardComponent, canActivate: [AuthGuard] },
  { path: 'account', component: AccountComponent, canActivate: [AuthGuard] },
  { path: 'abbonamenti', component: AbbonamentiListComponent, canActivate: [AuthGuard] },
  { path: 'corsi', component: CorsiListComponent, canActivate: [AuthGuard] },
  { path: 'alimentazione', component: AlimentazioneListComponent, canActivate: [AuthGuard] },
  { path: 'admin', component: AdminDashboardComponent, canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMIN' } },
  

  { path: '**', redirectTo: 'auth' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

