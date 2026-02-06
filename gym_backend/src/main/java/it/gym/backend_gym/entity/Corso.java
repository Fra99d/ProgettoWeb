package it.gym.backend_gym.entity;

import java.util.List;

public class Corso {
    private Long id;
    private String titolo;
    private String descrizione;
    private String lezione;
    private String fotoUrl;
    private List<RecensioneCorso> recensioni;

    public Corso() {}

    public Corso(String titolo, String descrizione) {
        this.titolo = titolo;
        this.descrizione = descrizione;
    }

    public Corso(String titolo, String descrizione, String lezione) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.lezione = lezione;
    }

    public Corso(String titolo, String descrizione, String lezione, String fotoUrl) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.lezione = lezione;
        this.fotoUrl = fotoUrl;
    }

    public Long getId() { return id; }
    public String getTitolo() { return titolo; }
    public String getDescrizione() { return descrizione; }
    public String getLezione() { return lezione; }     
    public String getFotoUrl() { return fotoUrl; }
    public List<RecensioneCorso> getRecensioni() { return recensioni == null ? List.of() : recensioni; }

    public void setId(Long id) { this.id = id; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public void setLezione(String lezione) { this.lezione = lezione; } 
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public void setRecensioni(List<RecensioneCorso> recensioni) { this.recensioni = recensioni; }
}



