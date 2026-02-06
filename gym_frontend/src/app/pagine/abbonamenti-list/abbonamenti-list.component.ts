import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { combineLatest, map, timer } from 'rxjs';
import { AbbonamentiService } from '../../services/abbonamenti.service';
import { ClienteStateService } from '../../services/cliente-state.service';
import { Abbonamento } from '../../models/abbonamento';

@Component({
  selector: 'app-abbonamenti-list',
  standalone: false,
  templateUrl: './abbonamenti-list.component.html',
  styleUrls: ['./abbonamenti-list.component.css']
})
export class AbbonamentiListComponent implements OnInit {
  private abbonamentiService = inject(AbbonamentiService);
  private clienteState = inject(ClienteStateService);
  private router = inject(Router);

  abbonamenti$ = this.abbonamentiService.abbonamenti$;
  state$ = this.clienteState.state$;
  giorniRimasti$ = combineLatest([this.state$, timer(0, 60_000)]).pipe(
    map(() => this.clienteState.getGiorniRimasti())
  );

  message = '';
  error = '';

  ngOnInit(): void {
    this.clienteState.ensureLoaded().subscribe(() => {
      if (!this.clienteState.isAbbonato()) {
        this.abbonamentiService.load();
      }
    });
  }

  abbonati(a: Abbonamento) {
    this.message = '';
    this.error = '';
    if (this.clienteState.isAbbonato()) {
      this.error = 'Hai gia\' un abbonamento attivo.';
      return;
    }
    this.clienteState.attivaAbbonamento(a).subscribe({
      next: () => this.router.navigate(['/cliente-dashboard']),
      error: (err) => {
        if (err?.status === 409) {
          this.error = 'Hai gia\' un abbonamento attivo.';
        } else {
          this.error = err?.error?.message ?? 'Errore durante attivazione abbonamento.';
        }
      }
    });
  }
}

