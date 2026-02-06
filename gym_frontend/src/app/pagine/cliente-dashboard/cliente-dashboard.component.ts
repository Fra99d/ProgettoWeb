import { Component, OnInit, inject } from '@angular/core';
import { combineLatest, map, timer } from 'rxjs';
import { ClienteStateService } from '../../services/cliente-state.service';
import { RecensioneCorso } from '../../models/recensione-corso';
import { RecensioneDieta } from '../../models/recensione-dieta';

@Component({
  selector: 'app-cliente-dashboard',
  standalone: false,
  templateUrl: './cliente-dashboard.component.html',
  styleUrls: ['./cliente-dashboard.component.css']
})
export class ClienteDashboardComponent implements OnInit {
  private state = inject(ClienteStateService);

  state$ = this.state.state$;
  recensioniCorsoOpen: Record<number, boolean> = {};
  recensioniDietaOpen: Record<number, boolean> = {};
  recensioniCorsoDraft: Record<number, string> = {};
  recensioniDietaDraft: Record<number, string> = {};

  // aggiorna il countdown ogni minuto
  giorniRimasti$ = combineLatest([this.state$, timer(0, 60_000)]).pipe(
    map(() => this.state.getGiorniRimasti())
  );

  ngOnInit(): void {
    this.state.ensureLoaded().subscribe();
  }

  heroBgFromUrl(url?: string | null): string {
    const safe = (url && url.trim()) ? url.trim() : '/assets/bg.png';
    return `linear-gradient(to left, rgba(0,0,0,.88), rgba(0,0,0,.10)),
            radial-gradient(600px 300px at 70% 20%, rgba(242,194,0,.18), transparent 60%),
            url('${safe}')`;
  }

  rimuoviCorso(id: number) {
    this.state.rimuoviCorso(id).subscribe();
  }

  annullaPrenotazione(id: number) {
    this.state.annullaPrenotazione(id).subscribe();
  }

  getRecensioneCorso(corsoId: number, recensioni: RecensioneCorso[]): RecensioneCorso | undefined {
    return recensioni.find(r => r.corsoId === corsoId);
  }

  getRecensioneDieta(dietaId: number, recensioni: RecensioneDieta[]): RecensioneDieta | undefined {
    return recensioni.find(r => r.dietaId === dietaId);
  }

  toggleRecensioneCorso(corsoId: number, existing?: RecensioneCorso) {
    const currentlyOpen = !!this.recensioniCorsoOpen[corsoId];
    if (currentlyOpen) {
      this.recensioniCorsoOpen[corsoId] = false;
      return;
    }
    this.recensioniCorsoOpen[corsoId] = true;
    this.recensioniCorsoDraft[corsoId] = existing?.testo ?? '';
  }

  toggleRecensioneDieta(dietaId: number, existing?: RecensioneDieta) {
    const currentlyOpen = !!this.recensioniDietaOpen[dietaId];
    if (currentlyOpen) {
      this.recensioniDietaOpen[dietaId] = false;
      return;
    }
    this.recensioniDietaOpen[dietaId] = true;
    this.recensioniDietaDraft[dietaId] = existing?.testo ?? '';
  }

  salvaRecensioneCorso(corsoId: number, existing?: RecensioneCorso) {
    const testo = (this.recensioniCorsoDraft[corsoId] ?? '').trim();
    if (!testo) return;
    const action$ = existing
      ? this.state.aggiornaRecensioneCorso(corsoId, testo)
      : this.state.creaRecensioneCorso(corsoId, testo);
    action$.subscribe(() => {
      this.recensioniCorsoOpen[corsoId] = false;
    });
  }

  salvaRecensioneDieta(dietaId: number, existing?: RecensioneDieta) {
    const testo = (this.recensioniDietaDraft[dietaId] ?? '').trim();
    if (!testo) return;
    const action$ = existing
      ? this.state.aggiornaRecensioneDieta(dietaId, testo)
      : this.state.creaRecensioneDieta(dietaId, testo);
    action$.subscribe(() => {
      this.recensioniDietaOpen[dietaId] = false;
    });
  }

  eliminaRecensioneCorso(corsoId: number) {
    this.state.eliminaRecensioneCorso(corsoId).subscribe(() => {
      this.recensioniCorsoOpen[corsoId] = false;
    });
  }

  eliminaRecensioneDieta(dietaId: number) {
    this.state.eliminaRecensioneDieta(dietaId).subscribe(() => {
      this.recensioniDietaOpen[dietaId] = false;
    });
  }

  isAbbonato(): boolean {
    return this.state.isAbbonato();
  }
}
