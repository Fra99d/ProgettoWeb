export interface RecensioneCorso {
  id: number;
  corsoId: number;
  corsoTitolo?: string | null;
  utenteId: number;
  utenteEmail?: string | null;
  testo: string;
  createdIso: string;
  updatedIso: string;
}
