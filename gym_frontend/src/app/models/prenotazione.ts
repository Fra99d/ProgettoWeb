import { Dieta } from './dieta';

export interface Prenotazione {
  id: number;
  dieta: Dieta;
  createdIso: string;
}
