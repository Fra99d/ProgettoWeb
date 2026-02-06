import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ClienteStateService } from '../../services/cliente-state.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnDestroy {
  isAbbonato = false;
  private sub: Subscription;

  constructor(
    private auth: AuthService,
    private router: Router,
    private clienteState: ClienteStateService
  ) {
    this.sub = this.clienteState.state$.subscribe(() => {
      this.isAbbonato = this.clienteState.isAbbonato();
    });
  }

  get disableProtectedLinks(): boolean {
    return !this.auth.isAdmin() && !this.isAbbonato;
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  logout() {
    this.auth.logout().subscribe({
      next: () => this.router.navigate(['/auth']),
      error: () => this.router.navigate(['/auth'])
    });
  }

  blockIfDisabled(event: Event) {
    if (this.disableProtectedLinks) {
      event.preventDefault();
      event.stopPropagation();
    }
  }
}
