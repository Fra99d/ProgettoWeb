package it.gym.backend_gym.entity;

import java.time.LocalDateTime;

public class RecensioneDieta {
    private Long id;
    private Utente utente;
    private Dieta dieta;
    private String testo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RecensioneDieta() {}

    public RecensioneDieta(Utente utente, Dieta dieta, String testo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.utente = utente;
        this.dieta = dieta;
        this.testo = testo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public Utente getUtente() { return utente; }
    public Dieta getDieta() { return dieta; }
    public String getTesto() { return testo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public void setDieta(Dieta dieta) { this.dieta = dieta; }
    public void setTesto(String testo) { this.testo = testo; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
