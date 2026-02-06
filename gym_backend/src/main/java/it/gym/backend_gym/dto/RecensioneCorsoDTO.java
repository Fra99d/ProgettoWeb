package it.gym.backend_gym.dto;

public record RecensioneCorsoDTO(
        Long id,
        Long corsoId,
        String corsoTitolo,
        Long utenteId,
        String utenteEmail,
        String testo,
        String createdIso,
        String updatedIso
) {}
