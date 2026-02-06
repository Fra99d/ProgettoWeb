package it.gym.backend_gym.entity;

import java.time.LocalDateTime;

public class RecensioneCorso {
    private Long id;
    private Utente utente;
    private Corso corso;
    private String testo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RecensioneCorso() {}

    public RecensioneCorso(Utente utente, Corso corso, String testo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.utente = utente;
        this.corso = corso;
        this.testo = testo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public Utente getUtente() { return utente; }
    public Corso getCorso() { return corso; }
    public String getTesto() { return testo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public void setCorso(Corso corso) { this.corso = corso; }
    public void setTesto(String testo) { this.testo = testo; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
