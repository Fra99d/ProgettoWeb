import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, finalize, map, of, switchMap, take, tap } from 'rxjs';
import { AuthUser } from '../models/auth-user';
import { ClienteStateService } from './cliente-state.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth';
  private userSubject = new BehaviorSubject<AuthUser | null>(null);
  user$ = this.userSubject.asObservable();

  private initialized = false;
  private lastMeAt = 0;
  private readonly meCacheMs = 30_000;
  private csrfReady = false;
  private csrfRequest$: Observable<void> | null = null;

  constructor(private http: HttpClient, private clienteState: ClienteStateService) {}

  ensureLoaded(): Observable<AuthUser | null> {
    const now = Date.now();
    if (this.initialized && now - this.lastMeAt < this.meCacheMs) {
      return this.user$.pipe(take(1));
    }
    this.initialized = true;
    return this.ensureCsrf().pipe(
      catchError(() => of(void 0)),
      switchMap(() => this.me())
    );
  }

  me(): Observable<AuthUser | null> {
    return this.http.get<AuthUser>(`${this.baseUrl}/me`).pipe(
      tap((user) => this.setUser(user)),
      catchError(() => {
        this.setUser(null);
        return of(null);
      }),
      finalize(() => {
        this.lastMeAt = Date.now();
      })
    );
  }

  login(email: string, password: string): Observable<AuthUser> {
    const token = btoa(`${email}:${password}`);
    const headers = new HttpHeaders({ Authorization: `Basic ${token}` });

    return this.ensureCsrf().pipe(
      switchMap(() =>
        this.http.post<AuthUser>(`${this.baseUrl}/login`, {}, { headers })
      ),
      tap((user) => {
        this.setUser(user);
        this.lastMeAt = Date.now();
      }),
      switchMap((user) =>
        this.forceCsrfRefresh().pipe(
          catchError(() => of(void 0)),
          map(() => user)
        )
      )
    );
  }

  register(email: string, password: string): Observable<AuthUser> {
    return this.ensureCsrf().pipe(
      switchMap(() =>
        this.http.post<AuthUser>(`${this.baseUrl}/register`, { email, password })
      )
    );
  }

  logout(): Observable<void> {
    return this.ensureCsrf().pipe(
      switchMap(() => this.http.post<void>(`${this.baseUrl}/logout`, {})),
      finalize(() => {
        this.setUser(null);
        this.csrfReady = false;
        this.lastMeAt = Date.now();
      })
    );
  }

  updateAccount(email: string, password: string | null): Observable<AuthUser> {
    const payload: { email?: string; password?: string } = { email };
    if (password) payload.password = password;
    return this.ensureCsrf().pipe(
      switchMap(() =>
        this.http.put<AuthUser>('http://localhost:8080/api/cliente/account', payload)
      ),
      tap((user) => {
        this.setUser(user);
        this.lastMeAt = Date.now();
      })
    );
  }

  handleUnauthorized(): void {
    this.setUser(null);
    this.lastMeAt = Date.now();
  }

  get snapshot(): AuthUser | null {
    return this.userSubject.value;
  }

  isLoggedIn(): boolean {
    return !!this.userSubject.value;
  }

  isAdmin(): boolean {
    return this.userSubject.value?.ruolo === 'ADMIN';
  }

  private setUser(user: AuthUser | null): void {
    this.userSubject.next(user);
    this.clienteState.setUser(user);
  }

  private ensureCsrf(): Observable<void> {
    if (this.csrfReady && this.hasCsrfCookie()) {
      return of(void 0);
    }
    if (this.csrfRequest$) {
      return this.csrfRequest$;
    }

    this.csrfRequest$ = this.http
      .get<{ token: string }>(`${this.baseUrl}/csrf`)
      .pipe(
        tap(() => {
          this.csrfReady = this.hasCsrfCookie();
        }),
        map(() => void 0),
        finalize(() => {
          this.csrfRequest$ = null;
        })
      );

    return this.csrfRequest$;
  }

  private forceCsrfRefresh(): Observable<void> {
    this.csrfReady = false;
    return this.ensureCsrf();
  }

  private hasCsrfCookie(): boolean {
    return /(?:^|; )XSRF-TOKEN=/.test(document.cookie);
  }

}
