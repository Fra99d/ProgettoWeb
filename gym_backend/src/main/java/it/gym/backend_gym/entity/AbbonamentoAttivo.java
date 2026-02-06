package it.gym.backend_gym.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class AbbonamentoAttivo {
    private Long id;
    private Utente utente;
    private Abbonamento abbonamento;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public AbbonamentoAttivo() {}

    public AbbonamentoAttivo(Utente utente, Abbonamento abbonamento, LocalDateTime startDate, LocalDateTime endDate) {
        this.utente = utente;
        this.abbonamento = abbonamento;
        this.startDate = startDate;
        this.endDate = Objects.requireNonNull(endDate, "endDate");
    }

    public Long getId() { return id; }
    public Utente getUtente() { return utente; }
    public Abbonamento getAbbonamento() { return abbonamento; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }

    public void setId(Long id) { this.id = id; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public void setAbbonamento(Abbonamento abbonamento) { this.abbonamento = abbonamento; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = Objects.requireNonNull(endDate, "endDate"); }
}
