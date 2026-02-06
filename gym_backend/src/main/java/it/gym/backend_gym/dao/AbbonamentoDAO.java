package it.gym.backend_gym.dao;

import it.gym.backend_gym.entity.Abbonamento;
import java.util.List;

public interface AbbonamentoDAO {
    List<Abbonamento> findAll();
    Abbonamento findById(long id);
    Abbonamento create(Abbonamento a);
    Abbonamento update(Abbonamento a);
    boolean existsById(long id);
    boolean existsInAbbonamentiAttivi(long id);
    void delete(long id);
}


