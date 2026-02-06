package it.gym.backend_gym.dto;

import java.math.BigDecimal;

public record AbbonamentoDTO(
        Long id,
        Integer durata,
        BigDecimal prezzo
) {}