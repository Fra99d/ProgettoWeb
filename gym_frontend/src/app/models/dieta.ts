import { RecensioneDieta } from './recensione-dieta';

export interface Dieta {
  id: number;
  nome: string;
  descrizione: string;
  appuntamento: string;
  fotoUrl?: string | null;
  recensioni?: RecensioneDieta[];
}
