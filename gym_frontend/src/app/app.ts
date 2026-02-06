import { Component, OnInit, signal } from '@angular/core';
import { filter } from 'rxjs/operators';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from './services/auth.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
  styleUrls: ['./app.css']
})
export class App implements OnInit {
  protected readonly title = signal('gym_frontend');
  showNavbar = false;

  constructor(private router: Router, private auth: AuthService) {
    this.router.events
      .pipe(filter((e) => e instanceof NavigationEnd))
      .subscribe(() => this.updateNavbar());
  }

  ngOnInit(): void {
    this.auth.ensureLoaded().subscribe(() => this.updateNavbar());
    this.auth.user$.subscribe(() => this.updateNavbar());
  }

  private updateNavbar(): void {
    const url = this.router.url;
    const hide = url.startsWith('/admin') || url.startsWith('/auth');
    this.showNavbar = this.auth.isLoggedIn() && !hide;
  }
}
