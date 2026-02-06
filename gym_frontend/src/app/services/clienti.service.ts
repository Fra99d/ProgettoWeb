import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, switchMap, tap } from 'rxjs';
import { Cliente } from '../models/cliente';
import { ClienteDashboard } from '../models/cliente-dashboard';

@Injectable({ providedIn: 'root' })
export class ClientiService {
  private baseUrl = 'http://localhost:8080/api/admin/clienti';

  private clientiSubject = new BehaviorSubject<Cliente[]>([]);
  clienti$ = this.clientiSubject.asObservable();

  constructor(private http: HttpClient) {}

  load(): void {
    this.http.get<Cliente[]>(this.baseUrl).subscribe({
      next: (data) => this.clientiSubject.next(data ?? []),
      error: () => this.clientiSubject.next([])
    });
  }

  update(id: number, payload: { email?: string; password?: string; ruolo?: string }): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.baseUrl}/${id}`, payload).pipe(
      tap((updated) => {
        const next = this.clientiSubject.value.map((c) => (c.id === id ? updated : c));
        this.clientiSubject.next(next);
      })
    );
  }

  getDashboard(id: number): Observable<ClienteDashboard> {
    return this.http.get<ClienteDashboard>(`${this.baseUrl}/${id}/dashboard`);
  }

  removeAbbonamento(id: number): Observable<ClienteDashboard> {
    return this.http
      .delete<void>(`${this.baseUrl}/${id}/abbonamento`)
      .pipe(switchMap(() => this.getDashboard(id)));
  }

  removeCorso(id: number, corsoId: number): Observable<ClienteDashboard> {
    return this.http
      .delete<void>(`${this.baseUrl}/${id}/corsi/${corsoId}`)
      .pipe(switchMap(() => this.getDashboard(id)));
  }

  removePrenotazione(id: number, prenId: number): Observable<ClienteDashboard> {
    return this.http
      .delete<void>(`${this.baseUrl}/${id}/prenotazioni/${prenId}`)
      .pipe(switchMap(() => this.getDashboard(id)));
  }

  removeRecensioneCorso(id: number, recensioneId: number): Observable<ClienteDashboard> {
    return this.http.delete<ClienteDashboard>(`${this.baseUrl}/${id}/recensioni/corsi/${recensioneId}`);
  }

  removeRecensioneDieta(id: number, recensioneId: number): Observable<ClienteDashboard> {
    return this.http.delete<ClienteDashboard>(`${this.baseUrl}/${id}/recensioni/diete/${recensioneId}`);
  }

  deleteCliente(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      tap(() => {
        const next = this.clientiSubject.value.filter((c) => c.id !== id);
        this.clientiSubject.next(next);
      })
    );
  }
}
