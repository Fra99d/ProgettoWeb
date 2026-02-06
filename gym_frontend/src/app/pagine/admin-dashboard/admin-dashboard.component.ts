import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';

import { CorsiService } from '../../services/corsi.service';
import { Corso } from '../../models/corso';

import { DieteService } from '../../services/diete.service';
import { Dieta } from '../../models/dieta';

import { AbbonamentiService } from '../../services/abbonamenti.service';
import { Abbonamento } from '../../models/abbonamento';
import { AuthService } from '../../services/auth.service';
import { ClientiService } from '../../services/clienti.service';
import { Cliente } from '../../models/cliente';
import { ClienteDashboard } from '../../models/cliente-dashboard';

@Component({
  selector: 'app-admin-dashboard',
  standalone: false,
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  // tabs
  activeTab: 'corsi' | 'diete' | 'abbonamenti' | 'clienti' = 'corsi';

  // services + streams
  private corsiService = inject(CorsiService);
  corsi$ = this.corsiService.corsi$;

  private dieteService = inject(DieteService);
  diete$ = this.dieteService.diete$;

  private abbonamentiService = inject(AbbonamentiService);
  abbonamenti$ = this.abbonamentiService.abbonamenti$;
  private clientiService = inject(ClientiService);
  clienti$ = this.clientiService.clienti$;
  private auth = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  // ===== CORSI form =====
  titolo = '';
  descrizione = '';
  lezione = ''; // nuovo campo (obbligatorio)
  editingId: number | null = null;

  // CORSI: l'admin inserisce solo il nome file (es: pugilato.png)
  fotoFile = '';
  private readonly corsiAssetsBase = '/assets/corsi/';

  // ===== DIETE form =====
  nomeDieta = '';
  descrizioneDieta = '';
  appuntamentoDieta = '';
  editingIdDieta: number | null = null;

  // DIETE: l'admin inserisce solo il nome file (es: durante.webp)
  fotoFileDieta = '';
  private readonly dieteAssetsBase = '/assets/dietologhi/';

  // ===== ABBONAMENTI form =====
  durata = 1;
  prezzo = 40;
  editingIdAbbonamento: number | null = null;

  message = '';
  error = '';
  private errorTimer: ReturnType<typeof setTimeout> | null = null;
  private messageTimer: ReturnType<typeof setTimeout> | null = null;

  // ===== CLIENTI =====
  selectedCliente: Cliente | null = null;
  clienteDashboard: ClienteDashboard = this.emptyDashboard();
  clienteEmail = '';
  clientePassword = '';
  clienteRuolo: 'ADMIN' | 'CLIENTE' = 'CLIENTE';
  clienteMessage = '';
  clienteError = '';
  clienteLoading = false;
  private emptyDashboard(): ClienteDashboard {
    return { abbonamentoAttivo: null, corsi: [], prenotazioni: [], recensioniCorsi: [], recensioniDiete: [] };
  }

  ngOnInit(): void {
    this.corsiService.load();
    this.dieteService.load();
    this.abbonamentiService.load();
    this.clientiService.load();
  }

  // -------- helpers ----------
  private resetAlerts() {
    this.message = '';
    this.error = '';
    this.clearErrorTimer();
    this.clearMessageTimer();
  }

  private handleError(err: any, fallbackMsg: string, conflictMsg: string) {
    if (err?.status === 409) {
      this.showError(conflictMsg);
    } else {
      const backendMsg =
        err?.error?.message ||
        (typeof err?.error === 'string' ? err.error : null);
      this.showError(backendMsg ? backendMsg : fallbackMsg);
    }
    this.scrollToAlerts();
  }

  private scrollToAlerts() {
    setTimeout(() => {
      const el = document.querySelector('.alerts');
      if (el) el.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }, 0);
  }

  private flushUi() {
    this.cdr.detectChanges();
  }

  private showError(msg: string) {
    this.error = msg;
    this.flushUi();
    this.scheduleErrorClear();
  }

  private scheduleErrorClear() {
    this.clearErrorTimer();
    this.errorTimer = setTimeout(() => {
      this.error = '';
      this.flushUi();
    }, 2000);
  }

  private clearErrorTimer() {
    if (this.errorTimer) {
      clearTimeout(this.errorTimer);
      this.errorTimer = null;
    }
  }

  private showMessage(msg: string) {
    this.message = msg;
    this.flushUi();
    this.scheduleMessageClear();
  }

  private scheduleMessageClear() {
    this.clearMessageTimer();
    this.messageTimer = setTimeout(() => {
      this.message = '';
      this.flushUi();
    }, 2000);
  }

  private clearMessageTimer() {
    if (this.messageTimer) {
      clearTimeout(this.messageTimer);
      this.messageTimer = null;
    }
  }
  // ===== CORSI =====
  startEdit(c: Corso) {
    this.activeTab = 'corsi';
    this.editingId = c.id;
    this.titolo = c.titolo;
    this.descrizione = c.descrizione;
    this.lezione = c.lezione ?? '';

    // se nel DB c'e' "/assets/corsi/pugilato.png" -> in input mostra "pugilato.png"
    const full = (c.fotoUrl ?? '').trim();
    this.fotoFile = full ? (full.split('/').pop() ?? '') : '';

    this.resetAlerts();
  }

  cancelEdit() {
    this.editingId = null;
    this.titolo = '';
    this.descrizione = '';
    this.lezione = '';
    this.fotoFile = '';
  }

  save() {
    this.message = '';
    this.error = '';

    const titolo = this.titolo.trim();
    const descrizione = this.descrizione.trim();
    const lezione = this.lezione.trim();

    if (!titolo || !descrizione) {
      this.showError('Titolo e descrizione sono obbligatori.');
      return;
    }

    // lezione obbligatoria
    if (!lezione) {
      this.showError("Lezione e' obbligatoria.");
      return;
    }

    const titoloNorm = titolo.toLowerCase();
    const duplicato = this.corsiService
      .getSnapshot()
      .some(c => c.titolo.trim().toLowerCase() === titoloNorm && c.id !== this.editingId);

    if (duplicato) {
      this.showError("Titolo gia' esistente. Scegli un titolo diverso.");
      return;
    }

    // costruisce automaticamente "/assets/corsi/<file>"
    const fileRaw = this.fotoFile.trim();
    const file = fileRaw ? (fileRaw.split('/').pop() ?? '') : '';
    if (!file) {
      this.showError('Foto obbligatoria (inserisci il nome file in /assets/corsi).');
      return;
    }
    const fotoUrl = this.corsiAssetsBase + file;

    const payload = { titolo, descrizione, lezione, fotoUrl };

    if (this.editingId === null) {
      this.corsiService.create(payload).subscribe({
        next: () => {
          this.showMessage('Corso aggiunto!');
          this.cancelEdit();
        },
        error: (err) => {
          if (err?.status === 409) this.showError("Titolo gia' esistente. Scegli un titolo diverso.");
          else if (err?.status === 400) this.showError(err?.error?.message ?? 'Dati non validi.');
          else this.showError('Errore durante aggiunta corso.');
        }
      });
    } else {
      this.corsiService.update(this.editingId, payload).subscribe({
        next: () => {
          this.showMessage('Corso modificato!');
          this.cancelEdit();
        },
        error: (err) => {
          if (err?.status === 409) this.showError("Titolo gia' esistente. Scegli un titolo diverso.");
          else if (err?.status === 400) this.showError(err?.error?.message ?? 'Dati non validi.');
          else this.showError('Errore durante modifica corso.');
        }
      });
    }
  }

  remove(id: number) {
    this.resetAlerts();

    this.corsiService.delete(id).subscribe({
      next: () => { this.showMessage('Corso eliminato!'); this.scrollToAlerts(); },
      error: (err) =>
        this.handleError(
          err,
          'Errore durante eliminazione corso.',
          'Errore durante eliminazione corso poiche iscritto da parte di un cliente.'
        )
    });
  }

  // ===== DIETE =====
  startEditDieta(d: Dieta) {
    this.activeTab = 'diete';
    this.editingIdDieta = d.id;
    this.nomeDieta = d.nome;
    this.descrizioneDieta = d.descrizione;
    this.appuntamentoDieta = (d.appuntamento ?? '').trim();

    // se nel DB c'e' "/assets/dietologhi/durante.webp" -> in input mostra "durante.webp"
    const full = (d.fotoUrl ?? '').trim();
    this.fotoFileDieta = full ? (full.split('/').pop() ?? '') : '';

    this.resetAlerts();
  }

  cancelEditDieta() {
    this.editingIdDieta = null;
    this.nomeDieta = '';
    this.descrizioneDieta = '';
    this.appuntamentoDieta = '';
    this.fotoFileDieta = '';
  }

  saveDieta() {
    this.message = '';
    this.error = '';

    const nome = this.nomeDieta.trim();
    const descrizione = this.descrizioneDieta.trim();
    const appuntamento = this.appuntamentoDieta.trim();

    if (!nome || !descrizione) {
      this.showError('Nome e descrizione dieta sono obbligatori.');
      return;
    }

    // appuntamento obbligatorio
    if (!appuntamento) {
      this.showError('Appuntamento obbligatorio.');
      return;
    }

    const nomeNorm = nome.toLowerCase();
    const duplicato = this.dieteService
      .getSnapshot()
      .some(d => d.nome.trim().toLowerCase() === nomeNorm && d.id !== this.editingIdDieta);

    if (duplicato) {
      this.showError("Nome dieta gia' esistente. Scegli un nome diverso.");
      return;
    }

    // foto obbligatoria: deve essere presente un file name
    const fileRaw = this.fotoFileDieta.trim();
    const file = fileRaw ? (fileRaw.split('/').pop() ?? '') : '';
    if (!file) {
      this.showError('Foto obbligatoria (inserisci il nome file in /assets/dietologhi).');
      return;
    }
    const fotoUrl = this.dieteAssetsBase + file;

    // include anche appuntamento
    const payload = { nome, descrizione, appuntamento, fotoUrl };

    if (this.editingIdDieta === null) {
      this.dieteService.create(payload).subscribe({
        next: () => {
          this.showMessage('Dieta aggiunta!');
          this.cancelEditDieta();
        },
        error: (err) => {
          if (err?.status === 409) this.showError("Nome dieta gia' esistente. Scegli un nome diverso.");
          else if (err?.status === 400) this.showError(err?.error?.message ?? 'Dati non validi.');
          else this.showError('Errore durante aggiunta dieta.');
        }
      });
    } else {
      this.dieteService.update(this.editingIdDieta, payload).subscribe({
        next: () => {
          this.showMessage('Dieta modificata!');
          this.cancelEditDieta();
        },
        error: (err) => {
          if (err?.status === 409) this.showError("Nome dieta gia' esistente. Scegli un nome diverso.");
          else if (err?.status === 400) this.showError(err?.error?.message ?? 'Dati non validi.');
          else this.showError('Errore durante modifica dieta.');
        }
      });
    }
  }

  removeDieta(id: number) {
    this.resetAlerts();

    this.dieteService.delete(id).subscribe({
      next: () => { this.showMessage('Dieta eliminata!'); this.scrollToAlerts(); },
      error: (err) =>
        this.handleError(
          err,
          'Errore durante eliminazione dieta.',
          'Errore durante eliminazione dieta poiche prenotata da parte di un cliente.'
        )
    });
  }

  // ===== ABBONAMENTI =====
  startEditAbbonamento(a: Abbonamento) {
    this.editingIdAbbonamento = a.id;
    this.durata = a.durata;
    this.prezzo = a.prezzo;

    this.message = '';
    this.error = '';
  }

  cancelEditAbbonamento() {
    this.editingIdAbbonamento = null;
    this.durata = 1;
    this.prezzo = 40;
  }

  saveAbbonamento() {
    this.message = '';
    this.error = '';

    const durata = Number(this.durata);
    const prezzo = Number(this.prezzo);

    if (!durata || durata <= 0) {
      this.showError('Durata non valida.');
      return;
    }
    if (!prezzo || prezzo <= 0) {
      this.showError('Prezzo non valido.');
      return;
    }

    const req = { durata, prezzo };

    if (this.editingIdAbbonamento === null) {
      this.abbonamentiService.create(req).subscribe({
        next: () => { this.showMessage('Abbonamento aggiunto!'); this.cancelEditAbbonamento(); },
        error: () => this.showError('Errore durante aggiunta abbonamento.')
      });
    } else {
      this.abbonamentiService.update(this.editingIdAbbonamento, req).subscribe({
        next: () => { this.showMessage('Abbonamento modificato!'); this.cancelEditAbbonamento(); },
        error: () => this.showError('Errore durante modifica abbonamento.')
      });
    }
  }

  removeAbbonamento(id: number) {
    this.resetAlerts();

    this.abbonamentiService.delete(id).subscribe({
      next: () => { this.showMessage('Abbonamento eliminato!'); this.scrollToAlerts(); },
      error: (err) =>
        this.handleError(
          err,
          'Errore durante eliminazione abbonamento.',
          'Errore durante eliminazione abbonamento poiche attivo da parte di un cliente.'
        )
    });
  }

  // ===== CLIENTI =====
  selectCliente(c: Cliente) {
    this.activeTab = 'clienti';
    this.selectedCliente = c;
    this.clienteDashboard = this.emptyDashboard();
    this.clienteEmail = c.email;
    this.clientePassword = '';
    this.clienteRuolo = c.ruolo;
    this.clienteMessage = '';
    this.clienteError = '';
    this.clienteLoading = true;
    this.flushUi();
    this.loadClienteDashboard(c.id);
  }

  clearClienteSelection() {
    this.selectedCliente = null;
    this.clienteDashboard = this.emptyDashboard();
    this.clienteEmail = '';
    this.clientePassword = '';
    this.clienteRuolo = 'CLIENTE';
    this.clienteMessage = '';
    this.clienteError = '';
    this.clienteLoading = false;
    this.flushUi();
  }

  private loadClienteDashboard(id: number) {
    this.clientiService.getDashboard(id).subscribe({
      next: (dash) => {
        this.clienteDashboard = dash;
        this.clienteLoading = false;
        this.flushUi();
      },
      error: (err) => {
        this.clienteError = err?.error?.message ?? 'Errore durante caricamento stato cliente.';
        this.clienteDashboard = this.emptyDashboard();
        this.clienteLoading = false;
        this.flushUi();
      }
    });
  }

  saveCliente() {
    if (!this.selectedCliente) return;
    this.clienteMessage = '';
    this.clienteError = '';

    const email = this.clienteEmail.trim().toLowerCase();
    const password = this.clientePassword;
    const ruolo = this.clienteRuolo;

    const payload: { email?: string; password?: string; ruolo?: string } = { ruolo };
    if (email) payload.email = email;
    if (password) payload.password = password;

    this.clientiService.update(this.selectedCliente.id, payload).subscribe({
      next: (updated) => {
        this.selectedCliente = updated;
        this.clienteMessage = 'Cliente aggiornato.';
        this.clientiService.load();
      },
      error: (err) => {
        this.clienteError = err?.error?.message ?? 'Errore durante aggiornamento cliente.';
      }
    });
  }

  removeClienteAbbonamento() {
    if (!this.selectedCliente) return;
    this.clienteLoading = true;
    this.clienteDashboard = this.emptyDashboard();
    this.flushUi();
    this.clientiService.removeAbbonamento(this.selectedCliente.id).subscribe({
      next: (dash) => {
        this.clienteDashboard = dash;
        this.clientiService.load();
        this.clienteLoading = false;
        this.flushUi();
      },
      error: (err) => {
        this.clienteError = err?.error?.message ?? 'Errore durante rimozione abbonamento.';
        this.clienteLoading = false;
        this.flushUi();
        this.loadClienteDashboard(this.selectedCliente!.id);
      }
    });
  }

  removeClienteCorso(corsoId: number) {
    if (!this.selectedCliente) return;
    const prev = this.clienteDashboard;
    this.clienteLoading = true;
    this.clienteDashboard = {
      ...prev,
      corsi: prev.corsi.filter((c) => c.id !== corsoId)
    };
    this.flushUi();
    this.clientiService.removeCorso(this.selectedCliente.id, corsoId).subscribe({
      next: (dash) => {
        this.clienteDashboard = dash;
        this.clienteLoading = false;
        this.flushUi();
      },
      error: (err) => {
        this.clienteError = err?.error?.message ?? 'Errore durante rimozione corso.';
        this.clienteLoading = false;
        this.flushUi();
        this.loadClienteDashboard(this.selectedCliente!.id);
      }
    });
  }

  removeClientePrenotazione(prenId: number) {
    if (!this.selectedCliente) return;
    const prev = this.clienteDashboard;
    this.clienteLoading = true;
    this.clienteDashboard = {
      ...prev,
      prenotazioni: prev.prenotazioni.filter((p) => p.id !== prenId)
    };
    this.flushUi();
    this.clientiService.removePrenotazione(this.selectedCliente.id, prenId).subscribe({
      next: (dash) => {
        this.clienteDashboard = dash;
        this.clienteLoading = false;
        this.flushUi();
      },
      error: (err) => {
        this.clienteError = err?.error?.message ?? 'Errore durante rimozione prenotazione.';
        this.clienteLoading = false;
        this.flushUi();
        this.loadClienteDashboard(this.selectedCliente!.id);
      }
    });
  }

  removeClienteRecensioneCorso(recensioneId: number) {
    if (!this.selectedCliente) return;
    const prev = this.clienteDashboard;
    this.clienteLoading = true;
    this.clienteDashboard = {
      ...prev,
      recensioniCorsi: prev.recensioniCorsi.filter((r) => r.id !== recensioneId)
    };
    this.flushUi();
    this.clientiService.removeRecensioneCorso(this.selectedCliente.id, recensioneId).subscribe({
      next: (dash) => {
        this.clienteDashboard = dash;
        this.clienteLoading = false;
        this.flushUi();
      },
      error: (err) => {
        this.clienteError = err?.error?.message ?? 'Errore durante rimozione recensione corso.';
        this.clienteLoading = false;
        this.flushUi();
        this.loadClienteDashboard(this.selectedCliente!.id);
      }
    });
  }

  removeClienteRecensioneDieta(recensioneId: number) {
    if (!this.selectedCliente) return;
    const prev = this.clienteDashboard;
    this.clienteLoading = true;
    this.clienteDashboard = {
      ...prev,
      recensioniDiete: prev.recensioniDiete.filter((r) => r.id !== recensioneId)
    };
    this.flushUi();
    this.clientiService.removeRecensioneDieta(this.selectedCliente.id, recensioneId).subscribe({
      next: (dash) => {
        this.clienteDashboard = dash;
        this.clienteLoading = false;
        this.flushUi();
      },
      error: (err) => {
        this.clienteError = err?.error?.message ?? 'Errore durante rimozione recensione dieta.';
        this.clienteLoading = false;
        this.flushUi();
        this.loadClienteDashboard(this.selectedCliente!.id);
      }
    });
  }

  deleteCliente(c: Cliente) {
    const confirmDelete = confirm(`Eliminare il cliente ${c.email}?`);
    if (!confirmDelete) return;

    this.clientiService.deleteCliente(c.id).subscribe({
      next: () => {
        if (this.selectedCliente?.id === c.id) {
          this.clearClienteSelection();
        }
        this.clientiService.load();
      },
      error: (err) => {
        this.clienteError = err?.error?.message ?? 'Errore durante eliminazione cliente.';
      }
    });
  }

  logout() {
    this.auth.logout().subscribe({
      next: () => this.router.navigate(['/auth']),
      error: () => this.router.navigate(['/auth'])
    });
  }
}














