package it.gym.backend_gym.dao;

import it.gym.backend_gym.entity.RecensioneDieta;

import java.util.List;

public interface RecensioneDietaDAO {
    List<RecensioneDieta> findByDietaId(long dietaId);
    List<RecensioneDieta> findByUtenteId(long utenteId);
    RecensioneDieta findByUtenteIdAndDietaId(long utenteId, long dietaId);
    RecensioneDieta findById(long id);
    RecensioneDieta create(RecensioneDieta recensione);
    RecensioneDieta update(RecensioneDieta recensione);
    void deleteById(long id);
    void deleteByUtenteIdAndDietaId(long utenteId, long dietaId);
}
