import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-account',
  standalone: false,
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit {
  email = '';
  newEmail = '';
  password = '';
  password2 = '';
  message = '';
  error = '';
  loading = false;
  showPassword = false;
  showPassword2 = false;

  constructor(private auth: AuthService) {}

  ngOnInit(): void {
    this.auth.ensureLoaded().subscribe((user) => {
      if (user) {
        this.email = user.email;
        this.newEmail = user.email;
      }
    });
  }

  save() {
    this.message = '';
    this.error = '';

    const email = this.newEmail.trim().toLowerCase();
    const password = this.password;

    if (!email || !email.includes('@')) {
      this.error = 'Email non valida.';
      return;
    }

    if (password && password.length < 6) {
      this.error = 'Password troppo corta (min 6).';
      return;
    }

    if (password && password !== this.password2) {
      this.error = 'Le password non coincidono.';
      return;
    }

    if (!password && email === this.email) {
      this.error = 'Nessuna modifica da salvare.';
      return;
    }

    this.loading = true;
    this.auth.updateAccount(email, password || null).subscribe({
      next: (user) => {
        this.email = user.email;
        this.newEmail = user.email;
        this.password = '';
        this.password2 = '';
        this.message = 'Account aggiornato.';
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Errore durante aggiornamento account.';
        this.loading = false;
      }
    });
  }
}
