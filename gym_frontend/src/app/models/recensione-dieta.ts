export interface RecensioneDieta {
  id: number;
  dietaId: number;
  dietaNome?: string | null;
  utenteId: number;
  utenteEmail?: string | null;
  testo: string;
  createdIso: string;
  updatedIso: string;
}
