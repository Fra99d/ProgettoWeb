package it.gym.backend_gym.dto;

import java.math.BigDecimal;

public record AbbonamentoAttivoDTO(
        Long id,
        Long abbonamentoId,
        Integer durata,
        BigDecimal prezzo,
        String startIso,
        String endIso
) {}
