import { ChangeDetectorRef, Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth',
  standalone: false,
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent {
  mode: 'login' | 'register' = 'login';
  email = '';
  password = '';
  password2 = '';
  showPassword = false;
  showPassword2 = false;
  message = '';
  error = '';
  loading = false;
  private errorTimer: ReturnType<typeof setTimeout> | null = null;
  private messageTimer: ReturnType<typeof setTimeout> | null = null;

  constructor(
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  setMode(mode: 'login' | 'register') {
    this.mode = mode;
    this.resetAlerts();
  }

  submit() {
    this.resetAlerts();

    const email = this.email.trim().toLowerCase();
    const password = this.password;

    if (!email || !email.includes('@')) {
      this.showError('Email non valida.');
      return;
    }
    if (password.length < 6) {
      this.showError('Password troppo corta (min 6).');
      return;
    }

    this.loading = true;

    if (this.mode === 'login') {
      this.auth.login(email, password).subscribe({
        next: (user) => this.navigateAfterLogin(user.ruolo),
        error: (err) => {
          this.showError(this.mapLoginError(err));
          this.loading = false;
        }
      });
      return;
    }

    if (password !== this.password2) {
      this.showError('Le password non coincidono.');
      this.loading = false;
      return;
    }

    this.auth.register(email, password).subscribe({
      next: () => {
        this.auth.login(email, password).subscribe({
          next: (user) => this.navigateAfterLogin(user.ruolo),
          error: () => {
            this.loading = false;
            this.showMessage('Registrazione completata. Effettua il login.');
            this.mode = 'login';
          }
        });
      },
      error: (err) => {
        this.showError(this.mapRegisterError(err));
        this.loading = false;
      }
    });
  }

  private mapLoginError(err: any): string {
    if (err?.status === 401) return 'Credenziali non valide.';
    const msg = err?.error?.message;
    return msg ? msg : 'Errore durante login.';
  }

  private mapRegisterError(err: any): string {
    if (err?.status === 409) return 'Email gia\' presente.';
    const msg = err?.error?.message;
    if (msg) return msg;
    if (err?.status === 400) return 'Dati non validi.';
    return 'Errore durante registrazione.';
  }

  private navigateAfterLogin(role: 'ADMIN' | 'CLIENTE') {
    const redirect = this.route.snapshot.queryParamMap.get('redirect');
    if (redirect && (role === 'ADMIN' || !redirect.startsWith('/admin'))) {
      this.router.navigateByUrl(redirect);
    } else {
      this.router.navigate([role === 'ADMIN' ? '/admin' : '/cliente-dashboard']);
    }
    this.loading = false;
  }

  private resetAlerts() {
    this.message = '';
    this.error = '';
    this.clearErrorTimer();
    this.clearMessageTimer();
  }

  private showError(msg: string) {
    this.error = msg;
    this.flushUi();
    this.scheduleErrorClear();
  }

  private showMessage(msg: string) {
    this.message = msg;
    this.flushUi();
    this.scheduleMessageClear();
  }

  private scheduleErrorClear() {
    this.clearErrorTimer();
    this.errorTimer = setTimeout(() => {
      this.error = '';
      this.flushUi();
    }, 2000);
  }

  private scheduleMessageClear() {
    this.clearMessageTimer();
    this.messageTimer = setTimeout(() => {
      this.message = '';
      this.flushUi();
    }, 2000);
  }

  private clearErrorTimer() {
    if (this.errorTimer) {
      clearTimeout(this.errorTimer);
      this.errorTimer = null;
    }
  }

  private clearMessageTimer() {
    if (this.messageTimer) {
      clearTimeout(this.messageTimer);
      this.messageTimer = null;
    }
  }

  private flushUi() {
    this.cdr.detectChanges();
  }
}
