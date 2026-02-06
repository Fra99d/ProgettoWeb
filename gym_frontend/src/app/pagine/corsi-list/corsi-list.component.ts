import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CorsiService } from '../../services/corsi.service';
import { Corso } from '../../models/corso';
import { ClienteStateService } from '../../services/cliente-state.service';

@Component({
  selector: 'app-corsi-list',
  standalone: false,
  templateUrl: './corsi-list.component.html',
  styleUrls: ['./corsi-list.component.css']
})
export class CorsiListComponent implements OnInit {
  private corsiService = inject(CorsiService);
  private clienteState = inject(ClienteStateService);
  private router = inject(Router);

  corsi$ = this.corsiService.corsi$;
  ngOnInit(): void {
    this.corsiService.load();
    this.clienteState.ensureLoaded().subscribe(() => {
      if (!this.clienteState.isAbbonato()) {
        this.router.navigate(['/abbonamenti']);
      }
    });
  }

  heroBg(c: Corso): string {
    const url = (c.fotoUrl && c.fotoUrl.trim()) ? c.fotoUrl.trim() : '/assets/bg.png';
    return `linear-gradient(to left, rgba(0,0,0,.88), rgba(0,0,0,.10)),
            radial-gradient(600px 300px at 70% 20%, rgba(242,194,0,.18), transparent 60%),
            url('${url}')`;
  }

  iscriviti(c: Corso) {
    if (!this.clienteState.isAbbonato()) {
      this.router.navigate(['/abbonamenti']);
      return;
    }
    this.clienteState.iscriviCorso(c).subscribe({
      next: () => this.router.navigate(['/cliente-dashboard']),
      error: () => this.router.navigate(['/cliente-dashboard'])
    });
  }

}




