package it.gym.backend_gym.dao;

import it.gym.backend_gym.entity.Dieta;
import java.util.List;

public interface DietaDAO {
    List<Dieta> findAll();
    Dieta findById(long id);
    Dieta create(Dieta d);
    Dieta update(Dieta d);
    boolean existsById(long id);
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, long idNot);
    boolean existsInPrenotazioni(long dietaId);
    void delete(long id);
}

