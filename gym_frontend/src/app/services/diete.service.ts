import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Dieta } from '../models/dieta';

type DietaRequest = { nome: string; descrizione: string; appuntamento: string;  fotoUrl?: string | null  };

@Injectable({ providedIn: 'root' })
export class DieteService {
  private baseUrl = 'http://localhost:8080/api/diete';

  private dieteSubject = new BehaviorSubject<Dieta[]>([]);
  diete$ = this.dieteSubject.asObservable();

  constructor(private http: HttpClient) {}

  load(): void {
    this.http.get<Dieta[]>(this.baseUrl).subscribe({
      next: (data) => this.dieteSubject.next(data ?? []),
      error: () => this.dieteSubject.next([]),
    });
  }

  create(req: DietaRequest): Observable<Dieta> {
    return this.http.post<Dieta>(this.baseUrl, req).pipe(
      tap((created) => {
        const next = [...this.dieteSubject.value, created].sort((a, b) => a.id - b.id);
        this.dieteSubject.next(next);
      })
    );
  }

  update(id: number, req: DietaRequest): Observable<Dieta> {
    return this.http.put<Dieta>(`${this.baseUrl}/${id}`, req).pipe(
      tap((updated) => {
        const next = this.dieteSubject.value.map(d => (d.id === id ? updated : d));
        this.dieteSubject.next(next);
      })
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      tap(() => {
        const next = this.dieteSubject.value.filter(d => d.id !== id);
        this.dieteSubject.next(next);
      })
    );
  }
  getSnapshot(): Dieta[] {
    return this.dieteSubject.value;
  }
}
