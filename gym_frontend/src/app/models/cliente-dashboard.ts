import { AbbonamentoAttivo } from './abbonamento-attivo';
import { Corso } from './corso';
import { Prenotazione } from './prenotazione';
import { RecensioneCorso } from './recensione-corso';
import { RecensioneDieta } from './recensione-dieta';

export interface ClienteDashboard {
  abbonamentoAttivo: AbbonamentoAttivo | null;
  corsi: Corso[];
  prenotazioni: Prenotazione[];
  recensioniCorsi: RecensioneCorso[];
  recensioniDiete: RecensioneDieta[];
}
