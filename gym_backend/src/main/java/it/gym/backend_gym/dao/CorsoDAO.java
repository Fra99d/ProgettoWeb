package it.gym.backend_gym.dao;

import it.gym.backend_gym.entity.Corso;
import java.util.List;

public interface CorsoDAO {
    List<Corso> findAll();
    Corso findById(long id);
    Corso create(Corso c);
    Corso update(Corso c);
    boolean existsById(long id);
    boolean existsByTitoloIgnoreCase(String titolo);
    boolean existsByTitoloIgnoreCaseAndIdNot(String titolo, long idNot);
    boolean existsInIscrizioni(long corsoId);
    void delete(long id);
}

