export interface Cliente {
  id: number;
  email: string;
  ruolo: 'ADMIN' | 'CLIENTE';
  hasAbbonamento: boolean;
}
