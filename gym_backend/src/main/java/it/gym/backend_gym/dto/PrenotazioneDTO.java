package it.gym.backend_gym.dto;

public record PrenotazioneDTO(
        Long id,
        DietaDTO dieta,
        String createdIso
) {}
