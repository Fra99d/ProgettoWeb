package it.gym.backend_gym.entity;

import java.time.LocalDateTime;

public class PrenotazioneDieta {
    private Long id;
    private Utente utente;
    private Dieta dieta;
    private LocalDateTime createdAt;

    public PrenotazioneDieta() {}

    public PrenotazioneDieta(Utente utente, Dieta dieta, LocalDateTime createdAt) {
        this.utente = utente;
        this.dieta = dieta;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Utente getUtente() { return utente; }
    public Dieta getDieta() { return dieta; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public void setDieta(Dieta dieta) { this.dieta = dieta; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
