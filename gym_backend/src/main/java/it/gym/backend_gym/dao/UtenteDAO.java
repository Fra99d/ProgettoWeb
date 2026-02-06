package it.gym.backend_gym.dao;

import it.gym.backend_gym.entity.Utente;
import java.util.List;

public interface UtenteDAO {
    Utente findByEmail(String email);
    Utente findById(long id);
    Utente create(Utente u);
    Utente update(Utente u);
    void deleteById(long id);
    List<Utente> findAll();
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, long idNot);
}
