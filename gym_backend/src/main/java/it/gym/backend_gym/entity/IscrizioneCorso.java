package it.gym.backend_gym.entity;

import java.time.LocalDateTime;

public class IscrizioneCorso {
    private Long id;
    private Utente utente;
    private Corso corso;
    private LocalDateTime createdAt;

    public IscrizioneCorso() {}

    public IscrizioneCorso(Utente utente, Corso corso, LocalDateTime createdAt) {
        this.utente = utente;
        this.corso = corso;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Utente getUtente() { return utente; }
    public Corso getCorso() { return corso; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public void setCorso(Corso corso) { this.corso = corso; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
