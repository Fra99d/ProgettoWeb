import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, of, switchMap, take, tap } from 'rxjs';
import { Abbonamento } from '../models/abbonamento';
import { Corso } from '../models/corso';
import { Dieta } from '../models/dieta';
import { AuthUser } from '../models/auth-user';
import { ClienteDashboard } from '../models/cliente-dashboard';

@Injectable({ providedIn: 'root' })
export class ClienteStateService {
  private baseUrl = 'http://localhost:8080/api/cliente';
  private stateSubject = new BehaviorSubject<ClienteDashboard>(this.emptyState());
  state$ = this.stateSubject.asObservable();

  private currentUserId: number | null = null;
  private loadedUserId: number | null = null;

  constructor(private http: HttpClient) {}

  get snapshot(): ClienteDashboard {
    return this.stateSubject.value;
  }

  setUser(user: AuthUser | null): void {
    this.currentUserId = user ? user.id : null;
    this.loadedUserId = null;
    this.stateSubject.next(this.emptyState());
    if (user) {
      this.load().subscribe();
    }
  }

  ensureLoaded(): Observable<ClienteDashboard> {
    if (this.currentUserId && this.loadedUserId === this.currentUserId) {
      return this.state$.pipe(take(1));
    }
    return this.load();
  }

  load(): Observable<ClienteDashboard> {
    return this.http.get<ClienteDashboard>(`${this.baseUrl}/dashboard`).pipe(
      tap((data) => {
        this.loadedUserId = this.currentUserId;
        this.setState(data ?? this.emptyState());
      }),
      catchError(() => {
        this.loadedUserId = null;
        this.setState(this.emptyState());
        return of(this.emptyState());
      })
    );
  }

  isAbbonato(): boolean {
    return !!this.snapshot.abbonamentoAttivo;
  }

  attivaAbbonamento(abbonamento: Abbonamento): Observable<ClienteDashboard> {
    return this.http
      .post<ClienteDashboard>(`${this.baseUrl}/abbonamento`, { abbonamentoId: abbonamento.id })
      .pipe(tap((data) => this.setState(data)));
  }

  rimuoviAbbonamento(): Observable<ClienteDashboard> {
    return this.http
      .delete<void>(`${this.baseUrl}/abbonamento`)
      .pipe(switchMap(() => this.load()));
  }

  iscriviCorso(corso: Corso): Observable<ClienteDashboard> {
    return this.http
      .post<ClienteDashboard>(`${this.baseUrl}/corsi/${corso.id}`, {})
      .pipe(tap((data) => this.setState(data)));
  }

  rimuoviCorso(corsoId: number): Observable<ClienteDashboard> {
    return this.http
      .delete<void>(`${this.baseUrl}/corsi/${corsoId}`)
      .pipe(switchMap(() => this.load()));
  }

  prenota(dieta: Dieta): Observable<ClienteDashboard> {
    return this.http
      .post<ClienteDashboard>(`${this.baseUrl}/prenotazioni/${dieta.id}`, {})
      .pipe(tap((data) => this.setState(data)));
  }

  annullaPrenotazione(prenId: number): Observable<ClienteDashboard> {
    return this.http
      .delete<void>(`${this.baseUrl}/prenotazioni/${prenId}`)
      .pipe(switchMap(() => this.load()));
  }

  creaRecensioneCorso(corsoId: number, testo: string): Observable<ClienteDashboard> {
    return this.http
      .post<ClienteDashboard>(`${this.baseUrl}/corsi/${corsoId}/recensione`, { testo })
      .pipe(tap((data) => this.setState(data)));
  }

  aggiornaRecensioneCorso(corsoId: number, testo: string): Observable<ClienteDashboard> {
    return this.http
      .put<ClienteDashboard>(`${this.baseUrl}/corsi/${corsoId}/recensione`, { testo })
      .pipe(tap((data) => this.setState(data)));
  }

  eliminaRecensioneCorso(corsoId: number): Observable<ClienteDashboard> {
    return this.http
      .delete<ClienteDashboard>(`${this.baseUrl}/corsi/${corsoId}/recensione`)
      .pipe(tap((data) => this.setState(data)));
  }

  creaRecensioneDieta(dietaId: number, testo: string): Observable<ClienteDashboard> {
    return this.http
      .post<ClienteDashboard>(`${this.baseUrl}/diete/${dietaId}/recensione`, { testo })
      .pipe(tap((data) => this.setState(data)));
  }

  aggiornaRecensioneDieta(dietaId: number, testo: string): Observable<ClienteDashboard> {
    return this.http
      .put<ClienteDashboard>(`${this.baseUrl}/diete/${dietaId}/recensione`, { testo })
      .pipe(tap((data) => this.setState(data)));
  }

  eliminaRecensioneDieta(dietaId: number): Observable<ClienteDashboard> {
    return this.http
      .delete<ClienteDashboard>(`${this.baseUrl}/diete/${dietaId}/recensione`)
      .pipe(tap((data) => this.setState(data)));
  }

  getGiorniRimasti(): number {
    const a = this.snapshot.abbonamentoAttivo;
    if (!a) return 0;

    const end = new Date(a.endIso);
    if (Number.isNaN(end.getTime())) return 0;
    const diff = end.getTime() - Date.now();
    const days = Math.ceil(diff / (1000 * 60 * 60 * 24));
    return days > 0 ? days : 0;
  }

  private setState(next: ClienteDashboard): void {
    this.stateSubject.next(next ?? this.emptyState());
  }

  private emptyState(): ClienteDashboard {
    return { abbonamentoAttivo: null, corsi: [], prenotazioni: [], recensioniCorsi: [], recensioniDiete: [] };
  }
}
