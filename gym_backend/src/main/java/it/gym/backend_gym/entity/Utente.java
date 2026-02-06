package it.gym.backend_gym.entity;

public class Utente {
    private Long id;
    private String email;
    private String passwordHash;
    private Ruolo ruolo;

    public Utente() {}

    public Utente(String email, String passwordHash, Ruolo ruolo) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.ruolo = ruolo;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Ruolo getRuolo() { return ruolo; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRuolo(Ruolo ruolo) { this.ruolo = ruolo; }
}
