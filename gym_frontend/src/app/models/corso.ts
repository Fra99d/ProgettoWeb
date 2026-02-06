import { RecensioneCorso } from './recensione-corso';

export interface Corso {
  id: number;
  titolo: string;
  descrizione: string;
  lezione: string;
  fotoUrl?: string | null;
  recensioni?: RecensioneCorso[];
}
