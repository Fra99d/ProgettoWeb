package it.gym.backend_gym.entity;

public class Dieta {
    private Long id;
    private String nome;
    private String descrizione;
    private String appuntamento;
    private String fotoUrl;

    public Dieta() {}
    public Dieta(String nome, String descrizione, String appuntamento, String fotoUrl) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.appuntamento = appuntamento;
        this.fotoUrl = fotoUrl;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescrizione() { return descrizione; }
    public String getAppuntamento() { return appuntamento; }
    public String getFotoUrl() { return fotoUrl; }
    
    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public void setAppuntamento(String appuntamento) { this.appuntamento = appuntamento; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}

