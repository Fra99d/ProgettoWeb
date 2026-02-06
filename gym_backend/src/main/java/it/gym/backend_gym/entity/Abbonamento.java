package it.gym.backend_gym.entity;

import java.math.BigDecimal;

public class Abbonamento {
    private Long id;
    private Integer durata;
    private BigDecimal prezzo;

    public Abbonamento() {}
    public Abbonamento(Integer durata, BigDecimal prezzo) {
        this.durata = durata;
        this.prezzo = prezzo;
    }

    public Long getId() { return id; }
    public Integer getDurata() { return durata; }
    public BigDecimal getPrezzo() { return prezzo; }

    public void setId(Long id) { this.id = id; }
    public void setDurata(Integer durata) { this.durata = durata; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }
}

