package it.gym.backend_gym.dto;

import java.util.List;

public record ClienteDashboardDTO(
        AbbonamentoAttivoDTO abbonamentoAttivo,
        List<CorsoDTO> corsi,
        List<PrenotazioneDTO> prenotazioni,
        List<RecensioneCorsoDTO> recensioniCorsi,
        List<RecensioneDietaDTO> recensioniDiete
) {}
