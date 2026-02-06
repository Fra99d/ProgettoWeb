import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Corso } from '../models/corso';

type CorsoRequest = { titolo: string; descrizione: string; lezione: string; fotoUrl?: string | null };

@Injectable({ providedIn: 'root' })
export class CorsiService {
  private baseUrl = 'http://localhost:8080/api/corsi';

  private corsiSubject = new BehaviorSubject<Corso[]>([]);
  corsi$ = this.corsiSubject.asObservable();

  constructor(private http: HttpClient) {}

  /** Carica (o ricarica) dal server */
  load(): void {
    this.http.get<Corso[]>(this.baseUrl).subscribe({
      next: (data) => this.corsiSubject.next(data ?? []),
      error: () => this.corsiSubject.next([]),
    });
  }

  create(req: CorsoRequest): Observable<Corso> {
    return this.http.post<Corso>(this.baseUrl, req).pipe(
      tap((created) => {
        const next = [...this.corsiSubject.value, created].sort((a, b) => a.id - b.id);
        this.corsiSubject.next(next);
      })
    );
  }

  update(id: number, req: CorsoRequest): Observable<Corso> {
    return this.http.put<Corso>(`${this.baseUrl}/${id}`, req).pipe(
      tap((updated) => {
        const next = this.corsiSubject.value.map(c => (c.id === id ? updated : c));
        this.corsiSubject.next(next);
      })
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      tap(() => {
        const next = this.corsiSubject.value.filter(c => c.id !== id);
        this.corsiSubject.next(next);
      })
    );
  }

  getSnapshot(): Corso[] {
    return this.corsiSubject.value;
  }
}


