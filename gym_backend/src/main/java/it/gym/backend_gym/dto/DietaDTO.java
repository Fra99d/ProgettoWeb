package it.gym.backend_gym.dto;

import java.util.List;

public record DietaDTO(
        Long id,
        String nome,
        String descrizione,
        String appuntamento,
        String fotoUrl,
        List<RecensioneDietaDTO> recensioni
) {}
