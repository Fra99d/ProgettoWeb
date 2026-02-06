import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { map, Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class GuestGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.auth.ensureLoaded().pipe(
      map((user) => {
        if (!user) return true;
        const target = user.ruolo === 'ADMIN' ? '/admin' : '/cliente-dashboard';
        this.router.navigate([target]);
        return false;
      })
    );
  }
}
