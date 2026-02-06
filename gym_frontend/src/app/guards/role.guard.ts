import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { map, Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    const requiredRole = (route.data['role'] as string) || 'ADMIN';

    return this.auth.ensureLoaded().pipe(
      map((user) => {
        if (!user) {
          this.router.navigate(['/auth'], { queryParams: { redirect: state.url } });
          return false;
        }
        if (user.ruolo === requiredRole) return true;
        this.router.navigate(['/cliente-dashboard']);
        return false;
      })
    );
  }
}
