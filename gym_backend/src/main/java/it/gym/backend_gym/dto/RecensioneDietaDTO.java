package it.gym.backend_gym.dto;

public record RecensioneDietaDTO(
        Long id,
        Long dietaId,
        String dietaNome,
        Long utenteId,
        String utenteEmail,
        String testo,
        String createdIso,
        String updatedIso
) {}
