package it.gym.backend_gym.dto;

import java.util.List;

public record CorsoDTO(
        Long id,
        String titolo,
        String descrizione,
        String lezione,
        String fotoUrl,
        List<RecensioneCorsoDTO> recensioni
) {}
