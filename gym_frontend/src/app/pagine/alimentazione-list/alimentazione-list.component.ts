import { Component, OnInit, inject } from '@angular/core';
import { DieteService } from '../../services/diete.service';
import { Dieta } from '../../models/dieta';
import { Router } from '@angular/router';
import { ClienteStateService } from '../../services/cliente-state.service';

@Component({
  selector: 'app-alimentazione-list',
  standalone: false,
  templateUrl: './alimentazione-list.component.html',
  styleUrls: ['./alimentazione-list.component.css']
})
export class AlimentazioneListComponent implements OnInit {
  private dieteService = inject(DieteService);
  diete$ = this.dieteService.diete$;
  private clienteState = inject(ClienteStateService);
  private router = inject(Router);
  prenota(d: Dieta) {
    if (!this.clienteState.isAbbonato()) {
      this.router.navigate(['/abbonamenti']);
       return;
      }
    this.clienteState.prenota(d).subscribe({
      next: () => this.router.navigate(['/cliente-dashboard']),
      error: () => this.router.navigate(['/cliente-dashboard'])
    });
  }

  ngOnInit(): void {
    this.dieteService.load();
    this.clienteState.ensureLoaded().subscribe();
  }

  heroBg(d: Dieta): string {
    const url = (d.fotoUrl && d.fotoUrl.trim()) ? d.fotoUrl.trim() : '/assets/bg.png';
    return `linear-gradient(to left, rgba(0,0,0,.88), rgba(0,0,0,.10)),
            radial-gradient(600px 300px at 70% 20%, rgba(242,194,0,.18), transparent 60%),
            url('${url}')`;
  }

}


