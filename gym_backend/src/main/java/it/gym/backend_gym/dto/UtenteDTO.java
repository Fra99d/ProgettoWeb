package it.gym.backend_gym.dto;

public record UtenteDTO(
        Long id,
        String email,
        String ruolo,
        boolean hasAbbonamento
) {}
