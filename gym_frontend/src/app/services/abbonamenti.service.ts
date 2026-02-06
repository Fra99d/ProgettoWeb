import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Abbonamento } from '../models/abbonamento';

type AbbonamentoRequest = {
  durata: number;
  prezzo: number;
};

@Injectable({ providedIn: 'root' })
export class AbbonamentiService {
  private baseUrl = 'http://localhost:8080/api/abbonamenti';

  private abbonamentiSubject = new BehaviorSubject<Abbonamento[]>([]);
  abbonamenti$ = this.abbonamentiSubject.asObservable();

  constructor(private http: HttpClient) {}

  load(): void {
    this.http.get<Abbonamento[]>(this.baseUrl).subscribe({
      next: (data) => this.abbonamentiSubject.next(data ?? []),
      error: () => this.abbonamentiSubject.next([]),
    });
  }

  getSnapshot(): Abbonamento[] {
    return this.abbonamentiSubject.value;
  }

  create(req: AbbonamentoRequest): Observable<Abbonamento> {
    return this.http.post<Abbonamento>(this.baseUrl, req).pipe(
      tap((created) => {
        const next = [...this.abbonamentiSubject.value, created].sort((a, b) => a.id - b.id);
        this.abbonamentiSubject.next(next);
      })
    );
  }

  update(id: number, req: AbbonamentoRequest): Observable<Abbonamento> {
    return this.http.put<Abbonamento>(`${this.baseUrl}/${id}`, req).pipe(
      tap((updated) => {
        const next = this.abbonamentiSubject.value.map(a => (a.id === id ? updated : a));
        this.abbonamentiSubject.next(next);
      })
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      tap(() => {
        const next = this.abbonamentiSubject.value.filter(a => a.id !== id);
        this.abbonamentiSubject.next(next);
      })
    );
  }
}
